package pe.edu.objetoshallados;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class FxmlLoadTest {
    private static final List<String> VIEWS = List.of(
            "login.fxml", "registro.fxml", "menu-principal.fxml", "catalogo.fxml", "publicar-objeto.fxml",
            "detalle-objeto.fxml", "mis-reclamos.fxml", "detalle-reclamo.fxml", "custodia.fxml", "avisos.fxml",
            "qr.fxml", "admin-panel.fxml", "reclamos-admin.fxml", "indicadores.fxml", "bitacora.fxml"
    );

    @BeforeAll
    static void startJavaFx() throws InterruptedException {
        CountDownLatch ready = new CountDownLatch(1);
        Platform.startup(ready::countDown);
        assertTrue(ready.await(10, TimeUnit.SECONDS), "JavaFX no inició a tiempo");
    }

    @AfterAll
    static void stopJavaFx() {
        Platform.exit();
    }

    @Test
    void everyViewLoadsWithItsController() throws InterruptedException {
        for (String view : VIEWS) {
            CountDownLatch loaded = new CountDownLatch(1);
            AtomicReference<Throwable> failure = new AtomicReference<>();
            Platform.runLater(() -> {
                try {
                    FXMLLoader.load(Objects.requireNonNull(App.class.getResource("/fxml/" + view)));
                } catch (Throwable throwable) {
                    failure.set(throwable);
                } finally {
                    loaded.countDown();
                }
            });
            assertTrue(loaded.await(10, TimeUnit.SECONDS), "Tiempo agotado al cargar " + view);
            assertNull(failure.get(), () -> "Falló FXMLLoader para " + view + ": " + failure.get());
        }
    }
}
