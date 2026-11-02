package pe.edu.uni.campusfind.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import pe.edu.objetoshallados.repository.MockAppRepository;
import pe.edu.uni.campusfind.model.Rol;
import pe.edu.uni.campusfind.repository.FakeUsuarioRepository;
import pe.edu.uni.campusfind.security.SessionManager;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {
    private final FakeUsuarioRepository repository = new FakeUsuarioRepository();
    private final AuthService service = new AuthService(repository);

    @AfterEach
    void clearSession() {
        SessionManager.getInstance().logout();
    }

    @Test
    void authenticatesDefaultAdministratorWithBcrypt() {
        assertTrue(service.iniciarSesion("admin@uni.edu.pe", "123456"));
        assertNotNull(SessionManager.getInstance().getUsuarioActual());
        assertEquals(Rol.ADMINISTRADOR, SessionManager.getInstance().getUsuarioActual().getRol());
    }

    @Test
    void registeredUserCanSignInThroughSameRepository() {
        assertTrue(service.registrarUsuario("Ana", "Torres", "ana@uni.edu.pe", "secreta123"));
        assertTrue(service.iniciarSesion("ana@uni.edu.pe", "secreta123"));
        assertEquals("Ana", SessionManager.getInstance().getUsuarioActual().getNombre());
    }

    @Test
    void activeUserNameIsUsedByTheInterfaceRepository() {
        assertTrue(service.registrarUsuario("Ana", "Torres", "ana@uni.edu.pe", "secreta123"));
        assertTrue(service.iniciarSesion("ana@uni.edu.pe", "secreta123"));

        var claim = new MockAppRepository().saveClaim(1,
                SessionManager.getInstance().getUsuarioActual().getIdUsuario(), "Es mi mochila azul");

        assertEquals("Ana Torres", claim.claimantName());
    }
}
