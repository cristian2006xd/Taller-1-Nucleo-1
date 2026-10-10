package com.ruedalibre.repository;

import com.ruedalibre.model.Cliente;
import com.ruedalibre.model.Venta;
import com.ruedalibre.model.enums.EstadoVenta;
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
public interface VentaRepository extends JpaRepository<Venta, Long> {

    Optional<Venta> findByNumeroComprobante(String numeroComprobante);

    List<Venta> findByClienteOrderByFechaDesc(Cliente cliente);

    boolean existsByCliente(Cliente cliente);

    long countByEstado(EstadoVenta estado);

    @Query("SELECT COALESCE(SUM(v.total), 0) FROM Venta v WHERE v.estado = 'COMPLETADA'")
    BigDecimal sumTotalVentasCompletadas();

    @Query("SELECT v FROM Venta v ORDER BY v.fecha DESC")
    List<Venta> findTop5ByOrderByFechaDesc(Pageable pageable);

    @Query("SELECT COALESCE(MAX(v.id), 0) FROM Venta v")
    Long findMaxId();

    @Query("SELECT v FROM Venta v WHERE " +
           "(:busqueda IS NULL OR LOWER(v.numeroComprobante) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
           "OR LOWER(v.cliente.nombres) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
           "OR LOWER(v.cliente.apellidos) LIKE LOWER(CONCAT('%', :busqueda, '%'))) " +
           "AND (:estado IS NULL OR v.estado = :estado)")
    Page<Venta> buscarConFiltros(
            @Param("busqueda") String busqueda,
            @Param("estado") EstadoVenta estado,
            Pageable pageable);
}
