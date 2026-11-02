package pe.edu.objetoshallados.controller;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import pe.edu.objetoshallados.config.AppContext;
import pe.edu.objetoshallados.model.Notice;
import pe.edu.objetoshallados.session.UserSession;

import java.time.format.DateTimeFormatter;

public final class NoticesController extends BaseController {
    @FXML private TableView<Notice> noticesTable;
    @FXML private TableColumn<Notice, String> stateColumn;
    @FXML private TableColumn<Notice, String> messageColumn;
    @FXML private TableColumn<Notice, String> objectColumn;
    @FXML private TableColumn<Notice, String> dateColumn;

    @FXML
    private void initialize() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        stateColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().read() ? "Leído" : "Pendiente"));
        messageColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().message()));
        objectColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().itemTitle()));
        dateColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().createdAt().format(formatter)));
        stateColumn.setCellFactory(column -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                getStyleClass().removeAll("notice-pending", "notice-read");
                if (!empty) getStyleClass().add("Pendiente".equals(item) ? "notice-pending" : "notice-read");
            }
        });
        noticesTable.setPlaceholder(new Label("No tienes avisos por el momento."));
        noticesTable.setItems(FXCollections.observableArrayList(AppContext.claims().notices(UserSession.get().id())));
    }
}
