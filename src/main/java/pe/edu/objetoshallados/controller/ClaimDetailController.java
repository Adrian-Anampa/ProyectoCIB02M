package pe.edu.objetoshallados.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import pe.edu.objetoshallados.config.AppContext;
import pe.edu.objetoshallados.model.Claim;
import pe.edu.objetoshallados.model.Role;
import pe.edu.objetoshallados.session.UserSession;
import pe.edu.objetoshallados.util.Navigator;

import java.time.format.DateTimeFormatter;

public final class ClaimDetailController extends BaseController {
    @FXML private Label claimIdLabel;
    @FXML private Label objectLabel;
    @FXML private Label claimantLabel;
    @FXML private Label dateLabel;
    @FXML private Label statusLabel;
    @FXML private Label answerLabel;
    @FXML private Label responseLabel;

    @FXML
    private void initialize() {
        boolean admin = UserSession.get().role() == Role.ADMINISTRADOR;
        Claim claim = AppContext.claims().find(UserSession.selectedClaimId(), UserSession.get().id(), admin).orElse(null);
        if (claim == null) return;
        claimIdLabel.setText("REC-" + String.format("%05d", claim.id()));
        objectLabel.setText(claim.itemTitle());
        claimantLabel.setText(claim.claimantName());
        dateLabel.setText(claim.createdAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        statusLabel.setText(claim.status().label());
        answerLabel.setText(claim.answer());
        responseLabel.setText(claim.adminResponse());
    }

    @Override
    public void onBack() {
        Navigator.goTo(UserSession.get().role() == Role.ADMINISTRADOR ? "reclamos-admin.fxml" : "mis-reclamos.fxml");
    }
}
