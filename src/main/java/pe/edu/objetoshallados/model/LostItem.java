package pe.edu.objetoshallados.model;

import java.time.LocalDate;

public record LostItem(
        long id,
        String title,
        String description,
        String category,
        String location,
        LocalDate foundDate,
        ItemStatus status,
        String imagePath,
        String verificationQuestion
) {}
