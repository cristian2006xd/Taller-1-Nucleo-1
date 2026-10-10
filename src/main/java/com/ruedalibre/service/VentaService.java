package com.ruedalibre.service;

import com.ruedalibre.exception.RecursoNoEncontradoException;
import com.ruedalibre.exception.ReglaNegocioException;
import com.ruedalibre.model.*;
import com.ruedalibre.model.enums.EstadoProducto;
import com.ruedalibre.model.enums.EstadoVenta;
import com.ruedalibre.model.enums.TipoMovimiento;
import com.ruedalibre.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class VentaService {

    private static final Logger log = LoggerFactory.getLogger(VentaService.class);

    @Value("${app.iva:0.15}")
    private BigDecimal tasaIva;

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private MovimientoInventarioRepository movimientoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public Venta crearVenta(Long clienteId, Long usuarioId, List<Long> productoIds,
                            List<Integer> cantidades, BigDecimal descuento, String metodoPago) {

        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado"));
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        if (productoIds == null || productoIds.isEmpty()) {
            throw new ReglaNegocioException("La venta debe tener al menos un producto.");
        }

        Venta venta = new Venta();
        venta.setCliente(cliente);
        venta.setUsuario(usuario);
        venta.setNumeroComprobante(generarNumeroComprobante());

        com.ruedalibre.model.enums.MetodoPago mp;
        try {
            mp = com.ruedalibre.model.enums.MetodoPago.valueOf(metodoPago);
        } catch (Exception e) {
            throw new ReglaNegocioException("Método de pago inválido: " + metodoPago);
        }
        venta.setMetodoPago(mp);

        if (descuento == null) descuento = BigDecimal.ZERO;
        if (descuento.compareTo(BigDecimal.ZERO) < 0) {
            throw new ReglaNegocioException("El descuento no puede ser negativo.");
        }

        venta = ventaRepository.save(venta);

        BigDecimal subtotalVenta = BigDecimal.ZERO;
        List<DetalleVenta> detalles = new ArrayList<>();

        for (int i = 0; i < productoIds.size(); i++) {
            Long productoId = productoIds.get(i);
            int cantidad = cantidades.get(i);

            Producto producto = productoRepository.findById(productoId)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado: " + productoId));

            if (producto.getEstado() == EstadoProducto.INACTIVO) {
                throw new ReglaNegocioException("El producto '" + producto.getNombre() + "' está inactivo.");
            }
            if (producto.getCantidad() < cantidad) {
                throw new ReglaNegocioException("Stock insuficiente para '" + producto.getNombre()
                        + "'. Disponible: " + producto.getCantidad());
            }
            if (cantidad <= 0) {
                throw new ReglaNegocioException("La cantidad debe ser mayor que cero.");
            }

            BigDecimal subtotalDetalle = producto.getPrecio().multiply(BigDecimal.valueOf(cantidad));

            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(venta);
            detalle.setProducto(producto);
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.setSubtotal(subtotalDetalle);
            detalles.add(detalle);

            producto.setCantidad(producto.getCantidad() - cantidad);
            productoRepository.save(producto);

            MovimientoInventario mov = new MovimientoInventario();
            mov.setProducto(producto);
            mov.setCantidad(cantidad);
            mov.setTipo(TipoMovimiento.VENTA);
            mov.setMotivo("Venta " + venta.getNumeroComprobante());
            mov.setUsuario(usuario);
            movimientoRepository.save(mov);

            subtotalVenta = subtotalVenta.add(subtotalDetalle);
        }

        detalleVentaRepository.saveAll(detalles);

        BigDecimal baseImponible = subtotalVenta.subtract(descuento);
        if (baseImponible.compareTo(BigDecimal.ZERO) < 0) {
            throw new ReglaNegocioException("El descuento no puede ser mayor al subtotal.");
        }
        BigDecimal ivaCalculado = baseImponible.multiply(tasaIva).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalVenta = baseImponible.add(ivaCalculado).setScale(2, RoundingMode.HALF_UP);

        venta.setSubtotal(subtotalVenta.setScale(2, RoundingMode.HALF_UP));
        venta.setDescuento(descuento.setScale(2, RoundingMode.HALF_UP));
        venta.setIva(ivaCalculado);
        venta.setTotal(totalVenta);
        venta.setDetalles(detalles);

        ventaRepository.save(venta);
        log.info("Venta creada: {} por usuario id={}", venta.getNumeroComprobante(), usuarioId);
        return venta;
    }

    @Transactional
    public void anularVenta(Long ventaId, Usuario usuarioActual) {
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Venta no encontrada con id: " + ventaId));

        if (venta.getEstado() == EstadoVenta.ANULADA) {
            throw new ReglaNegocioException("La venta ya está anulada.");
        }

        List<DetalleVenta> detalles = detalleVentaRepository.findByVenta(venta);
        for (DetalleVenta detalle : detalles) {
            Producto producto = detalle.getProducto();
            producto.setCantidad(producto.getCantidad() + detalle.getCantidad());
            productoRepository.save(producto);

            MovimientoInventario mov = new MovimientoInventario();
            mov.setProducto(producto);
            mov.setCantidad(detalle.getCantidad());
            mov.setTipo(TipoMovimiento.ANULACION);
            mov.setMotivo("Anulación de venta " + venta.getNumeroComprobante());
            mov.setUsuario(usuarioActual);
            movimientoRepository.save(mov);
        }

        venta.setEstado(EstadoVenta.ANULADA);
        ventaRepository.save(venta);
        log.info("Venta anulada: {} por usuario id={}", venta.getNumeroComprobante(), usuarioActual.getId());
    }

    public Venta buscarPorId(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Venta no encontrada con id: " + id));
    }

    public Page<Venta> buscarConFiltros(String busqueda, EstadoVenta estado, Pageable pageable) {
        return ventaRepository.buscarConFiltros(busqueda, estado, pageable);
    }

    public List<Venta> listarUltimas5() {
        return ventaRepository.findTop5ByOrderByFechaDesc(PageRequest.of(0, 5));
    }

    public long contarPorEstado(EstadoVenta estado) {
        return ventaRepository.countByEstado(estado);
    }

    public BigDecimal sumaTotalVentasCompletadas() {
        BigDecimal total = ventaRepository.sumTotalVentasCompletadas();
        return total != null ? total : BigDecimal.ZERO;
    }

    private String generarNumeroComprobante() {
        Long maxId = ventaRepository.findMaxId();
        long siguiente = (maxId != null ? maxId : 0L) + 1;
        return String.format("RL-%06d", siguiente);
    }

    public List<Venta> historialPorCliente(Cliente cliente) {
        return ventaRepository.findByClienteOrderByFechaDesc(cliente);
    }
}
