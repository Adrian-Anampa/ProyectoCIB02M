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
    @FXML private Label messageLabel;

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
        refresh();
    }

    @FXML
    private void onMarkRead() {
        Notice selected = noticesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            messageLabel.setText("Selecciona un aviso.");
            return;
        }
        try {
            AppContext.notices().markRead(selected.id(), UserSession.get().id());
            messageLabel.setText("Aviso marcado como leído.");
            refresh();
        } catch (RuntimeException exception) {
            messageLabel.setText("No se pudo actualizar el aviso.");
        }
    }

    private void refresh() {
        noticesTable.setItems(FXCollections.observableArrayList(AppContext.notices().forUser(UserSession.get().id())));
    }
}
