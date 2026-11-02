package pe.edu.uni.campusfind.controller;

import pe.edu.uni.campusfind.repository.FakeUsuarioRepository;
import pe.edu.uni.campusfind.service.AuthService;

public class RegistroController {

    private final AuthService authService;

    public RegistroController() {
        // Al igual que en el Login, instanciamos el servicio con el repositorio temporal
        this.authService = new AuthService(new FakeUsuarioRepository());
    }

    // Oscar conectará el botón "Registrarse" de su pantalla a este método
    public void intentarRegistro(String nombre, String apellido, String correo, String password) {

        // 1. Validación rápida de campos vacíos
        if (nombre.trim().isEmpty() || apellido.trim().isEmpty() ||
                correo.trim().isEmpty() || password.trim().isEmpty()) {
            System.out.println("Error: Todos los campos son obligatorios.");
            // TODO: Mostrar alerta visual en JavaFX
            return;
        }

        // 2. Pasamos los datos a tu lógica de negocio
        boolean exito = authService.registrarUsuario(nombre, apellido, correo, password);

        if (exito) {
            System.out.println("¡Registro exitoso! Aquí JavaFX cambiará a la pantalla de Login.");
            // TODO: Código JavaFX para cambiar de escena
        } else {
            System.out.println("Error: El correo " + correo + " ya está registrado.");
            // TODO: Código JavaFX para mostrar mensaje de error en rojo
        }
    }
}