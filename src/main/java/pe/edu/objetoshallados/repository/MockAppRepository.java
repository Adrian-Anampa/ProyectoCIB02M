package pe.edu.objetoshallados.repository;

import pe.edu.objetoshallados.model.*;
import pe.edu.uni.campusfind.model.Usuario;
import pe.edu.uni.campusfind.security.SessionManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public final class MockAppRepository implements AppRepository {
    private final List<UserAccount> users = new ArrayList<>();
    private final List<LostItem> items = new ArrayList<>();
    private final List<Claim> claims = new ArrayList<>();
    private final List<Notice> notices = new ArrayList<>();
    private final Map<Long, Long> noticeOwners = new HashMap<>();
    private final Map<Long, Long> claimOwners = new HashMap<>();
    private final Map<Long, Long> itemOwners = new HashMap<>();
    private final List<AuditEntry> audit = new ArrayList<>();
    private final AtomicLong userSequence = new AtomicLong(3);
    private final AtomicLong itemSequence = new AtomicLong(6);
    private final AtomicLong claimSequence = new AtomicLong(3);
    private final AtomicLong noticeSequence = new AtomicLong(3);

    public MockAppRepository() {
        users.add(new UserAccount(new User(1, "Ana Torres", "estudiante@demo.pe", "20240001", Role.ESTUDIANTE), "Demo123*"));
        users.add(new UserAccount(new User(2, "Carlos Administrador", "admin@demo.pe", "ADM001", Role.ADMINISTRADOR), "Admin123*"));

        items.add(new LostItem(1, "Mochila azul", "Mochila de lona con cuadernos de ingeniería.", "Mochilas", "Biblioteca central", LocalDate.now().minusDays(2), ItemStatus.DISPONIBLE, "", "¿Qué curso aparece en el cuaderno?"));
        items.add(new LostItem(2, "Audífonos inalámbricos", "Estuche negro con audífonos blancos.", "Tecnología", "Laboratorio 204", LocalDate.now().minusDays(4), ItemStatus.EN_RECLAMO, "", "¿Qué marca son los audífonos?"));
        items.add(new LostItem(3, "Botella térmica", "Botella metálica color verde con stickers.", "Accesorios", "Cafetería", LocalDate.now().minusDays(1), ItemStatus.DISPONIBLE, "", "Describe uno de los stickers."));
        items.add(new LostItem(4, "Calculadora científica", "Calculadora gris dentro de una funda.", "Útiles", "Aula B-305", LocalDate.now().minusDays(7), ItemStatus.DISPONIBLE, "", "¿Qué hay escrito en la funda?"));
        items.add(new LostItem(5, "Casaca negra", "Casaca ligera talla M.", "Ropa", "Patio principal", LocalDate.now().minusDays(10), ItemStatus.DEVUELTO, "", "¿Qué objeto había en un bolsillo?"));
        for (LostItem item : items) itemOwners.put(item.id(), 2L);

        claims.add(new Claim(1, 2, "Audífonos inalámbricos", "Ana Torres", LocalDateTime.now().minusDays(1), ClaimStatus.PENDIENTE, "Son marca X y el estuche tiene una marca pequeña.", "En evaluación"));
        claims.add(new Claim(2, 5, "Casaca negra", "Ana Torres", LocalDateTime.now().minusDays(8), ClaimStatus.APROBADO, "Había una tarjeta del bus.", "Identidad verificada. Acércate a recepción."));

        notices.add(new Notice(1, "Tu reclamo por Audífonos inalámbricos está siendo evaluado.", LocalDateTime.now().minusHours(5), "Audífonos inalámbricos", false));
        notices.add(new Notice(2, "Se publicó un objeto que coincide con tu búsqueda: Botella térmica.", LocalDateTime.now().minusDays(1), "Botella térmica", true));
        noticeOwners.put(1L, 1L);
        noticeOwners.put(2L, 1L);
        claimOwners.put(1L, 1L);
        claimOwners.put(2L, 1L);

        audit.add(new AuditEntry(1, "Carlos Administrador", "INICIO_SESION", LocalDateTime.now().minusMinutes(30), "Sesión administrativa"));
        audit.add(new AuditEntry(2, "Ana Torres", "CREAR_RECLAMO", LocalDateTime.now().minusDays(1), "Audífonos inalámbricos"));
    }

    @Override
    public Optional<User> authenticate(String email, String password) {
        return users.stream()
                .filter(account -> account.user().email().equalsIgnoreCase(email.trim()) && account.password().equals(password))
                .map(UserAccount::user)
                .findFirst();
    }

    @Override
    public synchronized User register(String fullName, String email, String studentCode, String password) {
        boolean exists = users.stream().anyMatch(account -> account.user().email().equalsIgnoreCase(email));
        if (exists) {
            throw new IllegalArgumentException("El correo ya se encuentra registrado.");
        }
        User user = new User(userSequence.getAndIncrement(), fullName, email.toLowerCase(Locale.ROOT), studentCode, Role.ESTUDIANTE);
        users.add(new UserAccount(user, password));
        return user;
    }

    @Override
    public List<LostItem> findItems() {
        return List.copyOf(items);
    }

    @Override
    public synchronized LostItem saveItem(LostItem item, long userId) {
        LostItem saved = new LostItem(itemSequence.getAndIncrement(), item.title(), item.description(), item.category(),
                item.location(), item.foundDate(), ItemStatus.DISPONIBLE, item.imagePath(), item.verificationQuestion());
        items.add(saved);
        itemOwners.put(saved.id(), userId);
        audit.add(new AuditEntry(audit.size() + 1L, userName(userId), "PUBLICAR_OBJETO", LocalDateTime.now(), saved.title()));
        return saved;
    }

    @Override
    public boolean isItemPublisher(long itemId, long userId) {
        return userId == itemOwners.getOrDefault(itemId, -1L);
    }

    @Override
    public synchronized LostItem updateItem(LostItem item, long userId) {
        if (!isItemPublisher(item.id(), userId)) throw new IllegalArgumentException("Solo puedes editar tus publicaciones.");
        for (int index = 0; index < items.size(); index++) {
            LostItem current = items.get(index);
            if (current.id() == item.id()) {
                if (current.status() != ItemStatus.DISPONIBLE) {
                    throw new IllegalArgumentException("No se puede editar un objeto con reclamos o ya devuelto.");
                }
                items.set(index, item);
                audit.add(new AuditEntry(audit.size() + 1L, userName(userId), "EDITAR_OBJETO", LocalDateTime.now(), item.title()));
                return item;
            }
        }
        throw new IllegalArgumentException("No se encontró el objeto.");
    }

    @Override
    public List<Claim> findClaims(long userId, boolean adminView) {
        if (adminView) {
            return List.copyOf(claims);
        }
        String name = userName(userId);
        return claims.stream().filter(claim -> claim.claimantName().equals(name)).toList();
    }

    @Override
    public synchronized Claim saveClaim(long itemId, long userId, String answer) {
        LostItem item = items.stream().filter(value -> value.id() == itemId).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("El objeto ya no está disponible."));
        Claim claim = new Claim(claimSequence.getAndIncrement(), itemId, item.title(), userName(userId),
                LocalDateTime.now(), ClaimStatus.PENDIENTE, answer, "Pendiente de revisión");
        claims.add(claim);
        claimOwners.put(claim.id(), userId);
        replaceItem(item, ItemStatus.EN_RECLAMO);
        audit.add(new AuditEntry(audit.size() + 1L, userName(userId), "CREAR_RECLAMO", LocalDateTime.now(), item.title()));
        return claim;
    }

    @Override
    public synchronized void updateClaim(long claimId, ClaimStatus status, String adminResponse, long adminId) {
        for (int index = 0; index < claims.size(); index++) {
            Claim current = claims.get(index);
            if (current.id() == claimId) {
                claims.set(index, new Claim(current.id(), current.itemId(), current.itemTitle(), current.claimantName(),
                        current.createdAt(), status, current.answer(), adminResponse));
                items.stream().filter(item -> item.id() == current.itemId()).findFirst()
                        .ifPresent(item -> replaceItem(item, status == ClaimStatus.APROBADO ? ItemStatus.DEVUELTO : ItemStatus.DISPONIBLE));
                audit.add(new AuditEntry(audit.size() + 1L, userName(adminId), "RESOLVER_RECLAMO", LocalDateTime.now(), current.itemTitle()));
                long noticeId = noticeSequence.getAndIncrement();
                notices.add(new Notice(noticeId, "Tu reclamo por " + current.itemTitle() + " fue "
                        + (status == ClaimStatus.APROBADO ? "aprobado" : "rechazado") + ". " + adminResponse,
                        LocalDateTime.now(), current.itemTitle(), false));
                noticeOwners.put(noticeId, claimOwners.get(current.id()));
                return;
            }
        }
        throw new IllegalArgumentException("No se encontró el reclamo.");
    }

    @Override
    public List<Notice> findNotices(long userId) {
        return notices.stream().filter(notice -> userId == noticeOwners.getOrDefault(notice.id(), -1L)).toList();
    }

    @Override
    public synchronized void markNoticeRead(long noticeId, long userId) {
        if (userId != noticeOwners.getOrDefault(noticeId, -1L)) {
            throw new IllegalArgumentException("No se encontró el aviso.");
        }
        for (int index = 0; index < notices.size(); index++) {
            Notice notice = notices.get(index);
            if (notice.id() == noticeId) {
                notices.set(index, new Notice(notice.id(), notice.message(), notice.createdAt(), notice.itemTitle(), true));
                return;
            }
        }
    }

    @Override
    public List<AuditEntry> findAuditEntries() {
        return List.copyOf(audit);
    }

    @Override
    public DashboardStats dashboardStats() {
        long available = items.stream().filter(item -> item.status() == ItemStatus.DISPONIBLE).count();
        long pending = claims.stream().filter(claim -> claim.status() == ClaimStatus.PENDIENTE).count();
        long returned = items.stream().filter(item -> item.status() == ItemStatus.DEVUELTO).count();
        LinkedHashMap<String, Number> categories = new LinkedHashMap<>();
        items.forEach(item -> categories.merge(item.category(), 1, (left, right) -> left.intValue() + right.intValue()));
        return new DashboardStats(available, pending, returned, categories);
    }

    private void replaceItem(LostItem item, ItemStatus status) {
        int index = items.indexOf(item);
        if (index >= 0) {
            items.set(index, new LostItem(item.id(), item.title(), item.description(), item.category(), item.location(),
                    item.foundDate(), status, item.imagePath(), item.verificationQuestion()));
        }
    }

    private String userName(long userId) {
        Usuario currentUser = SessionManager.getInstance().getUsuarioActual();
        if (currentUser != null && currentUser.getIdUsuario() == userId) {
            return (currentUser.getNombre() + " " + currentUser.getApellido()).trim();
        }
        return users.stream().map(UserAccount::user).filter(user -> user.id() == userId)
                .map(User::fullName).findFirst().orElse("Usuario");
    }

    private record UserAccount(User user, String password) {}
}
