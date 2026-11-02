package pe.edu.objetoshallados.service;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.UUID;

/** Stores validated photographs under the application's data directory. */
public final class ImageService {
    public static final long MAX_BYTES = 5L * 1024 * 1024;
    private final Path directory;

    public ImageService(Path directory) {
        this.directory = directory.toAbsolutePath().normalize();
    }

    public String store(String sourcePath) {
        if (sourcePath == null || sourcePath.isBlank()) return "";
        Path source = Path.of(sourcePath).toAbsolutePath().normalize();
        String name = source.getFileName().toString().toLowerCase(Locale.ROOT);
        String extension = name.endsWith(".png") ? ".png" :
                (name.endsWith(".jpg") || name.endsWith(".jpeg")) ? ".jpg" : "";
        if (extension.isEmpty()) throw new IllegalArgumentException("Selecciona una imagen PNG o JPG.");
        try {
            if (!Files.isRegularFile(source) || Files.size(source) == 0 || Files.size(source) > MAX_BYTES) {
                throw new IllegalArgumentException("La imagen debe existir y ocupar como máximo 5 MB.");
            }
            try (ImageInputStream input = ImageIO.createImageInputStream(source.toFile())) {
                if (input == null) throw new IllegalArgumentException("El archivo no contiene una imagen válida.");
                var readers = ImageIO.getImageReaders(input);
                if (!readers.hasNext()) throw new IllegalArgumentException("El archivo no contiene una imagen válida.");
                ImageReader reader = readers.next();
                try {
                    reader.setInput(input);
                    String format = reader.getFormatName().toLowerCase(Locale.ROOT);
                    if ((!extension.equals(".png") || !format.equals("png"))
                            && (!extension.equals(".jpg") || !format.equals("jpeg"))) {
                        throw new IllegalArgumentException("La extensión no coincide con el formato de la imagen.");
                    }
                    reader.read(0);
                } finally {
                    reader.dispose();
                }
            }
            Files.createDirectories(directory);
            Path destination = directory.resolve(UUID.randomUUID() + extension);
            Files.copy(source, destination);
            Path workingDirectory = Path.of("").toAbsolutePath().normalize();
            return destination.startsWith(workingDirectory) ? workingDirectory.relativize(destination).toString() : destination.toString();
        } catch (IOException exception) {
            throw new IllegalArgumentException("No se pudo guardar la imagen.", exception);
        }
    }

    public void discard(String storedPath) {
        if (storedPath == null || storedPath.isBlank()) return;
        Path stored = Path.of(storedPath).toAbsolutePath().normalize();
        if (stored.getParent().equals(directory)) {
            try { Files.deleteIfExists(stored); } catch (IOException ignored) { }
        }
    }
}
