package pe.edu.objetoshallados.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import pe.edu.objetoshallados.config.AppContext;
import pe.edu.objetoshallados.session.UserSession;
import pe.edu.objetoshallados.util.Dialogs;
import pe.edu.objetoshallados.util.Navigator;

import java.io.File;
import java.time.LocalDate;

public final class PublishItemController extends BaseController {
    @FXML private TextField titleField;
    @FXML private TextArea descriptionArea;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private TextField locationField;
    @FXML private DatePicker datePicker;
    @FXML private TextField questionField;
    @FXML private TextField imagePathField;
    @FXML private ImageView previewImage;
    @FXML private Label previewPlaceholder;
    @FXML private Label messageLabel;

    @FXML
    private void initialize() {
        categoryCombo.setItems(FXCollections.observableArrayList("Mochilas", "Tecnología", "Accesorios", "Útiles", "Ropa", "Documentos", "Otros"));
        datePicker.setValue(LocalDate.now());
    }

    @FXML
    private void onSelectImage() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Seleccionar fotografía del objeto");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes PNG o JPG", "*.png", "*.jpg", "*.jpeg"));
        File selected = chooser.showOpenDialog(Navigator.stage());
        if (selected != null) {
            imagePathField.setText(selected.getAbsolutePath());
            previewImage.setImage(new Image(selected.toURI().toString(), true));
            previewPlaceholder.setVisible(false);
        }
    }

    @FXML
    private void onPublishItem() {
        try {
            if (!Dialogs.confirm("Confirmar publicación", "¿Deseas publicar este objeto encontrado?")) return;
            var item = AppContext.items().publish(titleField.getText(), descriptionArea.getText(), categoryCombo.getValue(),
                    locationField.getText(), datePicker.getValue(), imagePathField.getText(), questionField.getText(), UserSession.get().id());
            Dialogs.info("Objeto publicado", "“" + item.title() + "” ya aparece en el catálogo.");
            UserSession.selectItem(item);
            Navigator.goTo("detalle-objeto.fxml");
        } catch (IllegalArgumentException exception) {
            messageLabel.setText(exception.getMessage());
            messageLabel.getStyleClass().setAll("form-message", "message-error");
        } catch (RuntimeException exception) {
            messageLabel.setText("No fue posible publicar el objeto. Inténtalo nuevamente.");
        }
    }
}
