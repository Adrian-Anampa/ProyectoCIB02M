package pe.edu.objetoshallados.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import pe.edu.objetoshallados.config.AppContext;
import pe.edu.objetoshallados.model.LostItem;
import pe.edu.objetoshallados.session.UserSession;
import pe.edu.objetoshallados.util.Dialogs;
import pe.edu.objetoshallados.util.Navigator;

public final class CustodyController extends BaseController {
    @FXML private Label objectLabel;
    @FXML private Label questionLabel;
    @FXML private TextArea answerArea;
    @FXML private Label messageLabel;

    private LostItem item;

    @FXML
    private void initialize() {
        item = UserSession.selectedItem();
        if (item != null) {
            objectLabel.setText(item.title());
            questionLabel.setText(item.verificationQuestion());
        }
    }

    @FXML
    private void onSubmitClaim() {
        if (item == null) {
            messageLabel.setText("No se encontró el objeto seleccionado.");
            return;
        }
        try {
            if (!Dialogs.confirm("Enviar reclamo", "La respuesta será revisada por un administrador. ¿Deseas continuar?")) return;
            var claim = AppContext.claims().create(item.id(), UserSession.get().id(), answerArea.getText());
            UserSession.selectClaim(claim.id());
            Dialogs.info("Reclamo enviado", "Tu reclamo quedó registrado como pendiente.");
            Navigator.goTo("detalle-reclamo.fxml");
        } catch (IllegalArgumentException exception) {
            messageLabel.setText(exception.getMessage());
        } catch (RuntimeException exception) {
            messageLabel.setText("No fue posible enviar el reclamo.");
        }
    }

    @Override
    public void onBack() {
        Navigator.goTo("detalle-objeto.fxml");
    }
}
