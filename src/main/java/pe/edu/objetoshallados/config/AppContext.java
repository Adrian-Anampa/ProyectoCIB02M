package pe.edu.objetoshallados.config;

import pe.edu.objetoshallados.repository.AppRepository;
import pe.edu.objetoshallados.repository.MockAppRepository;
import pe.edu.objetoshallados.repository.SqlServerAppRepository;
import pe.edu.objetoshallados.service.*;

public final class AppContext {
    private static final DatabaseConfig CONFIG = DatabaseConfig.load();
    private static final AppRepository REPOSITORY = "sqlserver".equalsIgnoreCase(CONFIG.mode())
            ? new SqlServerAppRepository(CONFIG)
            : new MockAppRepository();
    private static final AuthService AUTH = new AuthService(REPOSITORY);
    private static final ItemService ITEMS = new ItemService(REPOSITORY);
    private static final ClaimService CLAIMS = new ClaimService(REPOSITORY);
    private static final AdminService ADMIN = new AdminService(REPOSITORY);

    private AppContext() {}

    public static AuthService auth() { return AUTH; }
    public static ItemService items() { return ITEMS; }
    public static ClaimService claims() { return CLAIMS; }
    public static AdminService admin() { return ADMIN; }
    public static String mode() { return CONFIG.mode(); }
}
