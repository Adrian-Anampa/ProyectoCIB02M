package pe.edu.objetoshallados.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import pe.edu.objetoshallados.config.AppContext;
import pe.edu.objetoshallados.model.User;
import pe.edu.objetoshallados.session.UserSession;
import pe.edu.objetoshallados.util.Dialogs;
import pe.edu.objetoshallados.util.Navigator;

public final class RegisterController {
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField studentCodeField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmationField;
    @FXML private Label messageLabel;

    @FXML
    private void onRegister() {
        try {
            User user = AppContext.auth().register(fullNameField.getText(), emailField.getText(), studentCodeField.getText(),
                    passwordField.getText(), confirmationField.getText());
            UserSession.start(user);
            Dialogs.info("Cuenta creada", "Tu cuenta fue creada correctamente.");
            Navigator.goTo("menu-principal.fxml");
        } catch (IllegalArgumentException exception) {
            messageLabel.setText(exception.getMessage());
            messageLabel.getStyleClass().setAll("form-message", "message-error");
        } catch (RuntimeException exception) {
            messageLabel.setText("No fue posible completar el registro.");
        }
    }

    @FXML
    private void onBack() {
        Navigator.goTo("login.fxml");
    }
}
