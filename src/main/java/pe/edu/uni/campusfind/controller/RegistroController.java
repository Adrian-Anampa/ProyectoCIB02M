package pe.edu.uni.campusfind.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import pe.edu.objetoshallados.config.AppContext;
import pe.edu.objetoshallados.util.Dialogs;
import pe.edu.objetoshallados.util.Navigator;

public final class RegistroController {
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmationField;
    @FXML private Label messageLabel;

    @FXML
    private void onRegister() {
        String firstName = value(firstNameField);
        String lastName = value(lastNameField);
        String email = value(emailField);
        String password = passwordField.getText();
        String confirmation = confirmationField.getText();

        if (firstName.isBlank() || lastName.isBlank() || email.isBlank()
                || password == null || password.isBlank()) {
            showError("Todos los campos son obligatorios.");
            return;
        }
        if (!password.equals(confirmation)) {
            showError("Las contraseñas no coinciden.");
            return;
        }

        try {
            boolean registered = AppContext.auth().registrarUsuario(firstName, lastName, email, password);
            if (!registered) {
                showError("El correo ya está registrado.");
                return;
            }
            Dialogs.info("Cuenta creada", "La cuenta fue creada correctamente. Ya puedes iniciar sesión.");
            Navigator.goTo("login.fxml");
        } catch (RuntimeException exception) {
            showError("No fue posible completar el registro.");
        }
    }

    @FXML
    private void onBack() {
        Navigator.goTo("login.fxml");
    }

    private static String value(TextField field) {
        return field.getText() == null ? "" : field.getText().trim();
    }

    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.getStyleClass().setAll("form-message", "message-error");
    }
}
