package com.ruedalibre.model;

import com.ruedalibre.model.enums.EstadoProducto;
import com.ruedalibre.model.enums.TipoProducto;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El código es obligatorio")
    @Size(max = 50)
    @Column(unique = true, nullable = false, length = 50)
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String nombre;

    @NotBlank(message = "La marca es obligatoria")
    @Size(max = 100)
    @Column(length = 100)
    private String marca;

    @Size(max = 100)
    @Column(length = 100)
    private String modelo;

    @Size(max = 500)
    private String descripcion;

    @NotNull(message = "El precio de compra es obligatorio")
    @PositiveOrZero(message = "El precio de compra no puede ser negativo")
    @Column(name = "precio_compra", precision = 10, scale = 2)
    private BigDecimal precioCompra;

    @NotNull(message = "El precio de venta es obligatorio")
    @Positive(message = "El precio de venta debe ser mayor que cero")
    @Column(name = "precio", precision = 10, scale = 2)
    private BigDecimal precio;

    @NotNull(message = "La cantidad es obligatoria")
    @PositiveOrZero(message = "La cantidad no puede ser negativa")
    private Integer cantidad;

    @NotNull(message = "El stock mínimo es obligatorio")
    @PositiveOrZero(message = "El stock mínimo no puede ser negativo")
    @Column(name = "stock_minimo")
    private Integer stockMinimo;

    @NotNull(message = "El tipo es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoProducto tipo;

    private Integer cilindraje;

    private Integer anio;

    @Size(max = 50)
    @Column(length = 50)
    private String color;

    @Size(max = 17)
    @Column(unique = true, length = 17)
    private String vin;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoProducto estado;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @PrePersist
    public void prePersist() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
        if (estado == null) {
            estado = EstadoProducto.ACTIVO;
        }
    }

    public Producto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(BigDecimal precioCompra) { this.precioCompra = precioCompra; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public Integer getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }

    public TipoProducto getTipo() { return tipo; }
    public void setTipo(TipoProducto tipo) { this.tipo = tipo; }

    public Integer getCilindraje() { return cilindraje; }
    public void setCilindraje(Integer cilindraje) { this.cilindraje = cilindraje; }

    public Integer getAnio() { return anio; }
    public void setAnio(Integer anio) { this.anio = anio; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }

    public EstadoProducto getEstado() { return estado; }
    public void setEstado(EstadoProducto estado) { this.estado = estado; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
}
