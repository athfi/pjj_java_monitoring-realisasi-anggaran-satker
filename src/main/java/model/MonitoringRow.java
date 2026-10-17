package model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

public class MonitoringRow {
    private String kode;
    private String nama;
    private int tahun;
    private BigDecimal totalPagu;
    private BigDecimal totalRealisasi;
    private BigDecimal sisaPagu;
    private BigDecimal persentase;

    public MonitoringRow() {
        this.totalPagu = BigDecimal.ZERO;
        this.totalRealisasi = BigDecimal.ZERO;
        this.sisaPagu = BigDecimal.ZERO;
        this.persentase = BigDecimal.ZERO;
    }

    public MonitoringRow(String kode, String nama, int tahun, BigDecimal totalPagu, BigDecimal totalRealisasi) {
        this.kode = kode;
        this.nama = nama;
        this.tahun = tahun;
        this.totalPagu = totalPagu != null ? totalPagu : BigDecimal.ZERO;
        this.totalRealisasi = totalRealisasi != null ? totalRealisasi : BigDecimal.ZERO;
        hitungSisaDanPersen();
    }

    public void hitungSisaDanPersen() {
        if (totalPagu == null) totalPagu = BigDecimal.ZERO;
        if (totalRealisasi == null) totalRealisasi = BigDecimal.ZERO;

        // Sisa = Pagu - Realisasi
        this.sisaPagu = totalPagu.subtract(totalRealisasi);

        // Persentase = (Realisasi / Pagu) * 100
        if (totalPagu.compareTo(BigDecimal.ZERO) > 0) {
            this.persentase = totalRealisasi
                    .multiply(new BigDecimal("100"))
                    .divide(totalPagu, 2, RoundingMode.HALF_UP);
        } else {
            this.persentase = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public int getTahun() {
        return tahun;
    }

    public void setTahun(int tahun) {
        this.tahun = tahun;
    }

    public BigDecimal getTotalPagu() {
        return totalPagu;
    }

    public void setTotalPagu(BigDecimal totalPagu) {
        this.totalPagu = totalPagu;
        hitungSisaDanPersen();
    }

    public BigDecimal getTotalRealisasi() {
        return totalRealisasi;
    }

    public void setTotalRealisasi(BigDecimal totalRealisasi) {
        this.totalRealisasi = totalRealisasi;
        hitungSisaDanPersen();
    }

    public BigDecimal getSisaPagu() {
        return sisaPagu;
    }

    public BigDecimal getPersentase() {
        return persentase;
    }

    @Override
    public String toString() {
        NumberFormat rupiah = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));
        return String.format("%s - %-50s | Pagu: %18s | Real: %18s | Sisa: %18s | Serapan: %6.2f%%",
                kode,
                (nama.length() > 50 ? nama.substring(0, 47) + "..." : nama),
                rupiah.format(totalPagu),
                rupiah.format(totalRealisasi),
                rupiah.format(sisaPagu),
                persentase.doubleValue());
    }
}
