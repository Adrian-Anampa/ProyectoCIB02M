package pe.edu.uni.campusfind.model;

import java.time.LocalDateTime;

public class Usuario {
    private int idUsuario;
    private String nombre;
    private String apellido;
    private String correo;
    private String passwordHash;
    private Rol rol;
    private int intentosFallidos;
    private boolean bloqueado;
    private LocalDateTime fechaRegistro;

    // Constructor vacío
    public Usuario() {
    }

    // Constructor con datos principales
    public Usuario(String nombre, String apellido, String correo, String passwordHash, Rol rol) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.passwordHash = passwordHash;
        this.rol = rol;
        this.intentosFallidos = 0;
        this.bloqueado = false;
        this.fechaRegistro = LocalDateTime.now();
    }

    // Generar Getters y Setters
    // (En IntelliJ: Alt + Insert -> Getters and Setters -> Selecciona todos)

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
    public int getIntentosFallidos() { return intentosFallidos; }
    public void setIntentosFallidos(int intentosFallidos) { this.intentosFallidos = intentosFallidos; }
    public boolean isBloqueado() { return bloqueado; }
    public void setBloqueado(boolean bloqueado) { this.bloqueado = bloqueado; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}