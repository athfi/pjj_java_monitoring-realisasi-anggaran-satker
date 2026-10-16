package service;

import dao.PaguAkunDAO;
import dao.RealisasiDAO;
import exception.PaguExceededException;
import model.PaguAkun;
import model.Realisasi;
import util.DatabaseConnection;
import util.Validasi;
import util.PesanDatabase;
import dao.SatkerDAO;
import java.util.Objects;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Service untuk memproses transaksi anggaran secara aman (Thread-Safe & ACID).
 * Mengimplementasikan aturan transaksi Panduan v1.5 Bagian 8.3:
 * - Isolation READ_COMMITTED dan setAutoCommit(false)
 * - SELECT ... FOR UPDATE untuk mengunci baris pagu
 * - Validasi batas pagu sebelum commit, atau rollback bila over-budget
 */
public class BudgetService {
    private final PaguAkunDAO paguAkunDAO;
    private final RealisasiDAO realisasiDAO;
    private final PembukaKoneksi pembukaKoneksi;

    @FunctionalInterface
    public interface PembukaKoneksi { Connection buka() throws SQLException; }

    @FunctionalInterface
    private interface PekerjaanTransaksi { void jalankan(Connection conn) throws Exception; }

    public BudgetService() { this(DatabaseConnection::getConnection); }

    public BudgetService(PembukaKoneksi pembukaKoneksi) {
        this.paguAkunDAO = null;
        this.realisasiDAO = null;
        this.pembukaKoneksi = Objects.requireNonNull(pembukaKoneksi);
    }

    public BudgetService(PaguAkunDAO paguAkunDAO, RealisasiDAO realisasiDAO) {
        this.paguAkunDAO = Objects.requireNonNull(paguAkunDAO);
        this.realisasiDAO = Objects.requireNonNull(realisasiDAO);
        this.pembukaKoneksi = DatabaseConnection::getConnection;
    }

