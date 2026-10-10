package com.ruedalibre.repository;

import com.ruedalibre.model.Categoria;
import com.ruedalibre.model.Producto;
import com.ruedalibre.model.enums.EstadoProducto;
import com.ruedalibre.model.enums.TipoProducto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    Optional<Producto> findByCodigo(String codigo);

    Optional<Producto> findByVin(String vin);

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, Long id);

    boolean existsByVin(String vin);

    boolean existsByVinAndIdNot(String vin, Long id);

    Page<Producto> findByNombreContainingIgnoreCaseOrCodigoContainingIgnoreCaseOrMarcaContainingIgnoreCase(
            String nombre, String codigo, String marca, Pageable pageable);

    Page<Producto> findByTipo(TipoProducto tipo, Pageable pageable);

    Page<Producto> findByCategoria(Categoria categoria, Pageable pageable);

    Page<Producto> findByTipoAndCategoria(TipoProducto tipo, Categoria categoria, Pageable pageable);

    long countByTipo(TipoProducto tipo);

    long countByEstado(EstadoProducto estado);

    @Query("SELECT p FROM Producto p WHERE p.cantidad <= p.stockMinimo AND p.estado = 'ACTIVO'")
    List<Producto> findProductosConStockBajo();

    @Query("SELECT p FROM Producto p WHERE p.cantidad = 0 AND p.estado = 'ACTIVO'")
    List<Producto> findProductosAgotados();

    @Query("SELECT SUM(p.precioCompra * p.cantidad) FROM Producto p WHERE p.estado = 'ACTIVO'")
    BigDecimal calcularValorTotalInventario();

    @Query("SELECT p FROM Producto p WHERE " +
           "(:busqueda IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
           "OR LOWER(p.codigo) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
           "OR LOWER(p.marca) LIKE LOWER(CONCAT('%', :busqueda, '%'))) " +
           "AND (:tipo IS NULL OR p.tipo = :tipo) " +
           "AND (:categoriaId IS NULL OR p.categoria.id = :categoriaId)")
    Page<Producto> buscarConFiltros(
            @Param("busqueda") String busqueda,
            @Param("tipo") TipoProducto tipo,
            @Param("categoriaId") Long categoriaId,
            Pageable pageable);
}
