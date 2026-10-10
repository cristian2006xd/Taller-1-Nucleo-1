package com.ruedalibre.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La cédula es obligatoria")
    @Pattern(regexp = "\\d{10}", message = "La cédula debe tener 10 dígitos")
    @Column(unique = true, nullable = false, length = 10)
    private String cedula;

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String apellidos;

    @Size(max = 20)
    @Column(length = 20)
    private String telefono;

    @Email(message = "El correo no tiene formato válido")
    @Size(max = 150)
    @Column(length = 150)
    private String correo;

    @Size(max = 255)
    private String direccion;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @PrePersist
    public void prePersist() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
    }

    public Cliente() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }
}