    /**
     * Mencatat transaksi realisasi baru dalam satu transaksi database yang aman.
     */
    public void catatRealisasi(Realisasi realisasi) throws PaguExceededException {
        Validasi.realisasi(realisasi);

        try (Connection conn = pembukaKoneksi.buka()) {
            PaguAkunDAO paguAkunDAO = this.paguAkunDAO != null ? this.paguAkunDAO : new PaguAkunDAO(conn);
            RealisasiDAO realisasiDAO = this.realisasiDAO != null ? this.realisasiDAO : new RealisasiDAO(conn);
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            conn.setAutoCommit(false);

            try {
                // 1. Kunci baris pagu dengan SELECT ... FOR UPDATE
                PaguAkun pagu = paguAkunDAO.getPaguForUpdate(realisasi.getPaguId(), conn);
                if (pagu == null) {
                    throw new IllegalArgumentException("Pagu akun dengan ID " + realisasi.getPaguId() + " tidak ditemukan.");
                }

                // 2. Hitung total realisasi yang sudah ada pada koneksi yang sama
                BigDecimal totalRealisasiLama = realisasiDAO.getTotalRealisasiByPaguId(realisasi.getPaguId(), conn);
                BigDecimal sisaPagu = pagu.getNilaiPagu().subtract(totalRealisasiLama);

                // 3. Validasi apakah realisasi baru melampaui sisa pagu
                if (realisasi.getNilaiRealisasi().compareTo(sisaPagu) > 0) {
                    NumberFormat rupiah = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));
                    throw new PaguExceededException(String.format(
                            "Realisasi sebesar %s ditolak! Melebihi sisa pagu yang tersedia (%s).",
                            rupiah.format(realisasi.getNilaiRealisasi()),
                            rupiah.format(sisaPagu)
                    ));
                }

                // 4. Simpan transaksi realisasi
                realisasiDAO.insert(realisasi, conn);

                // 5. Commit transaksi jika semua valid
                conn.commit();
            } catch (Exception e) {
                rollback(conn, e);
                throw PesanDatabase.terjemahkan(e);
            }
        } catch (SQLException e) {
            throw PesanDatabase.terjemahkan(e);
        }
    }

    /**
     * Mengubah nilai pagu secara aman dengan memeriksa total realisasi yang telah tercatat.
     */
    public void ubahNilaiPagu(long paguId, BigDecimal nilaiPaguBaru) {
        Validasi.id(paguId, "Pagu ID");
        Validasi.uang(nilaiPaguBaru, "Nilai pagu", true);

        try (Connection conn = pembukaKoneksi.buka()) {
            PaguAkunDAO paguAkunDAO = this.paguAkunDAO != null ? this.paguAkunDAO : new PaguAkunDAO(conn);
            RealisasiDAO realisasiDAO = this.realisasiDAO != null ? this.realisasiDAO : new RealisasiDAO(conn);
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            conn.setAutoCommit(false);

            try {
                // 1. Kunci baris pagu
                PaguAkun pagu = paguAkunDAO.getPaguForUpdate(paguId, conn);
                if (pagu == null) {
                    throw new IllegalArgumentException("Pagu akun dengan ID " + paguId + " tidak ditemukan.");
                }

                // 2. Hitung total realisasi yang sudah ada
                BigDecimal totalRealisasi = realisasiDAO.getTotalRealisasiByPaguId(paguId, conn);

                // 3. Aturan SDD 8.1 & Panduan: Nilai pagu baru tidak boleh lebih kecil dari total realisasi
                if (nilaiPaguBaru.compareTo(totalRealisasi) < 0) {
                    NumberFormat rupiah = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));
                    throw new IllegalArgumentException(String.format(
                            "Nilai pagu baru (%s) tidak boleh lebih kecil dari total realisasi yang sudah tercatat (%s).",
                            rupiah.format(nilaiPaguBaru),
                            rupiah.format(totalRealisasi)
                    ));
                }

                // 4. Update nilai pagu
                paguAkunDAO.updateNilaiPagu(paguId, nilaiPaguBaru, conn);

                // 5. Commit
                conn.commit();
            } catch (Exception e) {
                rollback(conn, e);
                throw PesanDatabase.terjemahkan(e);
            }
        } catch (SQLException e) {
            throw PesanDatabase.terjemahkan(e);
        }
    }

    public void tambahPaguAkun(PaguAkun paguAkun) {
        Validasi.pagu(paguAkun);
        jalankanTransaksi(conn -> {
            pastikanSatker(conn, paguAkun.getSatkerId());
            new PaguAkunDAO(conn).insert(paguAkun);
        });
    }

    public void ubahPaguAkun(PaguAkun paguAkun) {
        Validasi.pagu(paguAkun);
        Validasi.id(paguAkun.getId(), "Pagu ID");
        jalankanTransaksi(conn -> {
            PaguAkunDAO paguAkunDAO = new PaguAkunDAO(conn);
            RealisasiDAO realisasiDAO = new RealisasiDAO(conn);
            PaguAkun pagu = paguAkunDAO.getPaguForUpdate(paguAkun.getId(), conn);
            if (pagu == null) throw new IllegalArgumentException("Pagu tidak ditemukan.");
            BigDecimal totalRealisasi = realisasiDAO.getTotalRealisasiByPaguId(paguAkun.getId(), conn);
            boolean identitasBerubah = !Objects.equals(pagu.getSatkerId(), paguAkun.getSatkerId())
                    || !Objects.equals(pagu.getKodeAkun(), paguAkun.getKodeAkun())
                    || pagu.getTahunAnggaran() != paguAkun.getTahunAnggaran()
                    || !Objects.equals(pagu.getDeskripsiKegiatan(), paguAkun.getDeskripsiKegiatan());
            if (totalRealisasi.signum() > 0 && identitasBerubah)
                throw new IllegalArgumentException("Satker, akun, tahun dan kegiatan tidak boleh diubah karena pagu sudah memiliki realisasi.");
            if (paguAkun.getNilaiPagu().compareTo(totalRealisasi) < 0)
                throw new IllegalArgumentException("Pagu baru tidak boleh di bawah total realisasi Rp" + totalRealisasi.toPlainString());
            pastikanSatker(conn, paguAkun.getSatkerId());
            paguAkunDAO.update(paguAkun);
        });
    }

    public void hapusPaguAkun(long id) {
        Validasi.id(id, "Pagu ID");
        jalankanTransaksi(conn -> {
            PaguAkunDAO paguAkunDAO = new PaguAkunDAO(conn);
            if (paguAkunDAO.getPaguForUpdate(id, conn) == null) throw new IllegalArgumentException("Pagu tidak ditemukan.");
            if (!new RealisasiDAO(conn).findByPaguId(id).isEmpty())
                throw new IllegalArgumentException("Pagu tidak dapat dihapus karena memiliki realisasi.");
            paguAkunDAO.delete(id);
        });
    }

    private void jalankanTransaksi(PekerjaanTransaksi pekerjaan) {
        try (Connection conn = pembukaKoneksi.buka()) {
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            conn.setAutoCommit(false);
            try {
                pekerjaan.jalankan(conn);
                conn.commit();
            } catch (Exception e) {
                rollback(conn, e);
                throw PesanDatabase.terjemahkan(e);
            }
        } catch (SQLException e) { throw PesanDatabase.terjemahkan(e); }
    }

    private static void pastikanSatker(Connection conn, long id) {
        if (new SatkerDAO(conn).findById(id) == null) throw new IllegalArgumentException("Satker tidak ditemukan.");
    }

    private static void rollback(Connection conn, Exception e) {
        try { conn.rollback(); } catch (SQLException rollbackError) { e.addSuppressed(rollbackError); }
    }
}
