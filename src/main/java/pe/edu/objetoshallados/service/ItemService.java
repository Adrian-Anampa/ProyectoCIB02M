package pe.edu.objetoshallados.service;

import pe.edu.objetoshallados.model.ItemStatus;
import pe.edu.objetoshallados.model.LostItem;
import pe.edu.objetoshallados.repository.AppRepository;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public final class ItemService {
    private final AppRepository repository;

    public ItemService(AppRepository repository) {
        this.repository = repository;
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
        LostItem item = new LostItem(0, title.trim(), description.trim(), category.trim(), location.trim(), date,
                ItemStatus.DISPONIBLE, imagePath == null ? "" : imagePath, question.trim());
        return repository.saveItem(item, userId);
    }

    public List<LostItem> all() {
        return repository.findItems();
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private static void require(String value, String message) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(message);
    }
}
