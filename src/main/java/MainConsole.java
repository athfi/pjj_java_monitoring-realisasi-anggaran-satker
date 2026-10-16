import dao.CrudRepository;
import dao.MonitoringDAO;
import dao.PaguAkunDAO;
import dao.RealisasiDAO;
import dao.SatkerDAO;
import exception.PaguExceededException;
import model.MonitoringRow;
import model.PaguAkun;
import model.Realisasi;
import model.Satker;
import service.BudgetService;
import util.DatabaseConnection;
import util.SetupDatabase;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public class MainConsole {

    public static void main(String[] args) {
        System.out.println("""
        +--------------------------------------------------------------+
        |           MONITORING REALISASI ANGGARAN SATKER               |
        |                                                              |
        |              Sistem Informasi Pengelolaan                    |
        |                     Anggaran Satker                          |
        |--------------------------------------------------------------|
        |                  JAVA CONSOLE - MODUL DATA                   |
        +--------------------------------------------------------------+
        """);

        try (Connection kon = DatabaseConnection.getConnection()) {
            System.out.println("Status: Koneksi ke database H2 berhasil.");

            // 1. Cek apakah tabel sudah ada; jika belum pernah setup, inisialisasi dengan setup_demo_h2.sql
            if (!isTableExist(kon, "SATKER")) {
                System.out.println("Tabel belum ditemukan. Menjalankan inisialisasi data demo awal...");
                SetupDatabase.setup();
            } else {
                System.out.println("Status: Struktur tabel database siap.");
            }

            // 2. Inisialisasi DAO (Tugas Utama Al - Akses Data)
            CrudRepository<Satker> satkerRepo = new SatkerDAO(kon);
            PaguAkunDAO paguDAO = new PaguAkunDAO(kon);
            RealisasiDAO realisasiDAO = new RealisasiDAO(kon);
            MonitoringDAO monitoringDAO = new MonitoringDAO(kon);
            BudgetService budgetService = new BudgetService(paguDAO, realisasiDAO);

            // 3. Uji Coba Pembacaan Satker via SatkerDAO (Polimorfisme CrudRepository)
            System.out.println("\n--- [1] UJI COBA SATKER DAO (Menampilkan 3 Satker Pertama) ---");
            List<Satker> listSatker = satkerRepo.findAll();
            for (int i = 0; i < Math.min(3, listSatker.size()); i++) {
                System.out.println("  " + listSatker.get(i));
            }
            System.out.println("  Total Satker terdaftar: " + listSatker.size());

            // 4. Uji Coba Pagu Akun via PaguAkunDAO
            System.out.println("\n--- [2] UJI COBA PAGU AKUN DAO (Menampilkan 3 Pagu Pertama) ---");
            List<PaguAkun> listPagu = paguDAO.findAll();
            for (int i = 0; i < Math.min(3, listPagu.size()); i++) {
                System.out.println("  " + listPagu.get(i));
            }
            System.out.println("  Total Pagu Akun terdaftar: " + listPagu.size());

            // 5. Uji Coba Monitoring DAO (Angka Pembanding Panduan v1.5 Bagian 8.4)
            System.out.println("\n--- [3] UJI COBA MONITORING DAO (Angka Pembanding 2026) ---");
            MonitoringRow summary2026 = monitoringDAO.getTotalRingkasan(2026);
            NumberFormat rupiah = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));

            System.out.println("  Tahun Anggaran  : " + summary2026.getTahun());
            System.out.println("  Total Pagu      : " + rupiah.format(summary2026.getTotalPagu()) + " (Target: Rp4.050.000.000)");
            System.out.println("  Total Realisasi : " + rupiah.format(summary2026.getTotalRealisasi()) + " (Target: Rp1.798.750.000)");
            System.out.println("  Sisa Anggaran   : " + rupiah.format(summary2026.getSisaPagu()) + " (Target: Rp2.251.250.000)");
            System.out.println("  Serapan         : " + summary2026.getPersentase() + "% (Target: 44,41%)");

            // 6. Uji Coba Rekap per Akun
            System.out.println("\n--- [4] REKAPITULASI PER AKUN (2026) ---");
            List<MonitoringRow> rekapAkun = monitoringDAO.getRekapPerAkun(2026);
            for (MonitoringRow row : rekapAkun) {
                System.out.println("  " + row);
            }

            // 7. Uji Transaksi Aman & Penguncian Pagu (BudgetService / Concurrency Check)
            System.out.println("\n--- [5] UJI TRANSAKSI AMAN (Validasi Batas Pagu & Custom Exception) ---");
            // Test mencatat realisasi yang melampaui sisa pagu
            long targetPaguId = 1L; // Pagu ID 1
            BigDecimal totalRealSaatIni = realisasiDAO.getTotalRealisasiByPaguId(targetPaguId);
            PaguAkun pagu1 = paguDAO.findById(targetPaguId);
            BigDecimal sisaPagu1 = pagu1.getNilaiPagu().subtract(totalRealSaatIni);

            System.out.println("  Pagu 1 (" + pagu1.getDeskripsiKegiatan() + ")");
            System.out.println("  - Sisa Anggaran Tersedia: " + rupiah.format(sisaPagu1));

            BigDecimal nilaiOver = sisaPagu1.add(new BigDecimal("1000000.00"));
            System.out.println("  - Mencoba input realisasi sebesar: " + rupiah.format(nilaiOver) + " (Sengaja melebihi sisa)...");

            try {
                Realisasi rOver = new Realisasi(
                        targetPaguId,
                        "TEST-OVER-" + System.currentTimeMillis(),
                        LocalDate.now(),
                        nilaiOver,
                        "Uji coba pelanggaran batas pagu"
                );
                budgetService.catatRealisasi(rOver);
                System.out.println("  [GAGAL] Transaksi seharusnya ditolak!");
            } catch (PaguExceededException pee) {
                System.out.println("  [SUKSES DITOLAK] Terdeteksi PaguExceededException: " + pee.getMessage());
            }

            System.out.println("\n=== Seluruh Pengujian Modul Akses Data (Al) Selesai dengan Sukses ===");

        } catch (Exception e) {
            System.err.println("Terjadi kesalahan: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static boolean isTableExist(Connection conn, String tableName) {
        try {
            DatabaseMetaData dbm = conn.getMetaData();
            try (ResultSet rs = dbm.getTables(null, null, tableName.toUpperCase(), null)) {
                return rs.next();
            }
        } catch (Exception e) {
            return false;
        }
    }
}
