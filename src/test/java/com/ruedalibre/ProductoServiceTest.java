package com.ruedalibre;

import com.ruedalibre.exception.ReglaNegocioException;
import com.ruedalibre.model.Producto;
import com.ruedalibre.model.enums.EstadoProducto;
import com.ruedalibre.model.enums.TipoProducto;
import com.ruedalibre.repository.ProductoRepository;
import com.ruedalibre.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    private Producto producto;

    @BeforeEach
    void setUp() {
        producto = new Producto();
        producto.setCodigo("TEST-001");
        producto.setNombre("Producto Test");
        producto.setMarca("Marca Test");
        producto.setPrecioCompra(new BigDecimal("100.00"));
        producto.setPrecio(new BigDecimal("150.00"));
        producto.setCantidad(10);
        producto.setStockMinimo(2);
        producto.setTipo(TipoProducto.REPUESTO);
        producto.setEstado(EstadoProducto.ACTIVO);
    }

    @Test
    void guardar_productoNuevo_exitoso() {
        when(productoRepository.existsByCodigo(anyString())).thenReturn(false);
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        Producto resultado = productoService.guardar(producto);

        assertNotNull(resultado);
        assertEquals("TEST-001", resultado.getCodigo());
        verify(productoRepository, times(1)).save(producto);
    }

    @Test
    void guardar_codigoDuplicado_lanzaExcepcion() {
        when(productoRepository.existsByCodigo("TEST-001")).thenReturn(true);

        assertThrows(ReglaNegocioException.class, () -> productoService.guardar(producto));
        verify(productoRepository, never()).save(any());
    }

    @Test
    void guardar_precioVentaMenorQueCompra_lanzaExcepcion() {
        when(productoRepository.existsByCodigo(anyString())).thenReturn(false);
        producto.setPrecio(new BigDecimal("50.00"));

        assertThrows(ReglaNegocioException.class, () -> productoService.guardar(producto));
        verify(productoRepository, never()).save(any());
    }

    @Test
    void buscarPorId_existente_retornaProducto() {
        producto.setId(1L);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        Producto resultado = productoService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void buscarPorId_noExistente_lanzaExcepcion() {
        when(productoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(com.ruedalibre.exception.RecursoNoEncontradoException.class,
                () -> productoService.buscarPorId(999L));
    }

    @Test
    void calcularValorTotalInventario_retornaCero_cuandoEsNull() {
        when(productoRepository.calcularValorTotalInventario()).thenReturn(null);

        BigDecimal valor = productoService.calcularValorTotalInventario();

        assertEquals(BigDecimal.ZERO, valor);
    }

    @Test
    void calcularValorTotalInventario_retornaValor_correcto() {
        BigDecimal esperado = new BigDecimal("5000.00");
        when(productoRepository.calcularValorTotalInventario()).thenReturn(esperado);

        BigDecimal valor = productoService.calcularValorTotalInventario();

        assertEquals(esperado, valor);
    }
}
