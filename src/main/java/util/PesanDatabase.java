package util;
import java.sql.SQLException;

/** Pesan untuk View; detail teknis tetap tersedia pada cause. */
public final class PesanDatabase {
    private PesanDatabase() {}
    public static RuntimeException terjemahkan(Exception e) {
        for (Throwable t = e; t != null; t = t.getCause()) {
            if (t instanceof SQLException sql) {
                String s = sql.getSQLState();
                if ("23505".equals(s)) return new IllegalArgumentException("Data duplikat: kode, kombinasi pagu, atau SP2D sudah digunakan.", e);
                if ("23503".equals(s) || "23506".equals(s))
                    return new IllegalArgumentException("Relasi tidak valid: induk tidak ditemukan atau masih digunakan.", e);
                if (s != null && s.startsWith("23"))
                    return new IllegalArgumentException("Data melanggar batas atau kelengkapan kolom database.", e);
                return new IllegalStateException("Operasi database gagal. Periksa koneksi atau hubungi pengelola.", e);
            }
        }
        return e instanceof RuntimeException r ? r : new IllegalStateException("Operasi database gagal.", e);
    }
}
