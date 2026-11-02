package pe.edu.objetoshallados.service;

import pe.edu.objetoshallados.model.Claim;
import pe.edu.objetoshallados.model.Notice;
import pe.edu.objetoshallados.repository.AppRepository;

import java.util.List;
import java.util.Optional;

public final class ClaimService {
    private final AppRepository repository;

    public ClaimService(AppRepository repository) {
        this.repository = repository;
    }

    public List<Claim> forUser(long userId) {
        return repository.findClaims(userId, false);
    }

    public List<Claim> all() {
        return repository.findClaims(0, true);
    }

    public Optional<Claim> find(long claimId, long userId, boolean admin) {
        return repository.findClaims(userId, admin).stream().filter(claim -> claim.id() == claimId).findFirst();
    }

    public Claim create(long itemId, long userId, String answer) {
        if (answer == null || answer.trim().length() < 4) {
            throw new IllegalArgumentException("Explica con suficiente detalle cómo identificas el objeto.");
        }
        return repository.saveClaim(itemId, userId, answer.trim());
    }

    public List<Notice> notices(long userId) {
        return repository.findNotices(userId);
    }
}
