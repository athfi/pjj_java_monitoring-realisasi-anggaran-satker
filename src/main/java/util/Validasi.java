package util;
import java.math.BigDecimal;
import model.*;

public final class Validasi {
    private Validasi() {}
    public static String teks(String v, String nama, int max) {
        if (v == null || v.isBlank()) throw new IllegalArgumentException(nama + " wajib diisi.");
        String s = v.strip();
        if (s.length() > max) throw new IllegalArgumentException(nama + " maksimal " + max + " karakter.");
        return s;
    }
    public static long id(Long v, String nama) {
        if (v == null || v <= 0) throw new IllegalArgumentException(nama + " harus berupa ID positif.");
        return v;
    }
    public static int tahun(int v, boolean semua) {
        if (semua && v == 0) return v;
        if (v < 1 || v > 9999) throw new IllegalArgumentException("Tahun harus 1 sampai 9999.");
        return v;
    }
    public static BigDecimal uang(BigDecimal v, String nama, boolean nol) {
        if (v == null) throw new IllegalArgumentException(nama + " wajib diisi.");
        if (v.signum() < 0 || (!nol && v.signum() == 0))
            throw new IllegalArgumentException(nama + (nol ? " tidak boleh negatif." : " harus lebih dari nol."));
        if (v.scale() > 2) throw new IllegalArgumentException(nama + " maksimal dua angka desimal.");
        if (v.compareTo(new BigDecimal("9999999999999999.99")) > 0)
            throw new IllegalArgumentException(nama + " maksimal 16 digit sebelum desimal.");
        return v;
    }
    public static void satker(Satker d) {
        if (d == null) throw new IllegalArgumentException("Data Satker wajib diisi.");
        d.setKodeSatker(teks(d.getKodeSatker(), "Kode Satker", 20));
        d.setNamaSatker(teks(d.getNamaSatker(), "Nama Satker", 120));
    }
    public static void pagu(PaguAkun d) {
        if (d == null) throw new IllegalArgumentException("Data pagu wajib diisi.");
        id(d.getSatkerId(), "Satker ID");
        d.setKodeAkun(teks(d.getKodeAkun(), "Kode akun", 12));
        d.setNamaAkun(teks(d.getNamaAkun(), "Nama akun", 120));
        tahun(d.getTahunAnggaran(), false);
        d.setDeskripsiKegiatan(teks(d.getDeskripsiKegiatan(), "Kegiatan", 150));
        uang(d.getNilaiPagu(), "Nilai pagu", true);
    }
    public static void realisasi(Realisasi d) {
        if (d == null) throw new IllegalArgumentException("Data realisasi wajib diisi.");
        id(d.getPaguId(), "Pagu ID");
        d.setNomorSp2d(teks(d.getNomorSp2d(), "Nomor SP2D", 40));
        if (d.getTanggalRealisasi() == null) throw new IllegalArgumentException("Tanggal realisasi wajib diisi.");
        tahun(d.getTanggalRealisasi().getYear(), false);
        uang(d.getNilaiRealisasi(), "Nilai realisasi", false);
        if (d.getKeterangan() != null && d.getKeterangan().length() > 250)
            throw new IllegalArgumentException("Keterangan maksimal 250 karakter.");
    }
}
