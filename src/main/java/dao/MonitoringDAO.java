package dao;

import model.MonitoringRow;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MonitoringDAO {
    private final Connection connection;

    public MonitoringDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Rekapitulasi realisasi anggaran per Satker untuk tahun tertentu (atau semua tahun jika tahun == 0).
     * Mencegah pagu dihitung ganda dengan melakukan agregasi realisasi terlebih dahulu di subquery.
     */
    public List<MonitoringRow> getRekapPerSatker(int tahun) {
        List<MonitoringRow> list = new ArrayList<>();
        String sql = """
            SELECT 
                s.kode_satker,
                s.nama_satker,
                p.tahun_anggaran,
                SUM(p.nilai_pagu) AS total_pagu,
                COALESCE(SUM(r.total_real), 0) AS total_realisasi
            FROM pagu_akun p
            JOIN satker s ON p.satker_id = s.id
            LEFT JOIN (
                SELECT pagu_id, SUM(nilai_realisasi) AS total_real
                FROM realisasi
                GROUP BY pagu_id
            ) r ON p.id = r.pagu_id
            WHERE (? = 0 OR p.tahun_anggaran = ?)
            GROUP BY s.id, s.kode_satker, s.nama_satker, p.tahun_anggaran
            ORDER BY s.kode_satker ASC
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, tahun);
            ps.setInt(2, tahun);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new MonitoringRow(
                            rs.getString("kode_satker"),
                            rs.getString("nama_satker"),
                            rs.getInt("tahun_anggaran"),
                            rs.getBigDecimal("total_pagu"),
                            rs.getBigDecimal("total_realisasi")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal mengambil rekapitulasi per Satker: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Rekapitulasi realisasi anggaran per Akun untuk tahun tertentu (atau semua tahun jika tahun == 0).
     */
    public List<MonitoringRow> getRekapPerAkun(int tahun) {
        List<MonitoringRow> list = new ArrayList<>();
        String sql = """
            SELECT 
                p.kode_akun,
                p.nama_akun,
                p.tahun_anggaran,
                SUM(p.nilai_pagu) AS total_pagu,
                COALESCE(SUM(r.total_real), 0) AS total_realisasi
            FROM pagu_akun p
            LEFT JOIN (
                SELECT pagu_id, SUM(nilai_realisasi) AS total_real
                FROM realisasi
                GROUP BY pagu_id
            ) r ON p.id = r.pagu_id
            WHERE (? = 0 OR p.tahun_anggaran = ?)
            GROUP BY p.kode_akun, p.nama_akun, p.tahun_anggaran
            ORDER BY p.kode_akun ASC
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, tahun);
            ps.setInt(2, tahun);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new MonitoringRow(
                            rs.getString("kode_akun"),
                            rs.getString("nama_akun"),
                            rs.getInt("tahun_anggaran"),
                            rs.getBigDecimal("total_pagu"),
                            rs.getBigDecimal("total_realisasi")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal mengambil rekapitulasi per Akun: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Total ringkasan seluruh anggaran untuk tahun tertentu (angka pembanding Panduan Bagian 8.4).
     */
    public MonitoringRow getTotalRingkasan(int tahun) {
        String sql = """
            SELECT 
                p.tahun_anggaran,
                SUM(p.nilai_pagu) AS total_pagu,
                COALESCE(SUM(r.total_real), 0) AS total_realisasi
            FROM pagu_akun p
            LEFT JOIN (
                SELECT pagu_id, SUM(nilai_realisasi) AS total_real
                FROM realisasi
                GROUP BY pagu_id
            ) r ON p.id = r.pagu_id
            WHERE (? = 0 OR p.tahun_anggaran = ?)
            GROUP BY p.tahun_anggaran
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, tahun);
            ps.setInt(2, tahun);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new MonitoringRow(
                            "SEMUA",
                            "Semua Satker",
                            rs.getInt("tahun_anggaran"),
                            rs.getBigDecimal("total_pagu"),
                            rs.getBigDecimal("total_realisasi")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal menghitung ringkasan anggaran: " + e.getMessage(), e);
        }
        return new MonitoringRow("SEMUA", "Semua Satker", tahun, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    /**
     * Rekapitulasi realisasi bulanan berdasarkan tanggal realisasi.
     */
    public Map<Integer, BigDecimal> getRekapitulasiBulanan(long paguId, int tahun) {
        Map<Integer, BigDecimal> map = new LinkedHashMap<>();
        String sql = """
            SELECT 
                MONTH(tanggal_realisasi) AS bulan,
                SUM(nilai_realisasi) AS total
            FROM realisasi
            WHERE (? = 0 OR pagu_id = ?)
              AND (? = 0 OR YEAR(tanggal_realisasi) = ?)
            GROUP BY MONTH(tanggal_realisasi)
            ORDER BY bulan ASC
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, paguId);
            ps.setLong(2, paguId);
            ps.setInt(3, tahun);
            ps.setInt(4, tahun);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getInt("bulan"), rs.getBigDecimal("total"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal mengambil rekapitulasi bulanan: " + e.getMessage(), e);
        }
        return map;
    }

    /**
     * Rekapitulasi realisasi tahunan berdasarkan tanggal realisasi untuk satker tertentu.
     */
    public Map<Integer, BigDecimal> getRekapitulasiTahunan(long satkerId) {
        Map<Integer, BigDecimal> map = new LinkedHashMap<>();
        String sql = """
            SELECT 
                YEAR(r.tanggal_realisasi) AS tahun,
                SUM(r.nilai_realisasi) AS total
            FROM realisasi r
            JOIN pagu_akun p ON r.pagu_id = p.id
            WHERE (? = 0 OR p.satker_id = ?)
            GROUP BY YEAR(r.tanggal_realisasi)
            ORDER BY tahun ASC
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, satkerId);
            ps.setLong(2, satkerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getInt("tahun"), rs.getBigDecimal("total"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal mengambil rekapitulasi tahunan: " + e.getMessage(), e);
        }
        return map;
    }
}
