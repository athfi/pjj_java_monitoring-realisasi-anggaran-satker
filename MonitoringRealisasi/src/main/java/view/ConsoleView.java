package view;
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;
import model.MonitoringRow;
import java.util.Scanner;

public class ConsoleView {
    private Scanner scanner;

    public ConsoleView() {
        scanner = new Scanner(System.in);
    }

    public void mulai() {
        boolean jalan = true;
        while (jalan) {
            System.out.println("\n=== MONITORING REALISASI ANGGARAN SATKER ===");
            System.out.println("1. Kelola Satker");
            System.out.println("2. Kelola Pagu Akun");
            System.out.println("3. Catat Realisasi");
            System.out.println("4. Rekap & Dashboard (Tugas Saya)");
            System.out.println("5. Impor Data CSV (Tugas Saya)");
            System.out.println("0. Keluar");
            System.out.print("Pilih menu: ");

            String pilihan = scanner.nextLine();

            switch (pilihan) {
                case "1":
                    System.out.println("-> Membuka Menu Kelola Satker (Menunggu Controller dari W...)");
                    // Nanti kita akan panggil SatkerController di sini
                    break;
                case "2":
                    System.out.println("-> Membuka Menu Kelola Pagu (Menunggu Controller dari W...)");
                    break;
                case "3":
                    System.out.println("-> Membuka Menu Catat Realisasi (Menunggu Controller dari W...)");
                    break;
//                case "4":
//                    System.out.println("-> Menampilkan Rekapitulasi...");
//                    // Nanti kode fitur rekap buatan Anda akan masuk ke sini
//                    break;
                case "4":
                 // Simulasi data dummy untuk menguji tabel buatan Chandra
                 List<MonitoringRow> dummyData = new ArrayList<>();
                 dummyData.add(new MonitoringRow("3.27.01.01", new BigDecimal("120000000"), new BigDecimal("112800000"), new BigDecimal("7200000"), 94.00));
                 tampilkanDashboard(dummyData);
                 break;
                case "5":
                    System.out.println("-> Memulai Impor CSV...");
                    // Nanti kode impor buatan Anda masuk ke sini
                    break;
                case "0":
                    jalan = false;
                    System.out.println("Keluar dari aplikasi. Terima kasih!");
                    break;
                default:
                    System.out.println("Pilihan tidak valid! Masukkan angka 0-5.");
            }
        }
    }
    // Method khusus buatan Chandra untuk memformat tabel Dashboard
    public void tampilkanDashboard(List<MonitoringRow> daftarRekap) {
        System.out.println("\n=== DASHBOARD REKAPITULASI ANGGARAN ===");
        System.out.println("----------------------------------------------------------------------------------");
        System.out.printf("| %-15s | %-15s | %-15s | %-15s | %-7s |\n", 
                "KODE SATKER", "PAGU", "REALISASI", "SISA", "PERSEN");
        System.out.println("----------------------------------------------------------------------------------");

        if (daftarRekap == null || daftarRekap.isEmpty()) {
            System.out.println("|                            Belum ada data transaksi                            |");
        } else {
            for (MonitoringRow baris : daftarRekap) {
                System.out.printf("| %-15s | Rp%-13s | Rp%-13s | Rp%-13s | %5.2f%% |\n",
                        baris.getKodeSatker(),
                        baris.getPagu(),
                        baris.getRealisasi(),
                        baris.getSisa(),
                        baris.getPersen());
            }
        }
        System.out.println("----------------------------------------------------------------------------------");
    }
}