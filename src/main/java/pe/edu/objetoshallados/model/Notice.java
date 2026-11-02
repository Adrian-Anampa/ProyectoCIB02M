package pe.edu.objetoshallados.model;

import java.time.LocalDateTime;

public record Notice(long id, String message, LocalDateTime createdAt, String itemTitle, boolean read) {}
