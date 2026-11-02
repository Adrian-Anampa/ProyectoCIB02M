package pe.edu.objetoshallados.controller;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import pe.edu.objetoshallados.config.AppContext;
import pe.edu.objetoshallados.model.Claim;
import pe.edu.objetoshallados.session.UserSession;
import pe.edu.objetoshallados.util.Navigator;

import java.time.format.DateTimeFormatter;

public final class MyClaimsController extends BaseController {
    @FXML private TableView<Claim> claimsTable;
    @FXML private TableColumn<Claim, String> objectColumn;
    @FXML private TableColumn<Claim, String> dateColumn;
    @FXML private TableColumn<Claim, String> statusColumn;
    @FXML private TableColumn<Claim, String> responseColumn;
    @FXML private Label messageLabel;

    @FXML
    private void initialize() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        objectColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().itemTitle()));
        dateColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().createdAt().format(formatter)));
        statusColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().status().label()));
        responseColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().adminResponse()));
        claimsTable.setPlaceholder(new Label("Todavía no has presentado reclamos."));
        claimsTable.setItems(FXCollections.observableArrayList(AppContext.claims().forUser(UserSession.get().id())));
        claimsTable.setRowFactory(table -> {
            TableRow<Claim> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) open(row.getItem());
            });
            return row;
        });
    }

    @FXML
    private void onOpenClaim() {
        Claim selected = claimsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            messageLabel.setText("Selecciona un reclamo para ver su detalle.");
            return;
        }
        open(selected);
    }

    private void open(Claim claim) {
        UserSession.selectClaim(claim.id());
        Navigator.goTo("detalle-reclamo.fxml");
    }
}
