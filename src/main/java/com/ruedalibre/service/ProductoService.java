package com.ruedalibre.service;

import com.ruedalibre.exception.RecursoNoEncontradoException;
import com.ruedalibre.exception.ReglaNegocioException;
import com.ruedalibre.model.Producto;
import com.ruedalibre.model.enums.EstadoProducto;
import com.ruedalibre.model.enums.TipoProducto;
import com.ruedalibre.repository.ProductoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductoService {

    private static final Logger log = LoggerFactory.getLogger(ProductoService.class);

    @Autowired
    private ProductoRepository productoRepository;

    public Page<Producto> buscarConFiltros(String busqueda, TipoProducto tipo, Long categoriaId, Pageable pageable) {
        return productoRepository.buscarConFiltros(busqueda, tipo, categoriaId, pageable);
    }

    public Producto buscarPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con id: " + id));
    }

    public Producto guardar(Producto producto) {
        validarProducto(producto);
        if (producto.getId() == null) {
            log.info("Creando producto: {}", producto.getCodigo());
        } else {
            log.info("Editando producto id={}: {}", producto.getId(), producto.getCodigo());
        }
        return productoRepository.save(producto);
    }

    private void validarProducto(Producto producto) {
        if (producto.getId() == null) {
            if (productoRepository.existsByCodigo(producto.getCodigo())) {
                throw new ReglaNegocioException("Ya existe un producto con el código: " + producto.getCodigo());
            }
            if (producto.getVin() != null && !producto.getVin().isBlank() && productoRepository.existsByVin(producto.getVin())) {
                throw new ReglaNegocioException("Ya existe una moto con el VIN: " + producto.getVin());
            }
        } else {
            if (productoRepository.existsByCodigoAndIdNot(producto.getCodigo(), producto.getId())) {
                throw new ReglaNegocioException("Ya existe un producto con el código: " + producto.getCodigo());
            }
            if (producto.getVin() != null && !producto.getVin().isBlank()
                    && productoRepository.existsByVinAndIdNot(producto.getVin(), producto.getId())) {
                throw new ReglaNegocioException("Ya existe una moto con el VIN: " + producto.getVin());
            }
        }
        if (producto.getPrecio() != null && producto.getPrecioCompra() != null
                && producto.getPrecio().compareTo(producto.getPrecioCompra()) < 0) {
            throw new ReglaNegocioException("El precio de venta no puede ser menor al precio de compra.");
        }
    }

    public void eliminar(Long id) {
        Producto producto = buscarPorId(id);
        log.info("Eliminando producto id={}: {}", id, producto.getCodigo());
        productoRepository.deleteById(id);
    }

    public List<Producto> listarProductosConStockBajo() {
        return productoRepository.findProductosConStockBajo();
    }

    public List<Producto> listarProductosAgotados() {
        return productoRepository.findProductosAgotados();
    }

    public BigDecimal calcularValorTotalInventario() {
        BigDecimal valor = productoRepository.calcularValorTotalInventario();
        return valor != null ? valor : BigDecimal.ZERO;
    }

    public long contarPorTipo(TipoProducto tipo) {
        return productoRepository.countByTipo(tipo);
    }

    public long contarPorEstado(EstadoProducto estado) {
        return productoRepository.countByEstado(estado);
    }

    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    public List<Producto> listarActivos() {
        return productoRepository.findAll().stream()
                .filter(p -> p.getEstado() == EstadoProducto.ACTIVO)
                .toList();
    }
}
