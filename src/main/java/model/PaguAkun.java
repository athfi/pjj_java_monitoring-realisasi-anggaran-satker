package model;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class PaguAkun {
    private Long id;
    private Long satkerId;
    private String kodeAkun;
    private String namaAkun;
    private int tahunAnggaran;
    private String deskripsiKegiatan;
    private BigDecimal nilaiPagu;

    public PaguAkun() {
    }

    public PaguAkun(Long satkerId, String kodeAkun, String namaAkun, int tahunAnggaran, String deskripsiKegiatan, BigDecimal nilaiPagu) {
        this.satkerId = satkerId;
        this.kodeAkun = kodeAkun;
        this.namaAkun = namaAkun;
        this.tahunAnggaran = tahunAnggaran;
        this.deskripsiKegiatan = deskripsiKegiatan;
        this.nilaiPagu = nilaiPagu;
    }

    public PaguAkun(Long id, Long satkerId, String kodeAkun, String namaAkun, int tahunAnggaran, String deskripsiKegiatan, BigDecimal nilaiPagu) {
        this.id = id;
        this.satkerId = satkerId;
        this.kodeAkun = kodeAkun;
        this.namaAkun = namaAkun;
        this.tahunAnggaran = tahunAnggaran;
        this.deskripsiKegiatan = deskripsiKegiatan;
        this.nilaiPagu = nilaiPagu;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSatkerId() {
        return satkerId;
    }

    public void setSatkerId(Long satkerId) {
        this.satkerId = satkerId;
    }

    public String getKodeAkun() {
        return kodeAkun;
    }

    public void setKodeAkun(String kodeAkun) {
        this.kodeAkun = kodeAkun;
    }

    public String getNamaAkun() {
        return namaAkun;
    }

    public void setNamaAkun(String namaAkun) {
        this.namaAkun = namaAkun;
    }

    public int getTahunAnggaran() {
        return tahunAnggaran;
    }

    public void setTahunAnggaran(int tahunAnggaran) {
        this.tahunAnggaran = tahunAnggaran;
    }

    public String getDeskripsiKegiatan() {
        return deskripsiKegiatan;
    }

    public void setDeskripsiKegiatan(String deskripsiKegiatan) {
        this.deskripsiKegiatan = deskripsiKegiatan;
    }

    public BigDecimal getNilaiPagu() {
        return nilaiPagu;
    }

    public void setNilaiPagu(BigDecimal nilaiPagu) {
        this.nilaiPagu = nilaiPagu;
    }

    @Override
    public String toString() {
        NumberFormat rupiah = NumberFormat.getCurrencyInstance(
            Locale.forLanguageTag("id-ID")
        );
        
        return "PaguAkun{" +
                "ID= " + id +
                ", ID Satker= " + satkerId +
                ", Kode Akun= '" + kodeAkun + '\'' +
                ", Nama Akun= '" + namaAkun + '\'' +
                ", Tahun Anggaran= " + tahunAnggaran +
                ", Kegiatan= '" + deskripsiKegiatan + '\'' +
                ", Pagu= " + rupiah.format(nilaiPagu) +
                '}';
    }
}
