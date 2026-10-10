package com.ruedalibre.repository;

import com.ruedalibre.model.MovimientoInventario;
import com.ruedalibre.model.Producto;
import com.ruedalibre.model.enums.TipoMovimiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

    List<MovimientoInventario> findByProductoOrderByFechaDesc(Producto producto);

    Page<MovimientoInventario> findAllByOrderByFechaDesc(Pageable pageable);

    @Query("SELECT m FROM MovimientoInventario m WHERE " +
           "(:productoId IS NULL OR m.producto.id = :productoId) " +
           "AND (:tipo IS NULL OR m.tipo = :tipo) " +
           "ORDER BY m.fecha DESC")
    Page<MovimientoInventario> buscarConFiltros(
            @Param("productoId") Long productoId,
            @Param("tipo") TipoMovimiento tipo,
            Pageable pageable);
}
