package com.ruedalibre.model;

import com.ruedalibre.model.enums.RolUsuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(max = 150)
    @Column(name = "nombre_completo", nullable = false, length = 150)
    private String nombreCompleto;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(max = 50)
    @Column(name = "nombre_usuario", unique = true, nullable = false, length = 50)
    private String nombreUsuario;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene formato válido")
    @Size(max = 150)
    @Column(unique = true, nullable = false, length = 150)
    private String correo;

    @Column(nullable = false)
    private String contrasena;

    @NotNull(message = "El rol es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RolUsuario rol;

    @Column(nullable = false)
    private Boolean activo;

    @PrePersist
    public void prePersist() {
        if (activo == null) {
            activo = true;
        }
    }

    public Usuario() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public RolUsuario getRol() { return rol; }
    public void setRol(RolUsuario rol) { this.rol = rol; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
