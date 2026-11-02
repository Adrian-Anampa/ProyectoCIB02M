package pe.edu.objetoshallados.util;

import javafx.scene.Parent;
import javafx.stage.Stage;
import pe.edu.objetoshallados.App;

import java.io.IOException;

public final class Navigator {
    private static Stage stage;

    private Navigator() {}

    public static void initialize(Stage primaryStage) {
        stage = primaryStage;
    }

    public static void goTo(String fxml) {
        if (stage == null || stage.getScene() == null) {
            throw new IllegalStateException("Navigator todavía no fue inicializado");
        }
        try {
            Parent root = App.loadView(fxml);
            stage.getScene().setRoot(root);
            stage.setTitle(App.APP_TITLE);
        } catch (IOException exception) {
            Dialogs.error("No se pudo abrir la pantalla", "Inténtalo nuevamente.");
            throw new IllegalStateException("Error al cargar " + fxml, exception);
        }
    }

    public static Stage stage() {
        return stage;
    }
}
