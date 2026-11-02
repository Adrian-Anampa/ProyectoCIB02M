package pe.edu.objetoshallados.repository;

import pe.edu.objetoshallados.model.*;

import java.util.List;
import java.util.Optional;

public interface AppRepository {
    Optional<User> authenticate(String email, String password);

    User register(String fullName, String email, String studentCode, String password);

    List<LostItem> findItems();

    LostItem saveItem(LostItem item, long userId);

    boolean isItemPublisher(long itemId, long userId);

    LostItem updateItem(LostItem item, long userId);

    List<Claim> findClaims(long userId, boolean adminView);

    Claim saveClaim(long itemId, long userId, String answer);

    void updateClaim(long claimId, ClaimStatus status, String adminResponse, long adminId);

    List<Notice> findNotices(long userId);

    void markNoticeRead(long noticeId, long userId);

    List<AuditEntry> findAuditEntries();

    DashboardStats dashboardStats();
}
