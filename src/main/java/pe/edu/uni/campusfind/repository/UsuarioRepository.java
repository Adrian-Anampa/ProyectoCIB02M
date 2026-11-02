package pe.edu.uni.campusfind.repository;

import pe.edu.uni.campusfind.model.Usuario;
import java.util.Optional;

public interface UsuarioRepository {
    // Busca un usuario por su correo. Usamos Optional por si no existe.
    Optional<Usuario> buscarPorCorreo(String correo);

    // Guarda un nuevo usuario en la base de datos
    void guardar(Usuario usuario);

    // Actualiza los datos de un usuario (ej. si se bloquea o cambia contraseña)
    void actualizar(Usuario usuario);
}