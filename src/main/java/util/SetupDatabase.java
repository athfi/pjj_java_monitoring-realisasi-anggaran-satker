package util;

import org.h2.tools.RunScript;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Helper untuk setup / reset database H2 menggunakan setup_demo_h2.sql.
 * Sesuai panduan v1.5 Bagian 7.4:
 * Dijalankan manual satu kali (bukan otomatis dipanggil oleh MainConsole).
 */
public class SetupDatabase {

    public static void main(String[] args) {
        System.out.println("=== Inisialisasi Database H2 (SetupDatabase) ===");
        try {
            setup();
            System.out.println("Setup database H2 selesai dan berhasil!");
        } catch (Exception e) {
            System.err.println("Gagal melakukan setup database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void setup() throws Exception {
        Reader reader = getSqlScriptReader();
        if (reader == null) {
            throw new IllegalStateException("File setup_demo_h2.sql tidak ditemukan di database/, root, atau resources.");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             Reader r = reader;
             Statement stmt = conn.createStatement()) {

            System.out.println("Mengeksekusi skrip setup_demo_h2.sql...");
            RunScript.execute(conn, r);

            // Verifikasi jumlah baris sesuai checklist T02
            int totalSatker = 0;
            int totalPagu = 0;
            int totalRealisasi = 0;

            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM satker")) {
                if (rs.next()) totalSatker = rs.getInt(1);
            }
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM pagu_akun")) {
                if (rs.next()) totalPagu = rs.getInt(1);
            }
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM realisasi")) {
                if (rs.next()) totalRealisasi = rs.getInt(1);
            }

            System.out.println("----------------------------------------------");
            System.out.println("Verifikasi Jumlah Data (Target Checklist T02):");
            System.out.println("- Satker    : " + totalSatker + " (Target: 12)");
            System.out.println("- Pagu Akun : " + totalPagu + " (Target: 26)");
            System.out.println("- Realisasi : " + totalRealisasi + " (Target: 52)");
            System.out.println("----------------------------------------------");

            if (totalSatker == 12 && totalPagu == 26 && totalRealisasi == 52) {
                System.out.println("STATUS: VALID (Sesuai 100% dengan dataset Suku Dinas)");
            } else {
                System.out.println("STATUS PERINGATAN: Jumlah data belum sesuai target 12, 26, 52.");
            }
        }
    }

    private static Reader getSqlScriptReader() throws Exception {
        InputStream is = null;
        File fileDb = new File("database/setup_demo_h2.sql");
        if (fileDb.exists()) {
            is = new FileInputStream(fileDb);
        } else {
            File fileRoot = new File("setup_demo_h2.sql");
            if (fileRoot.exists()) {
                is = new FileInputStream(fileRoot);
            } else {
                is = SetupDatabase.class.getResourceAsStream("/setup_demo_h2.sql");
            }
        }
        if (is == null) {
            return null;
        }

        // Tangani dan buang UTF-8 BOM jika ada
        BufferedInputStream bis = new BufferedInputStream(is);
        bis.mark(3);
        byte[] bom = new byte[3];
        int n = bis.read(bom, 0, 3);
        if (!(n >= 3 && (bom[0] & 0xFF) == 0xEF && (bom[1] & 0xFF) == 0xBB && (bom[2] & 0xFF) == 0xBF)) {
            bis.reset();
        }

        return new BufferedReader(new InputStreamReader(bis, StandardCharsets.UTF_8));
    }
}
