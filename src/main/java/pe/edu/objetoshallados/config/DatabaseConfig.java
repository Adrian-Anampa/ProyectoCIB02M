package pe.edu.objetoshallados.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public record DatabaseConfig(String mode, String url, String user, String password) {
    public static DatabaseConfig load() {
        Properties properties = new Properties();
        try (InputStream input = DatabaseConfig.class.getResourceAsStream("/app.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("No se pudo leer app.properties", exception);
        }
        return new DatabaseConfig(
                envOr("APP_MODE", properties.getProperty("app.mode", "mock")),
                envOr("APP_DB_URL", properties.getProperty("db.url", "")),
                envOr("APP_DB_USER", properties.getProperty("db.user", "")),
                envOr("APP_DB_PASSWORD", properties.getProperty("db.password", ""))
        );
    }

    private static String envOr(String key, String fallback) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? fallback : value;
    }
}
