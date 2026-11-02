package pe.edu.objetoshallados.model;

public enum Role {
    ESTUDIANTE("Estudiante"),
    ADMINISTRADOR("Administrador");

    private final String label;

    Role(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
