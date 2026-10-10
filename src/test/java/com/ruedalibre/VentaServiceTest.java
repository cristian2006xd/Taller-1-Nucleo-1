package com.ruedalibre;

import com.ruedalibre.exception.ReglaNegocioException;
import com.ruedalibre.model.*;
import com.ruedalibre.model.enums.*;
import com.ruedalibre.repository.*;
import com.ruedalibre.service.VentaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class VentaServiceTest {

    @Mock
    private VentaRepository ventaRepository;
    @Mock
    private DetalleVentaRepository detalleVentaRepository;
    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private MovimientoInventarioRepository movimientoRepository;
    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private VentaService ventaService;

    private Cliente cliente;
    private Usuario usuario;
    private Producto producto;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(ventaService, "tasaIva", new BigDecimal("0.15"));

        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombres("Juan");
        cliente.setApellidos("Test");
        cliente.setCedula("1234567890");

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombreCompleto("Admin Test");
        usuario.setNombreUsuario("admin");

        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Producto Test");
        producto.setCodigo("TEST-001");
        producto.setPrecio(new BigDecimal("100.00"));
        producto.setCantidad(10);
        producto.setEstado(EstadoProducto.ACTIVO);
    }

    @Test
    void crearVenta_sinProductos_lanzaExcepcion() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        assertThrows(ReglaNegocioException.class, () ->
                ventaService.crearVenta(1L, 1L, List.of(), List.of(), BigDecimal.ZERO, "EFECTIVO"));
    }

    @Test
    void crearVenta_productoInactivo_lanzaExcepcion() {
        producto.setEstado(EstadoProducto.INACTIVO);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(ventaRepository.findMaxId()).thenReturn(0L);
        Venta ventaMock = new Venta();
        ventaMock.setId(1L);
        ventaMock.setNumeroComprobante("RL-000001");
        when(ventaRepository.save(any())).thenReturn(ventaMock);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        assertThrows(ReglaNegocioException.class, () ->
                ventaService.crearVenta(1L, 1L, Arrays.asList(1L), Arrays.asList(2), BigDecimal.ZERO, "EFECTIVO"));
    }

    @Test
    void crearVenta_stockInsuficiente_lanzaExcepcion() {
        producto.setCantidad(1);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(ventaRepository.findMaxId()).thenReturn(0L);
        Venta ventaMock = new Venta();
        ventaMock.setId(1L);
        ventaMock.setNumeroComprobante("RL-000001");
        when(ventaRepository.save(any())).thenReturn(ventaMock);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        assertThrows(ReglaNegocioException.class, () ->
                ventaService.crearVenta(1L, 1L, Arrays.asList(1L), Arrays.asList(5), BigDecimal.ZERO, "EFECTIVO"));
    }

    @Test
    void anularVenta_yaAnulada_lanzaExcepcion() {
        Venta venta = new Venta();
        venta.setId(1L);
        venta.setEstado(EstadoVenta.ANULADA);
        venta.setNumeroComprobante("RL-000001");

        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));

        assertThrows(ReglaNegocioException.class, () ->
                ventaService.anularVenta(1L, usuario));
    }

    @Test
    void crearVenta_metodoPagoInvalido_lanzaExcepcion() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(ventaRepository.findMaxId()).thenReturn(0L);
        Venta ventaMock = new Venta();
        ventaMock.setId(1L);
        ventaMock.setNumeroComprobante("RL-000001");
        when(ventaRepository.save(any())).thenReturn(ventaMock);

        assertThrows(ReglaNegocioException.class, () ->
                ventaService.crearVenta(1L, 1L, Arrays.asList(1L), Arrays.asList(1), BigDecimal.ZERO, "INVALIDO"));
    }
}
