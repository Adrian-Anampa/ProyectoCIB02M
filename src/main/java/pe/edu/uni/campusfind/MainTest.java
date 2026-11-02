package pe.edu.uni.campusfind;

import pe.edu.uni.campusfind.repository.FakeUsuarioRepository;
import pe.edu.uni.campusfind.security.SessionManager;
import pe.edu.uni.campusfind.service.AuthService;

public class MainTest {
    public static void main(String[] args) {
        // 1. Inicializamos nuestra "base de datos" y el servicio
        FakeUsuarioRepository repositorio = new FakeUsuarioRepository();
        AuthService authService = new AuthService(repositorio);
        SessionManager sesion = SessionManager.getInstance();

        System.out.println("--- PRUEBA 1: Login de Administrador por defecto ---");
        boolean loginAdmin = authService.iniciarSesion("admin@uni.edu.pe", "123456");
        System.out.println("¿Login exitoso?: " + loginAdmin);

        if (sesion.haySesionActiva()) {
            System.out.println("Usuario activo: " + sesion.getUsuarioActual().getNombre());
            System.out.println("Rol: " + sesion.getUsuarioActual().getRol());
        }

        sesion.logout(); // Cerramos sesión para la siguiente prueba

        System.out.println("\n--- PRUEBA 2: Registro de nuevo estudiante ---");
        boolean registro = authService.registrarUsuario("Renato", "Arbieto", "renato@uni.edu.pe", "secreta123");
        System.out.println("¿Registro exitoso?: " + registro);

        System.out.println("\n--- PRUEBA 3: Intento de correo duplicado ---");
        boolean registroDuplicado = authService.registrarUsuario("Otro", "Estudiante", "renato@uni.edu.pe", "clave321");
        System.out.println("¿Registro con correo duplicado falló correctamente?: " + !registroDuplicado);

        System.out.println("\n--- PRUEBA 4: Login con el nuevo usuario ---");
        boolean loginNuevo = authService.iniciarSesion("renato@uni.edu.pe", "secreta123");
        System.out.println("¿Login del nuevo usuario exitoso?: " + loginNuevo);

        if (sesion.haySesionActiva()) {
            System.out.println("Bienvenido, " + sesion.getUsuarioActual().getNombre() + " (" + sesion.getUsuarioActual().getRol() + ")");
        }
    }
}