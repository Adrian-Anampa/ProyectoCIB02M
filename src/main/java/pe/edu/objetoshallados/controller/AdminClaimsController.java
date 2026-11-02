package pe.edu.objetoshallados.controller;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import pe.edu.objetoshallados.config.AppContext;
import pe.edu.objetoshallados.model.Claim;
import pe.edu.objetoshallados.model.ClaimStatus;
import pe.edu.objetoshallados.session.UserSession;
import pe.edu.objetoshallados.util.Dialogs;
import pe.edu.objetoshallados.util.Navigator;

import java.time.format.DateTimeFormatter;

public final class AdminClaimsController extends BaseController {
    @FXML private TableView<Claim> claimsTable;
    @FXML private TableColumn<Claim, String> idColumn;
    @FXML private TableColumn<Claim, String> objectColumn;
    @FXML private TableColumn<Claim, String> claimantColumn;
    @FXML private TableColumn<Claim, String> dateColumn;
    @FXML private TableColumn<Claim, String> statusColumn;
    @FXML private TextArea answerArea;
    @FXML private TextArea responseArea;
    @FXML private Label messageLabel;

    @FXML
    private void initialize() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        idColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper("REC-" + String.format("%05d", cell.getValue().id())));
        objectColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().itemTitle()));
        claimantColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().claimantName()));
        dateColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().createdAt().format(formatter)));
        statusColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().status().label()));
        claimsTable.setPlaceholder(new Label("No hay reclamos registrados."));
        claimsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, claim) -> {
            answerArea.setText(claim == null ? "" : claim.answer());
            responseArea.setText(claim == null ? "" : claim.adminResponse());
        });
        refresh();
    }

    @FXML private void onApprove() { resolve(ClaimStatus.APROBADO); }
    @FXML private void onReject() { resolve(ClaimStatus.RECHAZADO); }

    @FXML
    private void onOpenDetail() {
        Claim claim = claimsTable.getSelectionModel().getSelectedItem();
        if (claim == null) {
            messageLabel.setText("Selecciona un reclamo.");
            return;
        }
        UserSession.selectClaim(claim.id());
        Navigator.goTo("detalle-reclamo.fxml");
    }

    private void resolve(ClaimStatus status) {
        Claim claim = claimsTable.getSelectionModel().getSelectedItem();
        if (claim == null) {
            messageLabel.setText("Selecciona el reclamo que deseas resolver.");
            return;
        }
        String action = status == ClaimStatus.APROBADO ? "aprobar" : "rechazar";
        try {
            if (!Dialogs.confirm("Confirmar decisión", "¿Deseas " + action + " este reclamo?")) return;
            AppContext.admin().resolveClaim(claim.id(), status, responseArea.getText(), UserSession.get().id());
            Dialogs.info("Reclamo actualizado", "La decisión fue guardada correctamente.");
            refresh();
        } catch (IllegalArgumentException exception) {
            messageLabel.setText(exception.getMessage());
        } catch (RuntimeException exception) {
            messageLabel.setText("No fue posible actualizar el reclamo.");
        }
    }

    private void refresh() {
        claimsTable.setItems(FXCollections.observableArrayList(AppContext.claims().all()));
    }
}
