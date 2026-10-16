package model;

import java.math.BigDecimal;

public class MonitoringRow {
    private String kodeSatker;
    private BigDecimal pagu;
    private BigDecimal realisasi;
    private BigDecimal sisa;
    private double persen;

    public MonitoringRow(String kodeSatker, BigDecimal pagu, BigDecimal realisasi, BigDecimal sisa, double persen) {
        this.kodeSatker = kodeSatker;
        this.pagu = pagu;
        this.realisasi = realisasi;
        this.sisa = sisa;
        this.persen = persen;
    }

    // Getter untuk digunakan oleh ConsoleView
    public String getKodeSatker() { return kodeSatker; }
    public BigDecimal getPagu() { return pagu; }
    public BigDecimal getRealisasi() { return realisasi; }
    public BigDecimal getSisa() { return sisa; }
    public double getPersen() { return persen; }
}