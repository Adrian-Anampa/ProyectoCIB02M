package pe.edu.objetoshallados;

import org.junit.jupiter.api.Test;

import javax.xml.parsers.DocumentBuilderFactory;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FxmlStructureTest {
    private static final List<String> VIEWS = List.of(
            "login.fxml", "registro.fxml", "menu-principal.fxml", "catalogo.fxml", "publicar-objeto.fxml",
            "detalle-objeto.fxml", "mis-reclamos.fxml", "detalle-reclamo.fxml", "custodia.fxml", "avisos.fxml",
            "qr.fxml", "admin-panel.fxml", "reclamos-admin.fxml", "indicadores.fxml", "bitacora.fxml"
    );

    @Test
    void everyRequiredViewIsValidXmlAndDeclaresAController() throws Exception {
        var factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        for (String view : VIEWS) {
            var resource = App.class.getResource("/fxml/" + view);
            assertNotNull(resource, "Falta " + view);
            var document = factory.newDocumentBuilder().parse(resource.openStream());
            String controller = document.getDocumentElement().getAttributeNS("http://javafx.com/fxml/1", "controller");
            assertFalse(controller.isBlank(), "Falta fx:controller en " + view);
            assertDoesNotThrow(() -> Class.forName(controller), "No existe el controlador de " + view);
        }
    }
}
