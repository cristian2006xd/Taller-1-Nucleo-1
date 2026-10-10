package com.ruedalibre.controller;

import com.ruedalibre.exception.ReglaNegocioException;
import com.ruedalibre.model.DetalleVenta;
import com.ruedalibre.model.Usuario;
import com.ruedalibre.model.Venta;
import com.ruedalibre.model.enums.EstadoVenta;
import com.ruedalibre.model.enums.MetodoPago;
import com.ruedalibre.repository.DetalleVentaRepository;
import com.ruedalibre.repository.UsuarioRepository;
import com.ruedalibre.service.ClienteService;
import com.ruedalibre.service.ProductoService;
import com.ruedalibre.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/ventas")
public class VentaController {

    @Value("${app.iva:0.15}")
    private BigDecimal tasaIva;

    @Autowired
    private VentaService ventaService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    @GetMapping
    public String listar(@RequestParam(required = false) String busqueda,
                         @RequestParam(required = false) String estado,
                         @RequestParam(defaultValue = "0") int pagina,
                         @RequestParam(defaultValue = "10") int tamanio,
                         Model model) {

        EstadoVenta estadoEnum = null;
        if (estado != null && !estado.isBlank()) {
            try { estadoEnum = EstadoVenta.valueOf(estado); } catch (Exception ignored) {}
        }

        Page<Venta> page = ventaService.buscarConFiltros(busqueda, estadoEnum,
                PageRequest.of(pagina, tamanio, Sort.by(Sort.Direction.DESC, "fecha")));

        model.addAttribute("ventas", page.getContent());
        model.addAttribute("page", page);
        model.addAttribute("busqueda", busqueda);
        model.addAttribute("estadoSeleccionado", estado);
        model.addAttribute("estados", EstadoVenta.values());
        return "ventas/lista";
    }

    @GetMapping("/nueva")
    public String nueva(Model model) {
        model.addAttribute("clientes", clienteService.listarTodos());
        model.addAttribute("productos", productoService.listarActivos());
        model.addAttribute("metodosPago", MetodoPago.values());
        model.addAttribute("tasaIva", tasaIva);
        return "ventas/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam Long clienteId,
                          @RequestParam List<Long> productoIds,
                          @RequestParam List<Integer> cantidades,
                          @RequestParam(defaultValue = "0") BigDecimal descuento,
                          @RequestParam String metodoPago,
                          Authentication auth,
                          RedirectAttributes redirect) {
        try {
            Usuario usuario = obtenerUsuario(auth);
            Venta venta = ventaService.crearVenta(clienteId, usuario.getId(),
                    productoIds, cantidades, descuento, metodoPago);
            redirect.addFlashAttribute("exito", "Venta " + venta.getNumeroComprobante() + " registrada correctamente.");
            return "redirect:/ventas/detalle/" + venta.getId();
        } catch (ReglaNegocioException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/ventas/nueva";
        }
    }

    @GetMapping("/detalle/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        Venta venta = ventaService.buscarPorId(id);
        List<DetalleVenta> detalles = detalleVentaRepository.findByVenta(venta);
        model.addAttribute("venta", venta);
        model.addAttribute("detalles", detalles);
        return "ventas/detalle";
    }

    @PostMapping("/anular/{id}")
    public String anular(@PathVariable Long id, Authentication auth, RedirectAttributes redirect) {
        try {
            Usuario usuario = obtenerUsuario(auth);
            ventaService.anularVenta(id, usuario);
            redirect.addFlashAttribute("exito", "Venta anulada correctamente.");
        } catch (ReglaNegocioException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ventas/detalle/" + id;
    }

    private Usuario obtenerUsuario(Authentication auth) {
        return usuarioRepository.findByNombreUsuario(auth.getName())
                .orElseThrow(() -> new ReglaNegocioException("Usuario no encontrado en sesión."));
    }
}
