package pe.edu.objetoshallados.service;

import pe.edu.objetoshallados.model.DashboardStats;
import pe.edu.objetoshallados.repository.AppRepository;

public final class IndicatorService {
    private final AppRepository repository;

    public IndicatorService(AppRepository repository) {
        this.repository = repository;
    }

    public DashboardStats snapshot() {
        return repository.dashboardStats();
    }
}
