package controller;

import dao.MonitoringDAO;
import model.MonitoringRow;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class MonitoringController {
    private final MonitoringDAO monitoringDAO;

    public MonitoringController(MonitoringDAO monitoringDAO) {
        this.monitoringDAO = monitoringDAO;
    }

    public List<MonitoringRow> getRekapPerSatker(int tahun) {
        return monitoringDAO.getRekapPerSatker(tahun);
    }

    public List<MonitoringRow> getRekapPerAkun(int tahun) {
        return monitoringDAO.getRekapPerAkun(tahun);
    }

    public MonitoringRow getTotalRingkasan(int tahun) {
        return monitoringDAO.getTotalRingkasan(tahun);
    }

    public Map<Integer, BigDecimal> getRekapitulasiBulanan(long paguId, int tahun) {
        return monitoringDAO.getRekapitulasiBulanan(paguId, tahun);
    }

    public Map<Integer, BigDecimal> getRekapitulasiTahunan(long satkerId) {
        return monitoringDAO.getRekapitulasiTahunan(satkerId);
    }
}
