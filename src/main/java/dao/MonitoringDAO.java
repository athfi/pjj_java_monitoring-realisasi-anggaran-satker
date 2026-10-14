package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class MonitoringDAO {
    private final Connection connection;

    public MonitoringDAO(Connection connection) {
        this.connection = connection;
    }

    public Map<Integer, BigDecimal> getRekapitulasiBulanan(long paguId, int tahun) {
        Map<Integer, BigDecimal> rekapBulanan = new HashMap<>();
        // TODO: Query rekapitulasi realisasi bulanan menggunakan PreparedStatement
        return rekapBulanan;
    }

    public Map<Integer, BigDecimal> getRekapitulasiTahunan(long satkerId) {
        Map<Integer, BigDecimal> rekapTahunan = new HashMap<>();
        // TODO: Query rekapitulasi realisasi tahunan menggunakan PreparedStatement
        return rekapTahunan;
    }
}
