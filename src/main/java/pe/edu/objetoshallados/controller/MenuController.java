package pe.edu.objetoshallados.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import pe.edu.objetoshallados.config.AppContext;
import pe.edu.objetoshallados.model.DashboardStats;
import pe.edu.objetoshallados.util.ViewSupport;

public final class MenuController extends BaseController {
    @FXML private Label userLabel;
    @FXML private Label availableLabel;
    @FXML private Label pendingLabel;
    @FXML private Label returnedLabel;
    @FXML private Label modeLabel;
    @FXML private VBox adminCard;

    @FXML
    private void initialize() {
        userLabel.setText(ViewSupport.currentUserLabel());
        boolean admin = pe.edu.objetoshallados.session.UserSession.get().role()
                == pe.edu.objetoshallados.model.Role.ADMINISTRADOR;
        adminCard.setVisible(admin);
        adminCard.setManaged(admin);
        DashboardStats stats = AppContext.admin().stats();
        availableLabel.setText(String.valueOf(stats.available()));
        pendingLabel.setText(String.valueOf(stats.pendingClaims()));
        returnedLabel.setText(String.valueOf(stats.returned()));
        modeLabel.setText("Origen de datos: " + (AppContext.mode().equalsIgnoreCase("mock") ? "demostración" : "SQL Server"));
    }
}
