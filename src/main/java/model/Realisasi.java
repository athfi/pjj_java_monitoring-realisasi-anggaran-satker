package model;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;

public class Realisasi {
    private Long id;
    private Long paguId;
    private PaguAkun paguAkun;
    private String nomorSp2d;
    private LocalDate tanggalRealisasi;
    private BigDecimal nilaiRealisasi;
    private String keterangan;

    public Realisasi() {
    }

    public Realisasi(Long paguId, String nomorSp2d, LocalDate tanggalRealisasi, BigDecimal nilaiRealisasi, String keterangan) {
        this.paguId = paguId;
        this.nomorSp2d = nomorSp2d;
        this.tanggalRealisasi = tanggalRealisasi;
        this.nilaiRealisasi = nilaiRealisasi;
        this.keterangan = keterangan;
    }

    public Realisasi(PaguAkun paguAkun, String nomorSp2d, LocalDate tanggalRealisasi, BigDecimal nilaiRealisasi, String keterangan) {
        this.paguAkun = paguAkun;
        if (paguAkun != null) {
            this.paguId = paguAkun.getId();
        }
        this.nomorSp2d = nomorSp2d;
        this.tanggalRealisasi = tanggalRealisasi;
        this.nilaiRealisasi = nilaiRealisasi;
        this.keterangan = keterangan;
    }

    public Realisasi(Long id, Long paguId, String nomorSp2d, LocalDate tanggalRealisasi, BigDecimal nilaiRealisasi, String keterangan) {
        this.id = id;
        this.paguId = paguId;
        this.nomorSp2d = nomorSp2d;
        this.tanggalRealisasi = tanggalRealisasi;
        this.nilaiRealisasi = nilaiRealisasi;
        this.keterangan = keterangan;
    }

    public Realisasi(Long id, PaguAkun paguAkun, String nomorSp2d, LocalDate tanggalRealisasi, BigDecimal nilaiRealisasi, String keterangan) {
        this.id = id;
        this.paguAkun = paguAkun;
        if (paguAkun != null) {
            this.paguId = paguAkun.getId();
        }
        this.nomorSp2d = nomorSp2d;
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
        if (paguId != null) {
            return paguId;
        }
        return paguAkun != null ? paguAkun.getId() : null;
    }

    public void setPaguId(Long paguId) {
        this.paguId = paguId;
    }

    public PaguAkun getPaguAkun() {
        return paguAkun;
    }

    public void setPaguAkun(PaguAkun paguAkun) {
        this.paguAkun = paguAkun;
        if (paguAkun != null) {
            this.paguId = paguAkun.getId();
        }
    }

    public String getNomorSp2d() {
        return nomorSp2d;
    }

    public void setNomorSp2d(String nomorSp2d) {
        this.nomorSp2d = nomorSp2d;
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

        String infoPagu = (paguAkun != null) 
                ? (paguAkun.getKodeAkun() + " - " + paguAkun.getNamaAkun()) 
                : ("PaguID=" + paguId);

        return "Realisasi{" +
                "ID= " + id +
                ", SP2D= '" + nomorSp2d + '\'' +
                ", Pagu= " + infoPagu +
                ", Tanggal Realisasi= " + tanggalRealisasi +
                ", Nilai Realisasi= " + (nilaiRealisasi != null ? rupiah.format(nilaiRealisasi) : "0") +
                ", Keterangan= '" + keterangan + '\'' +
                '}';
    }
}
