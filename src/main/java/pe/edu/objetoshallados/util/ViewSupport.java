package pe.edu.objetoshallados.util;

import javafx.scene.control.Button;
import pe.edu.objetoshallados.model.Role;
import pe.edu.objetoshallados.session.UserSession;
import pe.edu.uni.campusfind.security.SessionManager;

public final class ViewSupport {
    private ViewSupport() {}

    public static void configureAdminButton(Button button) {
        boolean admin = UserSession.get().role() == Role.ADMINISTRADOR;
        button.setVisible(admin);
        button.setManaged(admin);
    }

    public static String currentUserLabel() {
        return UserSession.get().fullName() + " · " + UserSession.get().role().label();
    }

    public static void logout() {
        SessionManager.getInstance().logout();
        UserSession.clear();
        Navigator.goTo("login.fxml");
    }
}
