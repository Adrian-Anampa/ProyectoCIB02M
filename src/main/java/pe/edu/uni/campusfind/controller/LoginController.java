package pe.edu.uni.campusfind.controller;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import pe.edu.objetoshallados.config.AppContext;
import pe.edu.objetoshallados.model.Role;
import pe.edu.objetoshallados.model.User;
import pe.edu.objetoshallados.session.UserSession;
import pe.edu.objetoshallados.util.Navigator;
import pe.edu.uni.campusfind.model.Rol;
import pe.edu.uni.campusfind.model.Usuario;
import pe.edu.uni.campusfind.security.SessionManager;

public final class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField visiblePasswordField;
    @FXML private CheckBox showPasswordCheck;
    @FXML private Label messageLabel;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Button loginButton;

    @FXML
    private void initialize() {
        visiblePasswordField.textProperty().bindBidirectional(passwordField.textProperty());
        visiblePasswordField.managedProperty().bind(visiblePasswordField.visibleProperty());
        passwordField.managedProperty().bind(passwordField.visibleProperty());
        showPasswordCheck.selectedProperty().addListener((observable, oldValue, visible) -> {
            visiblePasswordField.setVisible(visible);
            passwordField.setVisible(!visible);
            if (visible) visiblePasswordField.requestFocus(); else passwordField.requestFocus();
        });
        loadingIndicator.setVisible(false);
    }

    @FXML
    private void onLogin() {
        messageLabel.setText("");
        if (emailField.getText() == null || emailField.getText().isBlank()
                || passwordField.getText() == null || passwordField.getText().isBlank()) {
            showError("Ingresa tu correo y contraseña.");
            return;
        }

        setBusy(true);
        Task<Boolean> task = new Task<>() {
            @Override protected Boolean call() {
                return AppContext.auth().iniciarSesion(emailField.getText().trim(), passwordField.getText());
            }
        };
        task.setOnSucceeded(event -> {
            setBusy(false);
            if (!task.getValue()) {
                showError("Correo o contraseña incorrectos.");
                return;
            }
            Usuario usuario = SessionManager.getInstance().getUsuarioActual();
            if (usuario == null) {
                showError("No fue posible iniciar la sesión.");
                return;
            }
            UserSession.start(toInterfaceUser(usuario));
            Navigator.goTo("menu-principal.fxml");
        });
        task.setOnFailed(event -> {
            setBusy(false);
            showError("No fue posible iniciar sesión.");
        });
        Thread.startVirtualThread(task);
    }

    @FXML
    private void onRegister() {
        Navigator.goTo("registro.fxml");
    }

    @FXML
    private void useAdminDemo() {
        emailField.setText("admin@uni.edu.pe");
        passwordField.setText("123456");
    }

    private static User toInterfaceUser(Usuario usuario) {
        String fullName = (usuario.getNombre() + " " + usuario.getApellido()).trim();
        Role role = usuario.getRol() == Rol.ADMINISTRADOR ? Role.ADMINISTRADOR : Role.ESTUDIANTE;
        return new User(usuario.getIdUsuario(), fullName, usuario.getCorreo(), "-", role);
    }

    private void setBusy(boolean busy) {
        loginButton.setDisable(busy);
        loadingIndicator.setVisible(busy);
    }

    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.getStyleClass().setAll("form-message", "message-error");
    }
}
