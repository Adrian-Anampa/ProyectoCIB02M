package pe.edu.objetoshallados.controller;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import pe.edu.objetoshallados.config.AppContext;
import pe.edu.objetoshallados.model.User;
import pe.edu.objetoshallados.session.UserSession;
import pe.edu.objetoshallados.util.Navigator;

import java.util.Optional;

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
        setBusy(true);
        Task<Optional<User>> task = new Task<>() {
            @Override protected Optional<User> call() {
                return AppContext.auth().authenticate(emailField.getText(), passwordField.getText());
            }
        };
        task.setOnSucceeded(event -> {
            setBusy(false);
            Optional<User> user = task.getValue();
            if (user.isEmpty()) {
                showError("Correo o contraseña incorrectos.");
                return;
            }
            UserSession.start(user.get());
            Navigator.goTo("menu-principal.fxml");
        });
        task.setOnFailed(event -> {
            setBusy(false);
            Throwable error = task.getException();
            showError(error instanceof IllegalArgumentException ? error.getMessage() : "No fue posible iniciar sesión.");
        });
        Thread.startVirtualThread(task);
    }

    @FXML
    private void onRegister() {
        Navigator.goTo("registro.fxml");
    }

    @FXML
    private void useStudentDemo() {
        emailField.setText("estudiante@demo.pe");
        passwordField.setText("Demo123*");
    }

    @FXML
    private void useAdminDemo() {
        emailField.setText("admin@demo.pe");
        passwordField.setText("Admin123*");
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
