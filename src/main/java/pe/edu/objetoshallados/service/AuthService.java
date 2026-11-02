package pe.edu.objetoshallados.service;

import pe.edu.objetoshallados.model.User;
import pe.edu.objetoshallados.repository.AppRepository;

import java.util.Optional;
import java.util.regex.Pattern;

public final class AuthService {
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private final AppRepository repository;

    public AuthService(AppRepository repository) {
        this.repository = repository;
    }

    public Optional<User> authenticate(String email, String password) {
        requireEmail(email);
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Ingresa tu contraseña.");
        }
        return repository.authenticate(email.trim(), password);
    }

    public User register(String fullName, String email, String studentCode, String password, String confirmation) {
        if (fullName == null || fullName.trim().length() < 3) {
            throw new IllegalArgumentException("Ingresa tu nombre completo.");
        }
        requireEmail(email);
        if (studentCode == null || studentCode.trim().length() < 4) {
            throw new IllegalArgumentException("Ingresa un código de estudiante válido.");
        }
        if (password == null || password.length() < 8 || password.chars().noneMatch(Character::isUpperCase)
                || password.chars().noneMatch(Character::isDigit)) {
            throw new IllegalArgumentException("La contraseña debe tener 8 caracteres, una mayúscula y un número.");
        }
        if (!password.equals(confirmation)) {
            throw new IllegalArgumentException("Las contraseñas no coinciden.");
        }
        return repository.register(fullName.trim(), email.trim(), studentCode.trim(), password);
    }

    private static void requireEmail(String email) {
        if (email == null || !EMAIL.matcher(email.trim()).matches()) {
            throw new IllegalArgumentException("Ingresa un correo válido.");
        }
    }
}
