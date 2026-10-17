import controller.*;
import dao.*;
import model.*;
import service.BudgetService;
import util.CsvImporter;
import org.h2.tools.RunScript;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Sarana uji manual W, bukan pengganti menu akhir anggota C.
 * Selalu memakai file data/uji_w yang terpisah dari database aplikasi.
 */
public class MainUjiW {
    private static final String URL = "jdbc:h2:./data/uji_w";
    private static final Scanner INPUT = new Scanner(System.in);
    private static String input(String label) { System.out.print(label + ": "); return INPUT.nextLine(); }
    private static long id(String label) { return Long.parseLong(input(label)); }
    private static BigDecimal nominal(String label) { return new BigDecimal(input(label)); }
    private static Connection buka() throws SQLException { return DriverManager.getConnection(URL,"sa",""); }

    public static void main(String[] args) throws Exception {
        System.out.println("UJI MODUL W - database khusus ./data/uji_w (bukan database aplikasi)");
        try (Connection c = buka()) {
            boolean ada;
            try (ResultSet rs = c.getMetaData().getTables(null,"PUBLIC","SATKER",new String[]{"TABLE"})) { ada = rs.next(); }
            if (!ada) {
                System.out.println("Membuat dataset demo pada database uji baru.");
                try (Reader r = new InputStreamReader(Objects.requireNonNull(
                        MainUjiW.class.getResourceAsStream("/setup_demo_h2.sql")), java.nio.charset.StandardCharsets.UTF_8)) {
                    RunScript.execute(c,r);
                }
            }
            BudgetService service = new BudgetService(MainUjiW::buka);
            SatkerController satker = new SatkerController(new SatkerDAO(c));
            PaguAkunController pagu = new PaguAkunController(new PaguAkunDAO(c),service);
            RealisasiController realisasi = new RealisasiController(new RealisasiDAO(c),service);
            MonitoringController monitoring = new MonitoringController(new MonitoringDAO(c));
            CsvImporter importer = new CsvImporter(MainUjiW::buka);
            while (true) {
                System.out.println("""
                        
                        1 Daftar Satker       2 Tambah Satker       3 Ubah Satker
                        4 Hapus Satker        5 Daftar pagu         6 Tambah pagu
                        7 Ubah nilai pagu     8 Catat realisasi     9 Ringkasan tahun
                        10 Impor Satker CSV   11 Impor pagu CSV      12 Ubah identitas pagu
                        13 Hapus pagu         14 Daftar realisasi   0 Keluar
                        """);
                try {
                    String menu = input("Menu");
                    if ("0".equals(menu)) break;
                    switch(menu) {
                        case "1" -> satker.getAllSatker().forEach(System.out::println);
                        case "2" -> {
                            Satker s = new Satker(null,input("Kode Satker"),input("Nama Satker"));
                            satker.tambahSatker(s); System.out.println("Tersimpan, ID=" + s.getId());
                        }
                        case "3" -> {
                            Satker s = satker.getSatkerById(id("Satker ID"));
                            s.setKodeSatker(input("Kode Satker baru")); s.setNamaSatker(input("Nama baru"));
                            satker.ubahSatker(s); System.out.println("Satker diperbarui.");
                        }
                        case "4" -> { satker.hapusSatker(id("Satker ID")); System.out.println("Satker dihapus."); }
                        case "5" -> pagu.getAllPaguAkun().forEach(System.out::println);
                        case "6" -> {
                            PaguAkun p = new PaguAkun(id("Satker ID"),input("Kode akun"),input("Nama akun"),
                                    Integer.parseInt(input("Tahun")),input("Kegiatan"),nominal("Pagu (contoh 1000.00)"));
                            pagu.tambahPaguAkun(p); System.out.println("Pagu tersimpan, ID=" + p.getId());
                        }
                        case "7" -> {
                            service.ubahNilaiPagu(id("Pagu ID"),nominal("Nilai pagu baru"));
                            System.out.println("Nilai pagu diperbarui.");
                        }
                        case "8" -> {
                            Realisasi r = new Realisasi(id("Pagu ID"),input("SP2D"),
                                    LocalDate.parse(input("Tanggal yyyy-MM-dd")),nominal("Nilai realisasi"),input("Keterangan"));
                            realisasi.catatRealisasi(r); System.out.println("Realisasi tersimpan, ID=" + r.getId());
                        }
                        case "9" -> System.out.println(monitoring.getTotalRingkasan(Integer.parseInt(input("Tahun"))));
                        case "10", "11" -> {
                            var tipe = "10".equals(menu) ? CsvImporter.Tipe.SATKER : CsvImporter.Tipe.PAGU;
                            var p = importer.pratinjau(Path.of(input("Path CSV tanpa tanda kutip")),tipe);
                            System.out.println("Valid: " + p.jumlah() + " record. File: " + p.file());
                            boolean lanjut = "YA".equalsIgnoreCase(input("Ketik YA untuk simpan, lainnya batal"));
                            System.out.println(importer.simpan(p,lanjut) + " record disimpan.");
                        }
                        case "12" -> {
                            PaguAkun p = pagu.getPaguAkunById(id("Pagu ID"));
                            p.setDeskripsiKegiatan(input("Deskripsi kegiatan baru"));
                            pagu.ubahPaguAkun(p); System.out.println("Kegiatan diperbarui.");
                        }
                        case "13" -> { pagu.hapusPaguAkun(id("Pagu ID")); System.out.println("Pagu dihapus."); }
                        case "14" -> realisasi.getAllRealisasi().forEach(System.out::println);
                        default -> System.out.println("Menu tidak dikenal.");
                    }
                } catch (RuntimeException e) {
                    System.out.println("DITOLAK: " + e.getMessage());
                }
            }
        }
    }
}
