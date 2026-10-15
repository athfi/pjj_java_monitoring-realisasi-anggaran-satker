
import database.InisiasiDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import model.Satker;
import util.DatabaseConnection;

public class MainConsole {

    public static void main(String[] args) {
        // TODO: Inisialisasi koneksi, DAO, Controller, dan jalankan menu Console

        System.out.println("""
        +--------------------------------------------------------------+
        |           MONITORING REALISASI ANGGARAN SATKER               |
        |                                                              |
        |              Sistem Informasi Pengelolaan                    |
        |                     Anggaran Satker                          |
        |--------------------------------------------------------------|
        |                      JAVA CONSOLE                            |
        +--------------------------------------------------------------+
        """);

        try (Connection kon = DatabaseConnection.getConnection()) {
            System.out.println("Koneksi database berhasil.");

            // 1. Menyiapkan tabel jika belum tersedia
            InisiasiDB.setupSchema(kon);
            System.out.println("Struktur database siap.");

            // 2. Memasukkan data awal
            InisiasiDB.seedData(kon);
            System.out.println("Penyiapan data awal selesai.");

            //testing ambil 3 satker
            System.out.println("Test ambil data 3 satker");
            String sql = "SELECT id, kode_satker, nama_satker FROM satker ORDER BY kode_satker ASC FETCH FIRST 3 ROWS ONLY ";
            try (PreparedStatement ps = kon.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                int i = 0;
                while (rs.next()) {
                    Satker satker = new Satker(
                            rs.getLong("id"),
                            rs.getString("kode_satker"),
                            rs.getString("nama_satker")
                    );
                    i++;
                    System.out.println(satker);
                }
                if (i == 0) {
                    System.out.println("  Belum ada data satker.");
                }

            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}
