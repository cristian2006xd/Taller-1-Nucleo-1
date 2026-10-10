package com.ruedalibre.service;

import com.ruedalibre.exception.ReglaNegocioException;
import com.ruedalibre.model.MovimientoInventario;
import com.ruedalibre.model.Producto;
import com.ruedalibre.model.Usuario;
import com.ruedalibre.model.enums.TipoMovimiento;
import com.ruedalibre.repository.MovimientoInventarioRepository;
import com.ruedalibre.repository.ProductoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InventarioService {

    private static final Logger log = LoggerFactory.getLogger(InventarioService.class);

    @Autowired
    private MovimientoInventarioRepository movimientoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Transactional
    public void registrarEntrada(Producto producto, int cantidad, String motivo, Usuario usuario) {
        if (cantidad <= 0) {
            throw new ReglaNegocioException("La cantidad de entrada debe ser mayor que cero.");
        }
        producto.setCantidad(producto.getCantidad() + cantidad);
        productoRepository.save(producto);

        MovimientoInventario mov = new MovimientoInventario();
        mov.setProducto(producto);
        mov.setCantidad(cantidad);
        mov.setTipo(TipoMovimiento.ENTRADA);
        mov.setMotivo(motivo);
        mov.setUsuario(usuario);
        movimientoRepository.save(mov);
        log.info("Entrada de {} unidades al producto id={}", cantidad, producto.getId());
    }

    @Transactional
    public void registrarSalida(Producto producto, int cantidad, String motivo, Usuario usuario) {
        if (cantidad <= 0) {
            throw new ReglaNegocioException("La cantidad de salida debe ser mayor que cero.");
        }
        if (producto.getCantidad() < cantidad) {
            throw new ReglaNegocioException("Stock insuficiente. Disponible: " + producto.getCantidad());
        }
        producto.setCantidad(producto.getCantidad() - cantidad);
        productoRepository.save(producto);

        MovimientoInventario mov = new MovimientoInventario();
        mov.setProducto(producto);
        mov.setCantidad(cantidad);
        mov.setTipo(TipoMovimiento.SALIDA);
        mov.setMotivo(motivo);
        mov.setUsuario(usuario);
        movimientoRepository.save(mov);
        log.info("Salida de {} unidades del producto id={}", cantidad, producto.getId());
    }

    @Transactional
    public void registrarAjuste(Producto producto, int nuevaCantidad, String motivo, Usuario usuario) {
        if (nuevaCantidad < 0) {
            throw new ReglaNegocioException("La cantidad de ajuste no puede ser negativa.");
        }
        int diferencia = nuevaCantidad - producto.getCantidad();
        producto.setCantidad(nuevaCantidad);
        productoRepository.save(producto);

        MovimientoInventario mov = new MovimientoInventario();
        mov.setProducto(producto);
        mov.setCantidad(diferencia);
        mov.setTipo(TipoMovimiento.AJUSTE);
        mov.setMotivo(motivo);
        mov.setUsuario(usuario);
        movimientoRepository.save(mov);
        log.info("Ajuste de inventario producto id={}: nueva cantidad={}", producto.getId(), nuevaCantidad);
    }

    public Page<MovimientoInventario> buscarConFiltros(Long productoId, TipoMovimiento tipo, Pageable pageable) {
        return movimientoRepository.buscarConFiltros(productoId, tipo, pageable);
    }

    public List<MovimientoInventario> historialPorProducto(Producto producto) {
        return movimientoRepository.findByProductoOrderByFechaDesc(producto);
    }
}
