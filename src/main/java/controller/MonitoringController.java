package controller;

import dao.MonitoringDAO;

import java.math.BigDecimal;
import java.util.Map;

public class MonitoringController {
    private final MonitoringDAO monitoringDAO;

    public MonitoringController(MonitoringDAO monitoringDAO) {
        this.monitoringDAO = monitoringDAO;
    }

    public Map<Integer, BigDecimal> getRekapitulasiBulanan(long paguId, int tahun) {
        return monitoringDAO.getRekapitulasiBulanan(paguId, tahun);
    }

    public Map<Integer, BigDecimal> getRekapitulasiTahunan(long satkerId) {
        return monitoringDAO.getRekapitulasiTahunan(satkerId);
    }
}
