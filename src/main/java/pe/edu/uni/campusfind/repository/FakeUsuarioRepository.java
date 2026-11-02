package pe.edu.uni.campusfind.repository;

import pe.edu.uni.campusfind.model.Rol;
import pe.edu.uni.campusfind.model.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.mindrot.jbcrypt.BCrypt; // Asegúrate de que esta importación esté arriba

public class FakeUsuarioRepository implements UsuarioRepository {

    // Esta lista actuará como nuestra base de datos temporal
    private final List<Usuario> baseDeDatosFalsa = new ArrayList<>();
    private int contadorId = 1;

    public FakeUsuarioRepository() {
        Usuario student = new Usuario("Ana", "Torres", "estudiante@demo.pe",
                BCrypt.hashpw("Demo123*", BCrypt.gensalt()), Rol.USUARIO);
        student.setIdUsuario(contadorId++);
        baseDeDatosFalsa.add(student);
        // Encriptamos la clave "123456" del administrador de prueba
        String adminHash = BCrypt.hashpw("123456", BCrypt.gensalt());
        Usuario admin = new Usuario("Admin", "Principal", "admin@uni.edu.pe", adminHash, Rol.ADMINISTRADOR);
        admin.setIdUsuario(contadorId++);
        baseDeDatosFalsa.add(admin);
    }

    @Override
    public Optional<Usuario> buscarPorCorreo(String correo) {
        return baseDeDatosFalsa.stream()
                .filter(u -> u.getCorreo().equalsIgnoreCase(correo))
                .findFirst();
    }

    @Override
    public void guardar(Usuario usuario) {
        usuario.setIdUsuario(contadorId++);
        baseDeDatosFalsa.add(usuario);
    }

    @Override
    public void actualizar(Usuario usuario) {
        // En una lista en memoria, si modificamos el objeto ya está actualizado,
        // pero en SQL aquí iría un UPDATE. Por ahora lo dejamos vacío.
    }
}
