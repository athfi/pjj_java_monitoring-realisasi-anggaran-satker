package controller;

import dao.MonitoringDAO;
import model.MonitoringRow;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import util.Validasi;

public class MonitoringController {
    private final MonitoringDAO monitoringDAO;

    public MonitoringController(MonitoringDAO monitoringDAO) {
        this.monitoringDAO = monitoringDAO;
    }

    public List<MonitoringRow> getRekapPerSatker(int tahun) {
        Validasi.tahun(tahun, true);
        return monitoringDAO.getRekapPerSatker(tahun);
    }

    public List<MonitoringRow> getRekapPerAkun(int tahun) {
        Validasi.tahun(tahun, true);
        return monitoringDAO.getRekapPerAkun(tahun);
    }

    public MonitoringRow getTotalRingkasan(int tahun) {
        Validasi.tahun(tahun, false);
        return monitoringDAO.getTotalRingkasan(tahun);
    }

    public Map<Integer, BigDecimal> getRekapitulasiBulanan(long paguId, int tahun) {
        if (paguId < 0) throw new IllegalArgumentException("Pagu ID tidak boleh negatif; 0 berarti semua.");
        Validasi.tahun(tahun, false);
        return monitoringDAO.getRekapitulasiBulanan(paguId, tahun);
    }

    public Map<Integer, BigDecimal> getRekapitulasiTahunan(long satkerId) {
        if (satkerId < 0) throw new IllegalArgumentException("Satker ID tidak boleh negatif; 0 berarti semua.");
        return monitoringDAO.getRekapitulasiTahunan(satkerId);
    }
}
