package pe.edu.uni.campusfind.service;

import org.mindrot.jbcrypt.BCrypt;
import pe.edu.uni.campusfind.model.Rol;
import pe.edu.uni.campusfind.model.Usuario;
import pe.edu.uni.campusfind.repository.UsuarioRepository;
import pe.edu.uni.campusfind.security.SessionManager;

import java.util.Optional;

public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final SessionManager sessionManager;

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.sessionManager = SessionManager.getInstance();
    }

    public boolean iniciarSesion(String correo, String password) {
        Optional<Usuario> usuarioOpt = usuarioRepository.buscarPorCorreo(correo);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            // BCrypt compara la contraseña de texto con el hash encriptado de la BD
            if (BCrypt.checkpw(password, usuario.getPasswordHash()) && !usuario.isBloqueado()) {
                sessionManager.login(usuario);
                return true;
            }
        }
        return false;
    }

    public boolean registrarUsuario(String nombre, String apellido, String correo, String password) {
        if (usuarioRepository.buscarPorCorreo(correo).isPresent()) {
            return false;
        }

        // Encriptamos la contraseña antes de crear al usuario
        String passwordEncriptada = BCrypt.hashpw(password, BCrypt.gensalt());

        Usuario nuevoUsuario = new Usuario(nombre, apellido, correo, passwordEncriptada, Rol.USUARIO);
        usuarioRepository.guardar(nuevoUsuario);
        return true;
    }
}