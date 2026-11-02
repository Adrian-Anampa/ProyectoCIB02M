package pe.edu.objetoshallados.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import pe.edu.objetoshallados.model.LostItem;
import pe.edu.objetoshallados.session.UserSession;
import pe.edu.objetoshallados.util.Navigator;

import java.util.Random;

public final class QrController extends BaseController {
    @FXML private GridPane qrGrid;
    @FXML private Label identifierLabel;
    @FXML private Label objectLabel;

    @FXML
    private void initialize() {
        LostItem item = UserSession.selectedItem();
        long id = item == null ? 0 : item.id();
        identifierLabel.setText("OBJ-" + String.format("%05d", id));
        objectLabel.setText(item == null ? "Objeto" : item.title());
        drawPreview(id);
    }

    private void drawPreview(long seed) {
        Random random = new Random(seed * 31 + 17);
        int size = 21;
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                Region pixel = new Region();
                pixel.setMinSize(8, 8);
                pixel.setPrefSize(8, 8);
                boolean finder = finderPixel(row, column, 0, 0)
                        || finderPixel(row, column, 0, size - 7)
                        || finderPixel(row, column, size - 7, 0);
                boolean dark = finder || random.nextBoolean();
                pixel.getStyleClass().add(dark ? "qr-dark" : "qr-light");
                qrGrid.add(pixel, column, row);
            }
        }
    }

    private static boolean finderPixel(int row, int column, int startRow, int startColumn) {
        if (row < startRow || row >= startRow + 7 || column < startColumn || column >= startColumn + 7) return false;
        int localRow = row - startRow;
        int localColumn = column - startColumn;
        return localRow == 0 || localRow == 6 || localColumn == 0 || localColumn == 6
                || (localRow >= 2 && localRow <= 4 && localColumn >= 2 && localColumn <= 4);
    }

    @Override
    public void onBack() {
        Navigator.goTo("detalle-objeto.fxml");
    }
}
