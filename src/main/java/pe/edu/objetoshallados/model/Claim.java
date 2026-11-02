package pe.edu.objetoshallados.model;

import java.time.LocalDateTime;

public record Claim(
        long id,
        long itemId,
        String itemTitle,
        String claimantName,
        LocalDateTime createdAt,
        ClaimStatus status,
        String answer,
        String adminResponse
) {}
