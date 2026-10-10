package com.ruedalibre.controller;

import com.ruedalibre.exception.ReglaNegocioException;
import com.ruedalibre.model.Producto;
import com.ruedalibre.model.Usuario;
import com.ruedalibre.model.enums.TipoMovimiento;
import com.ruedalibre.repository.UsuarioRepository;
import com.ruedalibre.service.InventarioService;
import com.ruedalibre.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/inventario")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public String listar(@RequestParam(required = false) Long productoId,
                         @RequestParam(required = false) String tipo,
                         @RequestParam(defaultValue = "0") int pagina,
                         @RequestParam(defaultValue = "15") int tamanio,
                         Model model) {

        TipoMovimiento tipoEnum = null;
        if (tipo != null && !tipo.isBlank()) {
            try { tipoEnum = TipoMovimiento.valueOf(tipo); } catch (Exception ignored) {}
        }

        model.addAttribute("movimientos", inventarioService.buscarConFiltros(
                productoId, tipoEnum,
                PageRequest.of(pagina, tamanio, Sort.by(Sort.Direction.DESC, "fecha"))));
        model.addAttribute("productos", productoService.listarTodos());
        model.addAttribute("tipos", TipoMovimiento.values());
        model.addAttribute("productoId", productoId);
        model.addAttribute("tipoSeleccionado", tipo);
        model.addAttribute("stockBajo", productoService.listarProductosConStockBajo());
        model.addAttribute("agotados", productoService.listarProductosAgotados());
        return "inventario/lista";
    }

    @GetMapping("/entrada")
    public String formEntrada(Model model) {
        model.addAttribute("productos", productoService.listarActivos());
        return "inventario/entrada";
    }

    @PostMapping("/entrada")
    public String registrarEntrada(@RequestParam Long productoId,
                                   @RequestParam int cantidad,
                                   @RequestParam(required = false) String motivo,
                                   Authentication auth, RedirectAttributes redirect) {
        try {
            Producto producto = productoService.buscarPorId(productoId);
            Usuario usuario = obtenerUsuario(auth);
            inventarioService.registrarEntrada(producto, cantidad, motivo, usuario);
            redirect.addFlashAttribute("exito", "Entrada registrada correctamente.");
        } catch (ReglaNegocioException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/inventario";
    }

    @GetMapping("/salida")
    public String formSalida(Model model) {
        model.addAttribute("productos", productoService.listarActivos());
        return "inventario/salida";
    }

    @PostMapping("/salida")
    public String registrarSalida(@RequestParam Long productoId,
                                  @RequestParam int cantidad,
                                  @RequestParam(required = false) String motivo,
                                  Authentication auth, RedirectAttributes redirect) {
        try {
            Producto producto = productoService.buscarPorId(productoId);
            Usuario usuario = obtenerUsuario(auth);
            inventarioService.registrarSalida(producto, cantidad, motivo, usuario);
            redirect.addFlashAttribute("exito", "Salida registrada correctamente.");
        } catch (ReglaNegocioException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/inventario";
    }

    @GetMapping("/ajuste")
    public String formAjuste(Model model) {
        model.addAttribute("productos", productoService.listarActivos());
        return "inventario/ajuste";
    }

    @PostMapping("/ajuste")
    public String registrarAjuste(@RequestParam Long productoId,
                                  @RequestParam int nuevaCantidad,
                                  @RequestParam(required = false) String motivo,
                                  Authentication auth, RedirectAttributes redirect) {
        try {
            Producto producto = productoService.buscarPorId(productoId);
            Usuario usuario = obtenerUsuario(auth);
            inventarioService.registrarAjuste(producto, nuevaCantidad, motivo, usuario);
            redirect.addFlashAttribute("exito", "Ajuste registrado correctamente.");
        } catch (ReglaNegocioException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/inventario";
    }

    private Usuario obtenerUsuario(Authentication auth) {
        return usuarioRepository.findByNombreUsuario(auth.getName())
                .orElseThrow(() -> new ReglaNegocioException("Usuario no encontrado en sesión."));
    }
}
