package pe.edu.objetoshallados.controller;

import com.google.zxing.common.BitMatrix;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import pe.edu.objetoshallados.config.AppContext;
import pe.edu.objetoshallados.model.LostItem;
import pe.edu.objetoshallados.session.UserSession;
import pe.edu.objetoshallados.util.Navigator;

public final class QrController extends BaseController {
    @FXML private ImageView qrImage;
    @FXML private Label identifierLabel;
    @FXML private Label objectLabel;

    @FXML
    private void initialize() {
        LostItem item = UserSession.selectedItem();
        if (item == null || item.id() <= 0) {
            identifierLabel.setText("Selecciona un objeto del catálogo.");
            return;
        }
        identifierLabel.setText(AppContext.qr().identifier(item.id()));
        objectLabel.setText(item.title());
        BitMatrix matrix = AppContext.qr().generate(item.id());
        WritableImage image = new WritableImage(matrix.getWidth(), matrix.getHeight());
        PixelWriter writer = image.getPixelWriter();
        for (int y = 0; y < matrix.getHeight(); y++) {
            for (int x = 0; x < matrix.getWidth(); x++) {
                writer.setColor(x, y, matrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        qrImage.setImage(image);
    }

    @Override
    public void onBack() {
        Navigator.goTo("detalle-objeto.fxml");
    }
}
