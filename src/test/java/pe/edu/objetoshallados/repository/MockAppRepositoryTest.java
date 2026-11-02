package pe.edu.objetoshallados.repository;

import org.junit.jupiter.api.Test;
import pe.edu.objetoshallados.model.ClaimStatus;

import static org.junit.jupiter.api.Assertions.*;

class MockAppRepositoryTest {
    @Test
    void authenticatesBothDemoRoles() {
        MockAppRepository repository = new MockAppRepository();
        assertTrue(repository.authenticate("estudiante@demo.pe", "Demo123*").isPresent());
        assertTrue(repository.authenticate("admin@demo.pe", "Admin123*").isPresent());
        assertTrue(repository.authenticate("admin@demo.pe", "incorrecta").isEmpty());
    }

    @Test
    void createsAndResolvesClaim() {
        MockAppRepository repository = new MockAppRepository();
        var claim = repository.saveClaim(1, 1, "El cuaderno es de Cálculo II.");
        assertEquals(ClaimStatus.PENDIENTE, claim.status());
        repository.updateClaim(claim.id(), ClaimStatus.APROBADO, "Identidad confirmada", 2);
        var updated = repository.findClaims(0, true).stream().filter(value -> value.id() == claim.id()).findFirst().orElseThrow();
        assertEquals(ClaimStatus.APROBADO, updated.status());
    }
}
