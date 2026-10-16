package dao;

import model.Satker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SatkerDAO implements CrudRepository<Satker> {
    private final Connection connection;

    public SatkerDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Satker> findAll() {
        List<Satker> list = new ArrayList<>();
        String sql = "SELECT id, kode_satker, nama_satker FROM satker ORDER BY kode_satker ASC";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToSatker(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal mengambil data Satker: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public Satker findById(long id) {
        String sql = "SELECT id, kode_satker, nama_satker FROM satker WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSatker(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal mencari Satker ID " + id + ": " + e.getMessage(), e);
        }
        return null;
    }

    public Satker findByKode(String kodeSatker) {
        String sql = "SELECT id, kode_satker, nama_satker FROM satker WHERE kode_satker = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, kodeSatker);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSatker(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal mencari Satker Kode " + kodeSatker + ": " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void insert(Satker data) {
        String sql = "INSERT INTO satker (kode_satker, nama_satker) VALUES (?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, data.getKodeSatker());
            ps.setString(2, data.getNamaSatker());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    data.setId(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal menambahkan Satker: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Satker data) {
        String sql = "UPDATE satker SET kode_satker = ?, nama_satker = ? WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, data.getKodeSatker());
            ps.setString(2, data.getNamaSatker());
            ps.setLong(3, data.getId());

            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new RuntimeException("Satker dengan ID " + data.getId() + " tidak ditemukan untuk diperbarui.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal mengubah Satker: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(long id) {
        String sql = "DELETE FROM satker WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            int deleted = ps.executeUpdate();
            if (deleted == 0) {
                throw new RuntimeException("Satker dengan ID " + id + " tidak ditemukan untuk dihapus.");
            }
        } catch (SQLException e) {
            // Tangani foreign key constraint
            if (e.getErrorCode() == 23503 || e.getMessage().toLowerCase().contains("foreign key") || e.getMessage().toLowerCase().contains("referensial")) {
                throw new RuntimeException("Satker tidak dapat dihapus karena masih memiliki pagu anggaran terkait.", e);
            }
            throw new RuntimeException("Gagal menghapus Satker: " + e.getMessage(), e);
        }
    }

    private Satker mapResultSetToSatker(ResultSet rs) throws SQLException {
        return new Satker(
                rs.getLong("id"),
                rs.getString("kode_satker"),
                rs.getString("nama_satker")
        );
    }
}
