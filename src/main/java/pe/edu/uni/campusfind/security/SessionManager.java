package pe.edu.uni.campusfind.security;

import pe.edu.uni.campusfind.model.Usuario;

public class SessionManager {
    // Usamos el patrón Singleton para que solo exista una sesión en toda la app
    private static SessionManager instancia;
    private Usuario usuarioActual;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instancia == null) {
            instancia = new SessionManager();
        }
        return instancia;
    }

    public void login(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    public void logout() {
        this.usuarioActual = null;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public boolean haySesionActiva() {
        return usuarioActual != null;
    }
}