package pe.edu.objetoshallados.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import pe.edu.objetoshallados.config.AppContext;
import pe.edu.objetoshallados.model.DashboardStats;
import pe.edu.objetoshallados.util.Navigator;
import pe.edu.objetoshallados.util.ViewSupport;

public final class AdminPanelController extends BaseController {
    @FXML private Label userLabel;
    @FXML private Label availableLabel;
    @FXML private Label pendingLabel;
    @FXML private Label returnedLabel;

    @FXML
    private void initialize() {
        userLabel.setText(ViewSupport.currentUserLabel());
        DashboardStats stats = AppContext.admin().stats();
        availableLabel.setText(String.valueOf(stats.available()));
        pendingLabel.setText(String.valueOf(stats.pendingClaims()));
        returnedLabel.setText(String.valueOf(stats.returned()));
    }

    @FXML private void onAdminClaims() { Navigator.goTo("reclamos-admin.fxml"); }
    @FXML private void onIndicators() { Navigator.goTo("indicadores.fxml"); }
    @FXML private void onAudit() { Navigator.goTo("bitacora.fxml"); }
}
