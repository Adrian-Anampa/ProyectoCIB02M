package pe.edu.objetoshallados.repository;

import pe.edu.objetoshallados.config.DatabaseConfig;
import pe.edu.objetoshallados.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

public final class SqlServerAppRepository implements AppRepository {
    private final DatabaseConfig config;

    public SqlServerAppRepository(DatabaseConfig config) {
        this.config = config;
    }

    @Override
    public Optional<User> authenticate(String email, String password) {
        String sql = """
                SELECT id_usuario, nombre_completo, correo, codigo_estudiante, rol
                FROM usuarios
                WHERE correo = ? AND password_hash = HASHBYTES('SHA2_256', ?) AND activo = 1
                """;
        try (Connection connection = connection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            statement.setString(2, password);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? Optional.of(readUser(result)) : Optional.empty();
            }
        } catch (SQLException exception) {
            throw failure("No se pudo iniciar sesión", exception);
        }
    }

    @Override
    public User register(String fullName, String email, String studentCode, String password) {
        String sql = """
                INSERT INTO usuarios(nombre_completo, correo, codigo_estudiante, password_hash, rol)
                OUTPUT inserted.id_usuario
                VALUES (?, ?, ?, HASHBYTES('SHA2_256', ?), 'ESTUDIANTE')
                """;
        try (Connection connection = connection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, fullName);
            statement.setString(2, email);
            statement.setString(3, studentCode);
            statement.setString(4, password);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    throw new SQLException("SQL Server no devolvió el usuario creado");
                }
                return new User(result.getLong(1), fullName, email, studentCode, Role.ESTUDIANTE);
            }
        } catch (SQLException exception) {
            if (exception.getErrorCode() == 2627 || exception.getErrorCode() == 2601) {
                throw new IllegalArgumentException("El correo o código ya se encuentra registrado.");
            }
            throw failure("No se pudo registrar el usuario", exception);
        }
    }

    @Override
    public List<LostItem> findItems() {
        String sql = """
                SELECT id_objeto, titulo, descripcion, categoria, lugar_hallazgo, fecha_hallazgo,
                       estado, ruta_imagen, pregunta_verificacion
                FROM objetos_encontrados
                ORDER BY fecha_hallazgo DESC, id_objeto DESC
                """;
        List<LostItem> items = new ArrayList<>();
        try (Connection connection = connection(); PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                items.add(readItem(result));
            }
            return items;
        } catch (SQLException exception) {
            throw failure("No se pudo consultar el catálogo", exception);
        }
    }

    @Override
    public LostItem saveItem(LostItem item, long userId) {
        String sql = """
                INSERT INTO objetos_encontrados(titulo, descripcion, categoria, lugar_hallazgo, fecha_hallazgo,
                                                estado, ruta_imagen, pregunta_verificacion, publicado_por)
                OUTPUT inserted.id_objeto
                VALUES (?, ?, ?, ?, ?, 'DISPONIBLE', ?, ?, ?)
                """;
        try (Connection connection = connection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, item.title());
            statement.setString(2, item.description());
            statement.setString(3, item.category());
            statement.setString(4, item.location());
            statement.setDate(5, Date.valueOf(item.foundDate()));
            statement.setString(6, item.imagePath());
            statement.setString(7, item.verificationQuestion());
            statement.setLong(8, userId);
            try (ResultSet result = statement.executeQuery()) {
                result.next();
                return new LostItem(result.getLong(1), item.title(), item.description(), item.category(), item.location(),
                        item.foundDate(), ItemStatus.DISPONIBLE, item.imagePath(), item.verificationQuestion());
            }
        } catch (SQLException exception) {
            throw failure("No se pudo publicar el objeto", exception);
        }
    }

    @Override
    public List<Claim> findClaims(long userId, boolean adminView) {
        String sql = """
                SELECT r.id_reclamo, r.id_objeto, o.titulo, u.nombre_completo, r.fecha_reclamo,
                       r.estado, r.respuesta_verificacion, r.respuesta_admin
                FROM reclamos r
                JOIN objetos_encontrados o ON o.id_objeto = r.id_objeto
                JOIN usuarios u ON u.id_usuario = r.id_usuario
                WHERE (? = 1 OR r.id_usuario = ?)
                ORDER BY r.fecha_reclamo DESC
                """;
        List<Claim> claims = new ArrayList<>();
        try (Connection connection = connection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, adminView);
            statement.setLong(2, userId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    claims.add(readClaim(result));
                }
            }
            return claims;
        } catch (SQLException exception) {
            throw failure("No se pudieron consultar los reclamos", exception);
        }
    }

    @Override
    public Claim saveClaim(long itemId, long userId, String answer) {
        String sql = """
                INSERT INTO reclamos(id_objeto, id_usuario, respuesta_verificacion, estado, respuesta_admin)
                OUTPUT inserted.id_reclamo, inserted.fecha_reclamo
                VALUES (?, ?, ?, 'PENDIENTE', N'Pendiente de revisión')
                """;
        try (Connection connection = connection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, itemId);
                statement.setLong(2, userId);
                statement.setString(3, answer);
                long id;
                Timestamp created;
                try (ResultSet result = statement.executeQuery()) {
                    result.next();
                    id = result.getLong(1);
                    created = result.getTimestamp(2);
                }
                try (PreparedStatement update = connection.prepareStatement(
                        "UPDATE objetos_encontrados SET estado = 'EN_RECLAMO' WHERE id_objeto = ?")) {
                    update.setLong(1, itemId);
                    update.executeUpdate();
                }
                String title;
                String claimant;
                try (PreparedStatement lookup = connection.prepareStatement("""
                        SELECT o.titulo, u.nombre_completo FROM objetos_encontrados o
                        CROSS JOIN usuarios u WHERE o.id_objeto = ? AND u.id_usuario = ?
                        """)) {
                    lookup.setLong(1, itemId);
                    lookup.setLong(2, userId);
                    try (ResultSet result = lookup.executeQuery()) {
                        result.next();
                        title = result.getString(1);
                        claimant = result.getString(2);
                    }
                }
                connection.commit();
                return new Claim(id, itemId, title, claimant, created.toLocalDateTime(), ClaimStatus.PENDIENTE,
                        answer, "Pendiente de revisión");
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            }
        } catch (SQLException exception) {
            throw failure("No se pudo crear el reclamo", exception);
        }
    }

    @Override
    public void updateClaim(long claimId, ClaimStatus status, String adminResponse, long adminId) {
        String sql = """
                UPDATE reclamos SET estado = ?, respuesta_admin = ?, revisado_por = ?, fecha_revision = SYSDATETIME()
                WHERE id_reclamo = ?
                """;
        try (Connection connection = connection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status.name());
            statement.setString(2, adminResponse);
            statement.setLong(3, adminId);
            statement.setLong(4, claimId);
            if (statement.executeUpdate() == 0) {
                throw new IllegalArgumentException("No se encontró el reclamo.");
            }
        } catch (SQLException exception) {
            throw failure("No se pudo actualizar el reclamo", exception);
        }
    }

    @Override
    public List<Notice> findNotices(long userId) {
        String sql = """
                SELECT a.id_aviso, a.mensaje, a.fecha_aviso, COALESCE(o.titulo, N'Sistema') AS objeto, a.leido
                FROM avisos a LEFT JOIN objetos_encontrados o ON o.id_objeto = a.id_objeto
                WHERE a.id_usuario = ? ORDER BY a.fecha_aviso DESC
                """;
        List<Notice> notices = new ArrayList<>();
        try (Connection connection = connection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    notices.add(new Notice(result.getLong(1), result.getString(2), result.getTimestamp(3).toLocalDateTime(),
                            result.getString(4), result.getBoolean(5)));
                }
            }
            return notices;
        } catch (SQLException exception) {
            throw failure("No se pudieron consultar los avisos", exception);
        }
    }

    @Override
    public List<AuditEntry> findAuditEntries() {
        String sql = """
                SELECT b.id_bitacora, COALESCE(u.nombre_completo, N'Sistema') AS usuario, b.accion,
                       b.fecha_accion, b.elemento_relacionado
                FROM bitacora b LEFT JOIN usuarios u ON u.id_usuario = b.id_usuario
                ORDER BY b.fecha_accion DESC
                """;
        List<AuditEntry> entries = new ArrayList<>();
        try (Connection connection = connection(); PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                entries.add(new AuditEntry(result.getLong(1), result.getString(2), result.getString(3),
                        result.getTimestamp(4).toLocalDateTime(), result.getString(5)));
            }
            return entries;
        } catch (SQLException exception) {
            throw failure("No se pudo consultar la bitácora", exception);
        }
    }

    @Override
    public DashboardStats dashboardStats() {
        String sql = """
                SELECT
                  SUM(CASE WHEN estado = 'DISPONIBLE' THEN 1 ELSE 0 END) disponibles,
                  SUM(CASE WHEN estado = 'DEVUELTO' THEN 1 ELSE 0 END) devueltos
                FROM objetos_encontrados;
                SELECT COUNT(*) pendientes FROM reclamos WHERE estado = 'PENDIENTE';
                SELECT categoria, COUNT(*) cantidad FROM objetos_encontrados GROUP BY categoria ORDER BY cantidad DESC;
                """;
        try (Connection connection = connection(); Statement statement = connection.createStatement()) {
            boolean hasResult = statement.execute(sql);
            long available = 0;
            long returned = 0;
            long pending = 0;
            LinkedHashMap<String, Number> categories = new LinkedHashMap<>();
            if (hasResult) {
                try (ResultSet result = statement.getResultSet()) {
                    if (result.next()) {
                        available = result.getLong(1);
                        returned = result.getLong(2);
                    }
                }
            }
            if (statement.getMoreResults()) {
                try (ResultSet result = statement.getResultSet()) {
                    if (result.next()) pending = result.getLong(1);
                }
            }
            if (statement.getMoreResults()) {
                try (ResultSet result = statement.getResultSet()) {
                    while (result.next()) categories.put(result.getString(1), result.getInt(2));
                }
            }
            return new DashboardStats(available, pending, returned, categories);
        } catch (SQLException exception) {
            throw failure("No se pudieron calcular los indicadores", exception);
        }
    }

    private Connection connection() throws SQLException {
        return DriverManager.getConnection(config.url(), config.user(), config.password());
    }

    private static User readUser(ResultSet result) throws SQLException {
        return new User(result.getLong("id_usuario"), result.getString("nombre_completo"), result.getString("correo"),
                result.getString("codigo_estudiante"), Role.valueOf(result.getString("rol")));
    }

    private static LostItem readItem(ResultSet result) throws SQLException {
        Date date = result.getDate("fecha_hallazgo");
        return new LostItem(result.getLong("id_objeto"), result.getString("titulo"), result.getString("descripcion"),
                result.getString("categoria"), result.getString("lugar_hallazgo"), date.toLocalDate(),
                ItemStatus.valueOf(result.getString("estado")), result.getString("ruta_imagen"),
                result.getString("pregunta_verificacion"));
    }

    private static Claim readClaim(ResultSet result) throws SQLException {
        return new Claim(result.getLong("id_reclamo"), result.getLong("id_objeto"), result.getString("titulo"),
                result.getString("nombre_completo"), result.getTimestamp("fecha_reclamo").toLocalDateTime(),
                ClaimStatus.valueOf(result.getString("estado")), result.getString("respuesta_verificacion"),
                result.getString("respuesta_admin"));
    }

    private static RepositoryException failure(String message, SQLException exception) {
        return new RepositoryException(message, exception);
    }
}
