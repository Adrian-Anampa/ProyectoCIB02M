package pe.edu.objetoshallados.controller;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import pe.edu.objetoshallados.config.AppContext;
import pe.edu.objetoshallados.model.AuditEntry;

import java.time.format.DateTimeFormatter;

public final class AuditController extends BaseController {
    @FXML private TableView<AuditEntry> auditTable;
    @FXML private TableColumn<AuditEntry, String> userColumn;
    @FXML private TableColumn<AuditEntry, String> actionColumn;
    @FXML private TableColumn<AuditEntry, String> dateColumn;
    @FXML private TableColumn<AuditEntry, String> elementColumn;

    @FXML
    private void initialize() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        userColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().user()));
        actionColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().action()));
        dateColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().createdAt().format(formatter)));
        elementColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().relatedElement()));
        auditTable.setPlaceholder(new Label("No existen acciones registradas."));
        auditTable.setItems(FXCollections.observableArrayList(AppContext.admin().audit()));
    }
}
