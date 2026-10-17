package dao;

import model.PaguAkun;
import model.Satker;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PaguAkunDAO implements CrudRepository<PaguAkun> {
    private final Connection connection;

    public PaguAkunDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<PaguAkun> findAll() {
        List<PaguAkun> list = new ArrayList<>();
        String sql = """
            SELECT p.id, p.satker_id, p.kode_akun, p.nama_akun, p.tahun_anggaran, p.deskripsi_kegiatan, p.nilai_pagu,
                   s.kode_satker, s.nama_satker
            FROM pagu_akun p
            LEFT JOIN satker s ON p.satker_id = s.id
            ORDER BY p.id ASC
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToPaguAkun(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal mengambil data Pagu Akun: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public PaguAkun findById(long id) {
        String sql = """
            SELECT p.id, p.satker_id, p.kode_akun, p.nama_akun, p.tahun_anggaran, p.deskripsi_kegiatan, p.nilai_pagu,
                   s.kode_satker, s.nama_satker
            FROM pagu_akun p
            LEFT JOIN satker s ON p.satker_id = s.id
            WHERE p.id = ?
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPaguAkun(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal mencari Pagu Akun ID " + id + ": " + e.getMessage(), e);
        }
        return null;
    }

    public List<PaguAkun> findBySatkerId(long satkerId) {
        List<PaguAkun> list = new ArrayList<>();
        String sql = """
            SELECT p.id, p.satker_id, p.kode_akun, p.nama_akun, p.tahun_anggaran, p.deskripsi_kegiatan, p.nilai_pagu,
                   s.kode_satker, s.nama_satker
            FROM pagu_akun p
            LEFT JOIN satker s ON p.satker_id = s.id
            WHERE p.satker_id = ?
            ORDER BY p.kode_akun ASC
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, satkerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToPaguAkun(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal mencari Pagu Akun Satker ID " + satkerId + ": " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public void insert(PaguAkun data) {
        String sql = """
            INSERT INTO pagu_akun (satker_id, kode_akun, nama_akun, tahun_anggaran, deskripsi_kegiatan, nilai_pagu)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, data.getSatkerId());
            ps.setString(2, data.getKodeAkun());
            ps.setString(3, data.getNamaAkun());
            ps.setInt(4, data.getTahunAnggaran());
            ps.setString(5, data.getDeskripsiKegiatan());
            ps.setBigDecimal(6, data.getNilaiPagu());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    data.setId(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal menambahkan Pagu Akun: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(PaguAkun data) {
        String sql = """
            UPDATE pagu_akun 
            SET satker_id = ?, kode_akun = ?, nama_akun = ?, tahun_anggaran = ?, deskripsi_kegiatan = ?, nilai_pagu = ?
            WHERE id = ?
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, data.getSatkerId());
            ps.setString(2, data.getKodeAkun());
            ps.setString(3, data.getNamaAkun());
            ps.setInt(4, data.getTahunAnggaran());
            ps.setString(5, data.getDeskripsiKegiatan());
            ps.setBigDecimal(6, data.getNilaiPagu());
            ps.setLong(7, data.getId());

            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new RuntimeException("Pagu Akun dengan ID " + data.getId() + " tidak ditemukan untuk diperbarui.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal mengubah Pagu Akun: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(long id) {
        String sql = "DELETE FROM pagu_akun WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            int deleted = ps.executeUpdate();
            if (deleted == 0) {
                throw new RuntimeException("Pagu Akun dengan ID " + id + " tidak ditemukan untuk dihapus.");
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 23503 || e.getMessage().toLowerCase().contains("foreign key") || e.getMessage().toLowerCase().contains("referensial")) {
                throw new RuntimeException("Pagu Akun tidak dapat dihapus karena sudah memiliki transaksi realisasi terkait.", e);
            }
            throw new RuntimeException("Gagal menghapus Pagu Akun: " + e.getMessage(), e);
        }
    }

    /**
     * Mengunci baris pagu_akun menggunakan SELECT ... FOR UPDATE (Petunjuk Panduan v1.5 Bagian 8.3).
     * Dijalankan pada koneksi transaksi yang sama untuk mencegah race condition.
     */
    public PaguAkun getPaguForUpdate(long paguId, Connection conn) throws SQLException {
        String sql = """
            SELECT id, satker_id, kode_akun, nama_akun, tahun_anggaran, deskripsi_kegiatan, nilai_pagu
            FROM pagu_akun
            WHERE id = ?
            FOR UPDATE
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, paguId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    PaguAkun pagu = new PaguAkun();
                    pagu.setId(rs.getLong("id"));
                    pagu.setSatkerId(rs.getLong("satker_id"));
                    pagu.setKodeAkun(rs.getString("kode_akun"));
                    pagu.setNamaAkun(rs.getString("nama_akun"));
                    pagu.setTahunAnggaran(rs.getInt("tahun_anggaran"));
                    pagu.setDeskripsiKegiatan(rs.getString("deskripsi_kegiatan"));
                    pagu.setNilaiPagu(rs.getBigDecimal("nilai_pagu"));
                    return pagu;
                }
            }
        }
        return null;
    }

    /**
     * Memperbarui nilai pagu menggunakan koneksi transaksi aman yang sedang memegang lock.
     */
    public void updateNilaiPagu(long paguId, BigDecimal nilaiBaru, Connection conn) throws SQLException {
        String sql = "UPDATE pagu_akun SET nilai_pagu = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, nilaiBaru);
            ps.setLong(2, paguId);
            ps.executeUpdate();
        }
    }

    private PaguAkun mapResultSetToPaguAkun(ResultSet rs) throws SQLException {
        PaguAkun pagu = new PaguAkun();
        pagu.setId(rs.getLong("id"));
        long satkerId = rs.getLong("satker_id");
        pagu.setSatkerId(satkerId);

        String kodeSatker = null;
        String namaSatker = null;
        try {
            kodeSatker = rs.getString("kode_satker");
            namaSatker = rs.getString("nama_satker");
        } catch (SQLException ignored) {
        }

        if (kodeSatker != null || namaSatker != null) {
            pagu.setSatker(new Satker(satkerId, kodeSatker, namaSatker));
        }

        pagu.setKodeAkun(rs.getString("kode_akun"));
        pagu.setNamaAkun(rs.getString("nama_akun"));
        pagu.setTahunAnggaran(rs.getInt("tahun_anggaran"));
        pagu.setDeskripsiKegiatan(rs.getString("deskripsi_kegiatan"));
        pagu.setNilaiPagu(rs.getBigDecimal("nilai_pagu"));
        return pagu;
    }
}
