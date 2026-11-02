package pe.edu.objetoshallados.config;

import pe.edu.objetoshallados.repository.AppRepository;
import pe.edu.objetoshallados.repository.MockAppRepository;
import pe.edu.objetoshallados.repository.SqlServerAppRepository;
import pe.edu.objetoshallados.service.AdminService;
import pe.edu.objetoshallados.service.ClaimService;
import pe.edu.objetoshallados.service.ItemService;
import pe.edu.objetoshallados.service.QrService;
import pe.edu.objetoshallados.service.NoticeService;
import pe.edu.objetoshallados.service.IndicatorService;
import pe.edu.uni.campusfind.repository.FakeUsuarioRepository;
import pe.edu.uni.campusfind.repository.UsuarioRepository;
import pe.edu.uni.campusfind.service.AuthService;

public final class AppContext {
    private static final DatabaseConfig CONFIG = DatabaseConfig.load();
    private static final AppRepository REPOSITORY = "sqlserver".equalsIgnoreCase(CONFIG.mode())
            ? new SqlServerAppRepository(CONFIG)
            : new MockAppRepository();
    private static final UsuarioRepository USER_REPOSITORY = new FakeUsuarioRepository();
    private static final AuthService AUTH = new AuthService(USER_REPOSITORY);
    private static final ItemService ITEMS = new ItemService(REPOSITORY);
    private static final ClaimService CLAIMS = new ClaimService(REPOSITORY);
    private static final AdminService ADMIN = new AdminService(REPOSITORY);
    private static final QrService QR = new QrService();
    private static final NoticeService NOTICES = new NoticeService(REPOSITORY);
    private static final IndicatorService INDICATORS = new IndicatorService(REPOSITORY);

    private AppContext() {}

    public static AuthService auth() { return AUTH; }
    public static ItemService items() { return ITEMS; }
    public static ClaimService claims() { return CLAIMS; }
    public static AdminService admin() { return ADMIN; }
    public static QrService qr() { return QR; }
    public static NoticeService notices() { return NOTICES; }
    public static IndicatorService indicators() { return INDICATORS; }
    public static String mode() { return CONFIG.mode(); }
}
