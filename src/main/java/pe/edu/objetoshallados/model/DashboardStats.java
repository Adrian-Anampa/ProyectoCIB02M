package pe.edu.objetoshallados.model;

import java.util.Map;

public record DashboardStats(long available, long pendingClaims, long returned, Map<String, Number> byCategory) {
    public long published() {
        return byCategory.values().stream().mapToLong(Number::longValue).sum();
    }

    public long inClaim() {
        return Math.max(0, published() - available - returned);
    }
}
