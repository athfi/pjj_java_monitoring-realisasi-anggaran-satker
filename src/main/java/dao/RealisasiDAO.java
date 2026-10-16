package dao;

import model.PaguAkun;
import model.Realisasi;
import model.Satker;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RealisasiDAO implements CrudRepository<Realisasi> {
    private final Connection connection;

    public RealisasiDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Realisasi> findAll() {
        List<Realisasi> list = new ArrayList<>();
        String sql = """
            SELECT r.id, r.pagu_id, r.nomor_sp2d, r.tanggal_realisasi, r.nilai_realisasi, r.keterangan,
                   p.satker_id, p.kode_akun, p.nama_akun, p.tahun_anggaran, p.deskripsi_kegiatan, p.nilai_pagu,
                   s.kode_satker, s.nama_satker
            FROM realisasi r
            LEFT JOIN pagu_akun p ON r.pagu_id = p.id
            LEFT JOIN satker s ON p.satker_id = s.id
            ORDER BY r.tanggal_realisasi DESC, r.id DESC
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToRealisasi(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal mengambil data Realisasi: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public Realisasi findById(long id) {
        String sql = """
            SELECT r.id, r.pagu_id, r.nomor_sp2d, r.tanggal_realisasi, r.nilai_realisasi, r.keterangan,
                   p.satker_id, p.kode_akun, p.nama_akun, p.tahun_anggaran, p.deskripsi_kegiatan, p.nilai_pagu,
                   s.kode_satker, s.nama_satker
            FROM realisasi r
            LEFT JOIN pagu_akun p ON r.pagu_id = p.id
            LEFT JOIN satker s ON p.satker_id = s.id
            WHERE r.id = ?
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRealisasi(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal mencari Realisasi ID " + id + ": " + e.getMessage(), e);
        }
        return null;
    }

    public List<Realisasi> findByPaguId(long paguId) {
        List<Realisasi> list = new ArrayList<>();
        String sql = """
            SELECT r.id, r.pagu_id, r.nomor_sp2d, r.tanggal_realisasi, r.nilai_realisasi, r.keterangan,
                   p.satker_id, p.kode_akun, p.nama_akun, p.tahun_anggaran, p.deskripsi_kegiatan, p.nilai_pagu,
                   s.kode_satker, s.nama_satker
            FROM realisasi r
            LEFT JOIN pagu_akun p ON r.pagu_id = p.id
            LEFT JOIN satker s ON p.satker_id = s.id
            WHERE r.pagu_id = ?
            ORDER BY r.tanggal_realisasi ASC, r.id ASC
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, paguId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToRealisasi(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal mengambil data Realisasi untuk Pagu ID " + paguId + ": " + e.getMessage(), e);
        }
        return list;
    }

    public Realisasi findByNomorSp2d(String nomorSp2d) {
        String sql = """
            SELECT r.id, r.pagu_id, r.nomor_sp2d, r.tanggal_realisasi, r.nilai_realisasi, r.keterangan,
                   p.satker_id, p.kode_akun, p.nama_akun, p.tahun_anggaran, p.deskripsi_kegiatan, p.nilai_pagu,
                   s.kode_satker, s.nama_satker
            FROM realisasi r
            LEFT JOIN pagu_akun p ON r.pagu_id = p.id
            LEFT JOIN satker s ON p.satker_id = s.id
            WHERE r.nomor_sp2d = ?
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, nomorSp2d);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRealisasi(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal mencari Realisasi nomor SP2D " + nomorSp2d + ": " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void insert(Realisasi data) {
        insert(data, this.connection);
    }

    /**
     * Menyimpan transaksi realisasi dengan koneksi tertentu (misal koneksi transaksi BudgetService).
     */
    public void insert(Realisasi data, Connection conn) {
        String sql = """
            INSERT INTO realisasi (pagu_id, nomor_sp2d, tanggal_realisasi, nilai_realisasi, keterangan)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, data.getPaguId());
            ps.setString(2, data.getNomorSp2d());
            ps.setDate(3, Date.valueOf(data.getTanggalRealisasi()));
            ps.setBigDecimal(4, data.getNilaiRealisasi());
            ps.setString(5, data.getKeterangan());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    data.setId(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal menambahkan Realisasi: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Realisasi data) {
        // Sesuai aturan bisnis SDD 4.2 & 8.3 & Panduan v1.5: Transaksi realisasi yang sudah tersimpan tidak dapat diubah
        throw new UnsupportedOperationException("Transaksi realisasi tidak dapat diubah.");
    }

    @Override
    public void delete(long id) {
        // Sesuai aturan bisnis SDD 4.2 & 8.3 & Panduan v1.5: Transaksi realisasi yang sudah tersimpan tidak dapat dihapus
        throw new UnsupportedOperationException("Transaksi realisasi tidak dapat dihapus.");
    }

    /**
     * Menghitung total akumulasi realisasi untuk pagu tertentu.
     */
    public BigDecimal getTotalRealisasiByPaguId(long paguId) {
        return getTotalRealisasiByPaguId(paguId, this.connection);
    }

    /**
     * Menghitung total realisasi untuk pagu tertentu pada koneksi transaksi yang sama.
     */
    public BigDecimal getTotalRealisasiByPaguId(long paguId, Connection conn) {
        String sql = "SELECT COALESCE(SUM(nilai_realisasi), 0) FROM realisasi WHERE pagu_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, paguId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal menghitung total realisasi untuk Pagu ID " + paguId + ": " + e.getMessage(), e);
        }
        return BigDecimal.ZERO;
    }

    private Realisasi mapResultSetToRealisasi(ResultSet rs) throws SQLException {
        Realisasi r = new Realisasi();
        r.setId(rs.getLong("id"));
        long paguId = rs.getLong("pagu_id");
        r.setPaguId(paguId);
        r.setNomorSp2d(rs.getString("nomor_sp2d"));

        Date tgl = rs.getDate("tanggal_realisasi");
        if (tgl != null) {
            r.setTanggalRealisasi(tgl.toLocalDate());
        }
        r.setNilaiRealisasi(rs.getBigDecimal("nilai_realisasi"));
        r.setKeterangan(rs.getString("keterangan"));

        // Map relasi PaguAkun & Satker jika tersedia dari kolom JOIN
        try {
            long satkerId = rs.getLong("satker_id");
            if (!rs.wasNull()) {
                PaguAkun p = new PaguAkun();
                p.setId(paguId);
                p.setSatkerId(satkerId);
                p.setKodeAkun(rs.getString("kode_akun"));
                p.setNamaAkun(rs.getString("nama_akun"));
                p.setTahunAnggaran(rs.getInt("tahun_anggaran"));
                p.setDeskripsiKegiatan(rs.getString("deskripsi_kegiatan"));
                p.setNilaiPagu(rs.getBigDecimal("nilai_pagu"));

                String kodeSatker = rs.getString("kode_satker");
                String namaSatker = rs.getString("nama_satker");
                if (kodeSatker != null || namaSatker != null) {
                    p.setSatker(new Satker(satkerId, kodeSatker, namaSatker));
                }
                r.setPaguAkun(p);
            }
        } catch (SQLException ignored) {
        }

        return r;
    }
}
