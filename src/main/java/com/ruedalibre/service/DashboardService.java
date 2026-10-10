package com.ruedalibre.service;

import com.ruedalibre.model.Venta;
import com.ruedalibre.model.enums.EstadoVenta;
import com.ruedalibre.model.enums.TipoProducto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private VentaService ventaService;

    public Map<String, Object> obtenerDatos() {
        Map<String, Object> datos = new HashMap<>();

        datos.put("totalMotos", productoService.contarPorTipo(TipoProducto.MOTO));
        datos.put("totalRepuestos", productoService.contarPorTipo(TipoProducto.REPUESTO));
        datos.put("totalAccesorios", productoService.contarPorTipo(TipoProducto.ACCESORIO));
        datos.put("totalClientes", clienteService.listarTodos().size());
        datos.put("totalVentas", ventaService.contarPorEstado(EstadoVenta.COMPLETADA));
        datos.put("valorVentas", ventaService.sumaTotalVentasCompletadas());
        datos.put("valorInventario", productoService.calcularValorTotalInventario());
        datos.put("stockBajo", productoService.listarProductosConStockBajo().size());

        List<Venta> ultimas5 = ventaService.listarUltimas5();
        datos.put("ultimasVentas", ultimas5);

        return datos;
    }
}
