package pe.edu.objetoshallados;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pe.edu.objetoshallados.util.Navigator;

import java.io.IOException;
import java.util.Objects;

public final class App extends Application {
    public static final String APP_TITLE = "Encuentra+ | Objetos perdidos";

    @Override
    public void start(Stage stage) throws IOException {
        Navigator.initialize(stage);
        Parent root = loadView("login.fxml");
        Scene scene = new Scene(root, 1180, 760);
        scene.getStylesheets().add(Objects.requireNonNull(
                App.class.getResource("/css/app.css")).toExternalForm());
        stage.setTitle(APP_TITLE);
        stage.setMinWidth(920);
        stage.setMinHeight(640);
        stage.setScene(scene);
        stage.show();
    }

    public static Parent loadView(String fxml) throws IOException {
        return FXMLLoader.load(Objects.requireNonNull(
                App.class.getResource("/fxml/" + fxml), "No existe /fxml/" + fxml));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
