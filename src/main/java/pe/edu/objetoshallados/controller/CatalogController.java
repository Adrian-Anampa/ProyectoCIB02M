package pe.edu.objetoshallados.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import pe.edu.objetoshallados.config.AppContext;
import pe.edu.objetoshallados.model.ItemStatus;
import pe.edu.objetoshallados.model.LostItem;
import pe.edu.objetoshallados.session.UserSession;
import pe.edu.objetoshallados.util.Navigator;

import java.time.LocalDate;

public final class CatalogController extends BaseController {
    @FXML private TextField searchField;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private TextField locationField;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<ItemStatus> statusCombo;
    @FXML private TableView<LostItem> itemsTable;
    @FXML private TableColumn<LostItem, String> titleColumn;
    @FXML private TableColumn<LostItem, String> categoryColumn;
    @FXML private TableColumn<LostItem, String> locationColumn;
    @FXML private TableColumn<LostItem, LocalDate> dateColumn;
    @FXML private TableColumn<LostItem, String> statusColumn;
    @FXML private Label resultLabel;

    @FXML
    private void initialize() {
        categoryCombo.setItems(FXCollections.observableArrayList("Todas", "Mochilas", "Tecnología", "Accesorios", "Útiles", "Ropa", "Documentos", "Otros"));
        categoryCombo.getSelectionModel().selectFirst();
        statusCombo.setItems(FXCollections.observableArrayList(ItemStatus.values()));
        titleColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().title()));
        categoryColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().category()));
        locationColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().location()));
        dateColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().foundDate()));
        statusColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().status().label()));
        itemsTable.setPlaceholder(new Label("No encontramos objetos con esos filtros."));
        itemsTable.setRowFactory(table -> {
            TableRow<LostItem> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    openDetail(row.getItem());
                }
            });
            return row;
        });
        onSearch();
    }

    @FXML
    private void onSearch() {
        var results = AppContext.items().search(searchField.getText(), categoryCombo.getValue(), locationField.getText(),
                datePicker.getValue(), statusCombo.getValue());
        itemsTable.setItems(FXCollections.observableArrayList(results));
        resultLabel.setText(results.size() + (results.size() == 1 ? " objeto encontrado" : " objetos encontrados"));
    }

    @FXML
    private void onClear() {
        searchField.clear();
        categoryCombo.getSelectionModel().selectFirst();
        locationField.clear();
        datePicker.setValue(null);
        statusCombo.setValue(null);
        onSearch();
    }

    @FXML
    private void onOpenDetail() {
        LostItem selected = itemsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            resultLabel.setText("Selecciona un objeto para ver su detalle.");
            return;
        }
        openDetail(selected);
    }

    private void openDetail(LostItem item) {
        UserSession.selectItem(item);
        Navigator.goTo("detalle-objeto.fxml");
    }
}
