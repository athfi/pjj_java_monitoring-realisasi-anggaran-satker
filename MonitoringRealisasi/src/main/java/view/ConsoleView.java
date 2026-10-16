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
//                case "1":
//                    System.out.println("-> Membuka Menu Kelola Satker (Menunggu Controller dari W...)");
//                    // Nanti kita akan panggil SatkerController di sini
//                    break;
                case "1":
                    // Mengarahkan ke sub-menu Satker
                    menuKelolaSatker();
                    break;
//                case "2":
//                    System.out.println("-> Membuka Menu Kelola Pagu (Menunggu Controller dari W...)");
//                    break;
                  case "2":
                    menuKelolaPagu();
                    break;
//                case "3":
//                    System.out.println("-> Membuka Menu Catat Realisasi (Menunggu Controller dari W...)");
//                    break;
                  case "3":
                    menuCatatRealisasi();
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
//                case "5":
//                    System.out.println("-> Memulai Impor CSV...");
//                    // Nanti kode impor buatan Anda masuk ke sini
//                    break;
                case "5":
                    menuImporCsv();
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
    
    // Sub-menu Kelola Satker buatan Chandra
    private void menuKelolaSatker() {
        boolean kembali = false;
        while (!kembali) {
            System.out.println("\n--- KELOLA SATKER ---");
            System.out.println("1. Tambah Satker Baru");
            System.out.println("2. Lihat Daftar Satker");
            System.out.println("3. Ubah Data Satker");
            System.out.println("4. Hapus Satker");
            System.out.println("0. Kembali ke Menu Utama");
            System.out.print("Pilih opsi: ");

            String pilihan = scanner.nextLine();

            switch (pilihan) {
                case "1":
                    System.out.println("-> [Tampilan Form Tambah Satker akan diletakkan di sini]");
                    // Menunggu SatkerController
                    break;
                case "2":
                    System.out.println("-> [Tampilan Tabel Daftar Satker akan diletakkan di sini]");
                    break;
                case "3":
                    System.out.println("-> [Tampilan Form Ubah Satker akan diletakkan di sini]");
                    break;
                case "4":
                    System.out.println("-> [Tampilan Form Hapus Satker akan diletakkan di sini]");
                    break;
                case "0":
                    kembali = true;
                    break;
                default:
                    System.out.println("Pilihan tidak valid! Masukkan angka 0-4.");
            }
        }
    }
    
    // Sub-menu Kelola Pagu buatan Chandra
    private void menuKelolaPagu() {
        boolean kembali = false;
        while (!kembali) {
            System.out.println("\n--- KELOLA PAGU AKUN ---");
            System.out.println("1. Tambah Pagu Baru");
            System.out.println("2. Lihat Daftar Pagu");
            System.out.println("3. Ubah Nilai Pagu");
            System.out.println("4. Hapus Pagu");
            System.out.println("0. Kembali ke Menu Utama");
            System.out.print("Pilih opsi: ");

            String pilihan = scanner.nextLine();

            switch (pilihan) {
                case "1": System.out.println("-> [Form Tambah Pagu menunggu Controller]"); break;
                case "2": System.out.println("-> [Tabel Daftar Pagu menunggu Controller]"); break;
                case "3": System.out.println("-> [Form Ubah Pagu menunggu Controller]"); break;
                case "4": System.out.println("-> [Form Hapus Pagu menunggu Controller]"); break;
                case "0": kembali = true; break;
                default: System.out.println("Pilihan tidak valid!");
            }
        }
    }

    // Sub-menu Catat Realisasi buatan Chandra
    private void menuCatatRealisasi() {
        boolean kembali = false;
        while (!kembali) {
            System.out.println("\n--- CATAT REALISASI ---");
            System.out.println("1. Catat Transaksi Pengeluaran Baru");
            System.out.println("2. Lihat Riwayat Realisasi");
            System.out.println("0. Kembali ke Menu Utama");
            System.out.print("Pilih opsi: ");

            String pilihan = scanner.nextLine();

            switch (pilihan) {
                case "1": System.out.println("-> [Form Catat Realisasi menunggu Controller]"); break;
                case "2": System.out.println("-> [Tabel Riwayat Realisasi menunggu Controller]"); break;
                case "0": kembali = true; break;
                default: System.out.println("Pilihan tidak valid!");
            }
        }
    }
    
    // Sub-menu Impor CSV buatan Chandra
//    private void menuImporCsv() {
//        System.out.println("\n--- IMPOR DATA CSV ---");
//        System.out.println("Fitur ini akan segera dibangun menggunakan Apache Commons CSV.");
//        System.out.println("Tekan Enter untuk kembali...");
//        scanner.nextLine();
//    }
    // Sub-menu Impor CSV buatan Chandra
    private void menuImporCsv() {
        System.out.println("\n--- IMPOR DATA CSV ---");
        System.out.println("1. Impor Data Satker (dari imports/import_satker_sudin.csv)");
        System.out.println("2. Impor Data Pagu (dari imports/import_pagu_akun_sudin.csv)");
        System.out.println("0. Kembali");
        System.out.print("Pilih file yang akan diimpor: ");
        
        String pilihan = scanner.nextLine();
        
        if (pilihan.equals("0")) return;
        
        System.out.println("\n-> Membaca file...");
        // Di sini nanti Controller buatan W akan mengecek isi file
        System.out.println("-> Ringkasan: Ditemukan 5 baris data valid."); // Ini contoh angka dummy
        
        System.out.print("Apakah Anda yakin ingin menyimpan data ini ke database? (Y/N): ");
        String konfirmasi = scanner.nextLine();
        
        if (konfirmasi.equalsIgnoreCase("Y")) {
            System.out.println("-> [Proses simpan berjalan... Menunggu Controller W]");
        } else {
            System.out.println("-> Impor dibatalkan.");
        }
    }
    
}