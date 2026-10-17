package service;

import dao.PaguAkunDAO;
import dao.RealisasiDAO;
import exception.PaguExceededException;
import model.PaguAkun;
import model.Realisasi;
import util.DatabaseConnection;

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

    public BudgetService(PaguAkunDAO paguAkunDAO, RealisasiDAO realisasiDAO) {
        this.paguAkunDAO = paguAkunDAO;
        this.realisasiDAO = realisasiDAO;
    }

    /**
     * Mencatat transaksi realisasi baru dalam satu transaksi database yang aman.
     */
    public void catatRealisasi(Realisasi realisasi) throws PaguExceededException {
        if (realisasi == null) {
            throw new IllegalArgumentException("Data realisasi tidak boleh null.");
        }
        if (realisasi.getPaguId() == null) {
            throw new IllegalArgumentException("Pagu ID wajib diisi.");
        }
        if (realisasi.getNomorSp2d() == null || realisasi.getNomorSp2d().isBlank()) {
            throw new IllegalArgumentException("Nomor SP2D wajib diisi.");
        }
        if (realisasi.getNilaiRealisasi() == null || realisasi.getNilaiRealisasi().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Nilai realisasi harus lebih besar dari 0.");
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
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
                    conn.rollback();
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
                conn.rollback();
                if (e instanceof PaguExceededException) {
                    throw (PaguExceededException) e;
                }
                throw new RuntimeException("Gagal mencatat transaksi realisasi: " + e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Koneksi database gagal: " + e.getMessage(), e);
        }
    }

    /**
     * Mengubah nilai pagu secara aman dengan memeriksa total realisasi yang telah tercatat.
     */
    public void ubahNilaiPagu(long paguId, BigDecimal nilaiPaguBaru) {
        if (nilaiPaguBaru == null || nilaiPaguBaru.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Nilai pagu tidak boleh bernilai negatif.");
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
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
                    conn.rollback();
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
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Koneksi database gagal: " + e.getMessage(), e);
        }
    }
}
