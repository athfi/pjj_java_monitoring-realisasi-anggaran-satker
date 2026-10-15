
import database.InisiasiDB;
import java.sql.Connection;
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
        
        
        try(Connection kon = DatabaseConnection.getConnection()) {
            System.out.println("Koneksi database berhasil.");
            
            // 1. Menyiapkan tabel jika belum tersedia
            InisiasiDB.setupSchema(kon);
            System.out.println("Struktur database siap.");
            
            // 2. Memasukkan data awal jika diperlukan
            InisiasiDB.seedData(kon);
            System.out.println("Penyiapan data awal selesai.");
            
        } catch (Exception e) {
        }
        
        
    }
}
