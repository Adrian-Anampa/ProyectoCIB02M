package pe.edu.objetoshallados.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import pe.edu.objetoshallados.config.AppContext;
import pe.edu.objetoshallados.model.LostItem;
import pe.edu.objetoshallados.session.UserSession;
import pe.edu.objetoshallados.util.Navigator;

import java.io.File;

public final class EditItemController extends BaseController {
    @FXML private TextField titleField;
    @FXML private TextArea descriptionArea;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private TextField locationField;
    @FXML private DatePicker datePicker;
    @FXML private TextField questionField;
    @FXML private TextField imagePathField;
    @FXML private ImageView previewImage;
    @FXML private Label messageLabel;

    private LostItem item;

    @FXML
    private void initialize() {
        item = UserSession.selectedItem();
        categoryCombo.setItems(FXCollections.observableArrayList("Mochilas", "Tecnología", "Accesorios", "Útiles", "Ropa", "Documentos", "Otros"));
        if (item == null || !AppContext.items().canEdit(item, UserSession.get().id())) {
            messageLabel.setText("Selecciona una publicación propia disponible.");
            return;
        }
        titleField.setText(item.title());
        descriptionArea.setText(item.description());
        categoryCombo.setValue(item.category());
        locationField.setText(item.location());
        datePicker.setValue(item.foundDate());
        questionField.setText(item.verificationQuestion());
        if (item.imagePath() != null && !item.imagePath().isBlank()) {
            File file = new File(item.imagePath());
            if (file.isFile()) previewImage.setImage(new Image(file.toURI().toString(), true));
        }
    }

    @FXML
    private void onSelectImage() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Reemplazar fotografía");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes PNG o JPG", "*.png", "*.jpg", "*.jpeg"));
        File selected = chooser.showOpenDialog(Navigator.stage());
        if (selected != null) {
            imagePathField.setText(selected.getAbsolutePath());
            previewImage.setImage(new Image(selected.toURI().toString(), true));
        }
    }

    @FXML
    private void onSave() {
        try {
            if (item == null) throw new IllegalArgumentException("Selecciona un objeto válido.");
            LostItem updated = AppContext.items().edit(item.id(), titleField.getText(), descriptionArea.getText(),
                    categoryCombo.getValue(), locationField.getText(), datePicker.getValue(), imagePathField.getText(),
                    questionField.getText(), UserSession.get().id());
            UserSession.selectItem(updated);
            Navigator.goTo("detalle-objeto.fxml");
        } catch (IllegalArgumentException exception) {
            messageLabel.setText(exception.getMessage());
        } catch (RuntimeException exception) {
            messageLabel.setText("No se pudo guardar la edición.");
        }
    }

    @Override
    public void onBack() {
        Navigator.goTo("detalle-objeto.fxml");
    }
}
