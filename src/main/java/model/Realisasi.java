package model;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;

public class Realisasi {
    private Long id;
    private Long paguId;
    private LocalDate tanggalRealisasi;
    private BigDecimal nilaiRealisasi;
    private String keterangan;

    public Realisasi() {
    }

    public Realisasi(Long paguId, LocalDate tanggalRealisasi, BigDecimal nilaiRealisasi, String keterangan) {
        this.paguId = paguId;
        this.tanggalRealisasi = tanggalRealisasi;
        this.nilaiRealisasi = nilaiRealisasi;
        this.keterangan = keterangan;
    }

    public Realisasi(Long id, Long paguId, LocalDate tanggalRealisasi, BigDecimal nilaiRealisasi, String keterangan) {
        this.id = id;
        this.paguId = paguId;
        this.tanggalRealisasi = tanggalRealisasi;
        this.nilaiRealisasi = nilaiRealisasi;
        this.keterangan = keterangan;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPaguId() {
        return paguId;
    }

    public void setPaguId(Long paguId) {
        this.paguId = paguId;
    }

    public LocalDate getTanggalRealisasi() {
        return tanggalRealisasi;
    }

    public void setTanggalRealisasi(LocalDate tanggalRealisasi) {
        this.tanggalRealisasi = tanggalRealisasi;
    }

    public BigDecimal getNilaiRealisasi() {
        return nilaiRealisasi;
    }

    public void setNilaiRealisasi(BigDecimal nilaiRealisasi) {
        this.nilaiRealisasi = nilaiRealisasi;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    @Override
    public String toString() {
        NumberFormat rupiah = NumberFormat.getCurrencyInstance(
            Locale.forLanguageTag("id-ID")
        );
        
        return "Realisasi{" +
                "ID= " + id +
                ", ID Pagu= " + paguId +
                ", Tanggal Realisasi= " + tanggalRealisasi +
                ", Nilai Realisasi= " + rupiah.format(nilaiRealisasi) +
                ", Keterangan= '" + keterangan + '\'' +
                '}';
    }
}
