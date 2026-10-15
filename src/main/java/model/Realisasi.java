package model;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;

public class Realisasi {
    private Long id;
    private PaguAkun paguAkun;
    private LocalDate tanggalRealisasi;
    private BigDecimal nilaiRealisasi;
    private String keterangan;

    public Realisasi() {
    }

    public Realisasi(PaguAkun paguAkun, LocalDate tanggalRealisasi, BigDecimal nilaiRealisasi, String keterangan) {
        this.paguAkun = paguAkun;
        this.tanggalRealisasi = tanggalRealisasi;
        this.nilaiRealisasi = nilaiRealisasi;
        this.keterangan = keterangan;
    }

    public Realisasi(Long id, PaguAkun paguAkun, LocalDate tanggalRealisasi, BigDecimal nilaiRealisasi, String keterangan) {
        this.id = id;
        this.paguAkun = paguAkun;
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

    public PaguAkun  getPaguAkun() {
        return paguAkun;
    }

    public void setPaguId(PaguAkun paguAkun) {
        this.paguAkun = paguAkun;
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
                ", Kode Akun= " + paguAkun.getKodeAkun() +
                ", Nama Akun= " + paguAkun.getNamaAkun() +
                ", Nama Kegiatan= " + paguAkun.getDeskripsiKegiatan() +
                ", Tanggal Realisasi= " + tanggalRealisasi +
                ", Nilai Realisasi= " + rupiah.format(nilaiRealisasi) +
                ", Keterangan= '" + keterangan + '\'' +
                '}';
    }
}
