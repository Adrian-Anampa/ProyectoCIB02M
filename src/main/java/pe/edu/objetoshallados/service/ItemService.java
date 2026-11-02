package pe.edu.objetoshallados.service;

import pe.edu.objetoshallados.model.ItemStatus;
import pe.edu.objetoshallados.model.LostItem;
import pe.edu.objetoshallados.repository.AppRepository;

import java.time.LocalDate;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public final class ItemService {
    private final AppRepository repository;
    private final ImageService images;

    public ItemService(AppRepository repository) {
        this(repository, new ImageService(Path.of("data", "images")));
    }

    public ItemService(AppRepository repository, ImageService images) {
        this.repository = repository;
        this.images = images;
    }

    public List<LostItem> search(String keyword, String category, String location, LocalDate date, ItemStatus status) {
        String term = normalize(keyword);
        String categoryFilter = normalize(category);
        String locationFilter = normalize(location);
        return repository.findItems().stream()
                .filter(item -> term.isBlank() || normalize(item.title()).contains(term) || normalize(item.description()).contains(term))
                .filter(item -> categoryFilter.isBlank() || categoryFilter.equals("todas") || normalize(item.category()).equals(categoryFilter))
                .filter(item -> locationFilter.isBlank() || normalize(item.location()).contains(locationFilter))
                .filter(item -> date == null || item.foundDate().equals(date))
                .filter(item -> status == null || item.status() == status)
                .sorted(Comparator.comparing(LostItem::foundDate).reversed())
                .toList();
    }

    public LostItem publish(String title, String description, String category, String location, LocalDate date,
                            String imagePath, String question, long userId) {
        require(title, "Ingresa el título del objeto.");
        require(description, "Ingresa una descripción.");
        require(category, "Selecciona una categoría.");
        require(location, "Ingresa el lugar del hallazgo.");
        require(question, "Ingresa una pregunta de verificación.");
        if (date == null || date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Selecciona una fecha de hallazgo válida.");
        }
        String storedImage = images.store(imagePath);
        LostItem item = new LostItem(0, title.trim(), description.trim(), category.trim(), location.trim(), date,
                ItemStatus.DISPONIBLE, storedImage, question.trim());
        try {
            return repository.saveItem(item, userId);
        } catch (RuntimeException exception) {
            images.discard(storedImage);
            throw exception;
        }
    }

    public List<LostItem> all() {
        return repository.findItems();
    }

    public LostItem find(long itemId) {
        return repository.findItems().stream().filter(item -> item.id() == itemId).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el objeto."));
    }

    public boolean canEdit(LostItem item, long userId) {
        return item != null && item.status() == ItemStatus.DISPONIBLE
                && repository.isItemPublisher(item.id(), userId);
    }

    public LostItem edit(long itemId, String title, String description, String category, String location,
                         LocalDate date, String newImagePath, String question, long userId) {
        LostItem current = find(itemId);
        if (!canEdit(current, userId)) throw new IllegalArgumentException("El objeto no está disponible o no te pertenece.");
        require(title, "Ingresa el título del objeto.");
        require(description, "Ingresa una descripción.");
        require(category, "Selecciona una categoría.");
        require(location, "Ingresa el lugar del hallazgo.");
        require(question, "Ingresa una pregunta de verificación.");
        if (date == null || date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Selecciona una fecha de hallazgo válida.");
        }
        String storedImage = newImagePath == null || newImagePath.isBlank() ? "" : images.store(newImagePath);
        LostItem changed = new LostItem(current.id(), title.trim(), description.trim(), category.trim(), location.trim(),
                date, current.status(), storedImage.isBlank() ? current.imagePath() : storedImage, question.trim());
        try {
            LostItem updated = repository.updateItem(changed, userId);
            if (!storedImage.isBlank()) images.discard(current.imagePath());
            return updated;
        } catch (RuntimeException exception) {
            images.discard(storedImage);
            throw exception;
        }
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private static void require(String value, String message) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(message);
    }
}
