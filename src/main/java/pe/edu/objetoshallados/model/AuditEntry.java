package pe.edu.objetoshallados.model;

import java.time.LocalDateTime;

public record AuditEntry(long id, String user, String action, LocalDateTime createdAt, String relatedElement) {}
