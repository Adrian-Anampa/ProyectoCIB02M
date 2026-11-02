package pe.edu.objetoshallados.model;

public enum ItemStatus {
    DISPONIBLE("Disponible"),
    EN_RECLAMO("En reclamo"),
    DEVUELTO("Devuelto");

    private final String label;

    ItemStatus(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
