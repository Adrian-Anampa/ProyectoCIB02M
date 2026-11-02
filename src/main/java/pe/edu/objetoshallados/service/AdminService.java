package pe.edu.objetoshallados.service;

import pe.edu.objetoshallados.model.AuditEntry;
import pe.edu.objetoshallados.model.ClaimStatus;
import pe.edu.objetoshallados.model.DashboardStats;
import pe.edu.objetoshallados.repository.AppRepository;

import java.util.List;

public final class AdminService {
    private final AppRepository repository;

    public AdminService(AppRepository repository) {
        this.repository = repository;
    }

    public void resolveClaim(long claimId, ClaimStatus status, String response, long adminId) {
        if (status == ClaimStatus.PENDIENTE) {
            throw new IllegalArgumentException("Selecciona aprobar o rechazar.");
        }
        if (response == null || response.isBlank()) {
            throw new IllegalArgumentException("Escribe una respuesta para el estudiante.");
        }
        repository.updateClaim(claimId, status, response.trim(), adminId);
    }

    public DashboardStats stats() {
        return repository.dashboardStats();
    }

    public List<AuditEntry> audit() {
        return repository.findAuditEntries();
    }
}
