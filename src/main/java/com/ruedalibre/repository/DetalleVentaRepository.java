package com.ruedalibre.repository;

import com.ruedalibre.model.DetalleVenta;
import com.ruedalibre.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {

    List<DetalleVenta> findByVenta(Venta venta);
}
