package com.ruedalibre.controller;

import com.ruedalibre.exception.ReglaNegocioException;
import com.ruedalibre.model.Categoria;
import com.ruedalibre.model.Producto;
import com.ruedalibre.model.enums.EstadoProducto;
import com.ruedalibre.model.enums.TipoProducto;
import com.ruedalibre.service.CategoriaService;
import com.ruedalibre.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public String listar(@RequestParam(required = false) String busqueda,
                         @RequestParam(required = false) String tipo,
                         @RequestParam(required = false) Long categoriaId,
                         @RequestParam(defaultValue = "0") int pagina,
                         @RequestParam(defaultValue = "10") int tamanio,
                         Model model) {

        TipoProducto tipoEnum = null;
        if (tipo != null && !tipo.isBlank()) {
            try { tipoEnum = TipoProducto.valueOf(tipo); } catch (Exception ignored) {}
        }

        Page<Producto> page = productoService.buscarConFiltros(
                busqueda, tipoEnum, categoriaId,
                PageRequest.of(pagina, tamanio, Sort.by("nombre")));

        model.addAttribute("productos", page.getContent());
        model.addAttribute("page", page);
        model.addAttribute("busqueda", busqueda);
        model.addAttribute("tipoSeleccionado", tipo);
        model.addAttribute("categoriaId", categoriaId);
        model.addAttribute("tipos", TipoProducto.values());
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "productos/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("producto", new Producto());
        cargarFormulario(model);
        return "productos/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Producto producto, BindingResult result,
                          Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            cargarFormulario(model);
            return "productos/formulario";
        }
        try {
            if (producto.getTipo() != TipoProducto.MOTO) {
                producto.setVin(null);
            }
            productoService.guardar(producto);
            redirect.addFlashAttribute("exito", "Producto guardado correctamente.");
        } catch (ReglaNegocioException e) {
            model.addAttribute("error", e.getMessage());
            cargarFormulario(model);
            return "productos/formulario";
        }
        return "redirect:/productos";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("producto", productoService.buscarPorId(id));
        cargarFormulario(model);
        return "productos/formulario";
    }

    @GetMapping("/detalle/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        model.addAttribute("producto", productoService.buscarPorId(id));
        return "productos/detalle";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            productoService.eliminar(id);
            redirect.addFlashAttribute("exito", "Producto eliminado correctamente.");
        } catch (ReglaNegocioException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/productos";
    }

    private void cargarFormulario(Model model) {
        model.addAttribute("categorias", categoriaService.listarTodas());
        model.addAttribute("tipos", TipoProducto.values());
        model.addAttribute("estados", EstadoProducto.values());
    }
}
