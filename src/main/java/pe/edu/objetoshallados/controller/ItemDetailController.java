package pe.edu.objetoshallados.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import pe.edu.objetoshallados.config.AppContext;
import pe.edu.objetoshallados.model.ItemStatus;
import pe.edu.objetoshallados.model.LostItem;
import pe.edu.objetoshallados.session.UserSession;
import pe.edu.objetoshallados.util.Navigator;

import java.io.File;
import java.time.format.DateTimeFormatter;

public final class ItemDetailController extends BaseController {
    @FXML private Label titleLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label categoryLabel;
    @FXML private Label locationLabel;
    @FXML private Label dateLabel;
    @FXML private Label statusLabel;
    @FXML private Label itemIdLabel;
    @FXML private ImageView itemImage;
    @FXML private Label imagePlaceholder;
    @FXML private Button claimButton;
    @FXML private Button editButton;

    private LostItem item;

    @FXML
    private void initialize() {
        item = UserSession.selectedItem();
        if (item == null) {
            item = AppContext.items().all().stream().findFirst().orElse(null);
            UserSession.selectItem(item);
        }
        if (item == null) return;
        titleLabel.setText(item.title());
        descriptionLabel.setText(item.description());
        categoryLabel.setText(item.category());
        locationLabel.setText(item.location());
        dateLabel.setText(item.foundDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        statusLabel.setText(item.status().label());
        statusLabel.getStyleClass().add("status-" + item.status().name().toLowerCase().replace('_', '-'));
        itemIdLabel.setText("OBJ-" + String.format("%05d", item.id()));
        claimButton.setDisable(item.status() != ItemStatus.DISPONIBLE);
        claimButton.setText(item.status() == ItemStatus.DISPONIBLE ? "Este objeto es mío" : "No disponible para reclamo");
        boolean editable = AppContext.items().canEdit(item, UserSession.get().id());
        editButton.setVisible(editable);
        editButton.setManaged(editable);
        loadImage(item.imagePath());
    }

    @FXML
    private void onClaimItem() {
        Navigator.goTo("custodia.fxml");
    }

    @FXML
    private void onQr() {
        Navigator.goTo("qr.fxml");
    }

    @FXML
    private void onEdit() {
        Navigator.goTo("editar-objeto.fxml");
    }

    @Override
    public void onBack() {
        Navigator.goTo("catalogo.fxml");
    }

    private void loadImage(String path) {
        if (path == null || path.isBlank()) return;
        File file = new File(path);
        if (file.isFile()) {
            itemImage.setImage(new Image(file.toURI().toString(), true));
            imagePlaceholder.setVisible(false);
        }
    }
}
