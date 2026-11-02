package pe.edu.objetoshallados.service;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pe.edu.objetoshallados.model.ClaimStatus;
import pe.edu.objetoshallados.repository.MockAppRepository;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class HanFeaturesTest {
    @TempDir Path temp;

    @Test
    void publishedPhotoIsStoredAndInvalidFileIsRejected() throws Exception {
        ImageService images = new ImageService(temp.resolve("images"));
        MockAppRepository repository = new MockAppRepository();
        ItemService items = new ItemService(repository, images);
        Path source = temp.resolve("source.png");
        BufferedImage sample = new BufferedImage(4, 4, BufferedImage.TYPE_INT_RGB);
        ImageIO.write(sample, "png", source.toFile());

        var published = items.publish("Llaves", "Juego de llaves", "Accesorios", "Biblioteca",
                LocalDate.now(), source.toString(), "¿Cuántas llaves hay?", 7);
        assertNotEquals(source.toString(), published.imagePath());
        assertTrue(Files.isRegularFile(Path.of(published.imagePath())));
        assertEquals(published.imagePath(), items.find(published.id()).imagePath());

        Path fake = temp.resolve("fake.png");
        Files.writeString(fake, "no es una imagen");
        assertThrows(IllegalArgumentException.class, () -> images.store(fake.toString()));
    }

    @Test
    void publisherCanEditAvailableItemOnly() {
        MockAppRepository repository = new MockAppRepository();
        ItemService items = new ItemService(repository, new ImageService(temp.resolve("images")));
        var published = items.publish("Llaves", "Juego de llaves", "Accesorios", "Biblioteca",
                LocalDate.now(), "", "¿Cuántas llaves hay?", 7);
        assertFalse(items.canEdit(published, 8));
        assertThrows(IllegalArgumentException.class, () -> items.edit(published.id(), "Llaves nuevas", "Descripción",
                "Accesorios", "Biblioteca", LocalDate.now(), "", "¿Cuántas?", 8));
        var edited = items.edit(published.id(), "Llaves nuevas", "Descripción", "Accesorios",
                "Biblioteca", LocalDate.now(), "", "¿Cuántas?", 7);
        assertEquals("Llaves nuevas", edited.title());
        repository.saveClaim(edited.id(), 1, "Hay tres llaves.");
        assertThrows(IllegalArgumentException.class, () -> items.edit(edited.id(), "Otro", "Descripción",
                "Accesorios", "Biblioteca", LocalDate.now(), "", "¿Cuántas?", 7));
    }

    @Test
    void qrEncodesObjectIdentifier() throws Exception {
        QrService service = new QrService();
        BitMatrix matrix = service.generate(154);
        LuminanceSource source = new LuminanceSource(matrix.getWidth(), matrix.getHeight()) {
            @Override public byte[] getRow(int y, byte[] row) {
                byte[] pixels = row == null || row.length < getWidth() ? new byte[getWidth()] : row;
                for (int x = 0; x < getWidth(); x++) pixels[x] = (byte) (matrix.get(x, y) ? 0 : 255);
                return pixels;
            }
            @Override public byte[] getMatrix() {
                byte[] pixels = new byte[getWidth() * getHeight()];
                for (int y = 0; y < getHeight(); y++) {
                    for (int x = 0; x < getWidth(); x++) pixels[y * getWidth() + x] = (byte) (matrix.get(x, y) ? 0 : 255);
                }
                return pixels;
            }
        };
        assertEquals("OBJ-00154", new MultiFormatReader().decode(new BinaryBitmap(new HybridBinarizer(source))).getText());
    }

    @Test
    void claimResolutionCreatesUserNoticeThatCanBeRead() {
        MockAppRepository repository = new MockAppRepository();
        NoticeService notices = new NoticeService(repository);
        var claim = repository.saveClaim(1, 1, "El cuaderno es mío.");
        repository.updateClaim(claim.id(), ClaimStatus.APROBADO, "Acércate a recepción.", 2);
        var notice = notices.forUser(1).stream().filter(value -> value.message().contains("aprobado")).findFirst().orElseThrow();
        assertFalse(notice.read());
        assertTrue(notices.forUser(2).isEmpty());
        assertThrows(IllegalArgumentException.class, () -> notices.markRead(notice.id(), 2));
        notices.markRead(notice.id(), 1);
        assertTrue(notices.forUser(1).stream().filter(value -> value.id() == notice.id()).findFirst().orElseThrow().read());
    }
}
