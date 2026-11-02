package pe.edu.uni.campusfind.controller;

import pe.edu.uni.campusfind.repository.FakeUsuarioRepository;
import pe.edu.uni.campusfind.service.AuthService;

public class LoginController {

    private final AuthService authService;

    public LoginController() {
        // Por ahora instanciamos el repositorio falso directamente aquí.
        // Cuando Adrian termine SQL Server, solo cambiaremos esta línea.
        this.authService = new AuthService(new FakeUsuarioRepository());
    }

    // Oscar conectará el botón "Iniciar Sesión" a este método
    public void intentarLogin(String correoIngresado, String passwordIngresada) {

        boolean exito = authService.iniciarSesion(correoIngresado, passwordIngresada);

        if (exito) {
            System.out.println("¡Éxito! Aquí JavaFX cambiará a la pantalla del Catálogo.");
            // TODO: Agregar código de JavaFX (Scene) para cambiar de ventana
        } else {
            System.out.println("Error: Correo o contraseña incorrectos.");
            // TODO: Agregar código de JavaFX para mostrar un texto rojo en pantalla
        }
    }
}