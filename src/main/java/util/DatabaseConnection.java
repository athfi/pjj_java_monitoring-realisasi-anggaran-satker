package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static final Properties properties = new Properties();

    static {
        loadProperties();
    }

    private static void loadProperties() {
        // Coba muat dari file lokal db.properties di root direktori
        try (InputStream input = new FileInputStream("db.properties")) {
            properties.load(input);
        } catch (IOException e1) {
            // Jika tidak ada di root, coba muat dari classpath
            try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
                if (input != null) {
                    properties.load(input);
                } else {
                    // Fallback ke db.properties.example
                    try (InputStream exampleInput = DatabaseConnection.class.getClassLoader().getResourceAsStream("db.properties.example")) {
                        if (exampleInput != null) {
                            properties.load(exampleInput);
                        } else {
                            // Fallback default
                            properties.setProperty("db.url", "jdbc:h2:./data/monitoring_anggaran");
                            properties.setProperty("db.user", "sa");
                            properties.setProperty("db.password", "");
                        }
                    }
                }
            } catch (IOException e2) {
                properties.setProperty("db.url", "jdbc:h2:./data/monitoring_anggaran");
                properties.setProperty("db.user", "sa");
                properties.setProperty("db.password", "");
            }
        }
    }

    public static Connection getConnection() throws SQLException {
        String url = properties.getProperty("db.url", "jdbc:h2:./data/monitoring_anggaran");
        String user = properties.getProperty("db.user", "sa");
        String password = properties.getProperty("db.password", "");
        return DriverManager.getConnection(url, user, password);
    }
}
