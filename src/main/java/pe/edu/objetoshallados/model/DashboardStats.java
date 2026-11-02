package pe.edu.objetoshallados.model;

import java.util.Map;

public record DashboardStats(long available, long pendingClaims, long returned, Map<String, Number> byCategory) {}
