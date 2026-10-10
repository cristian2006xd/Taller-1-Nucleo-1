package com.ruedalibre.controller;

import com.ruedalibre.exception.ReglaNegocioException;
import com.ruedalibre.model.Categoria;
import com.ruedalibre.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "categorias/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("categoria", new Categoria());
        return "categorias/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Categoria categoria, BindingResult result,
                          Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "categorias/formulario";
        }
        try {
            categoriaService.guardar(categoria);
            redirect.addFlashAttribute("exito", "Categoría guardada correctamente.");
        } catch (ReglaNegocioException e) {
            model.addAttribute("error", e.getMessage());
            return "categorias/formulario";
        }
        return "redirect:/categorias";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("categoria", categoriaService.buscarPorId(id));
        return "categorias/formulario";
    }

    @GetMapping("/detalle/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        model.addAttribute("categoria", categoriaService.buscarPorId(id));
        return "categorias/detalle";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            categoriaService.eliminar(id);
            redirect.addFlashAttribute("exito", "Categoría eliminada correctamente.");
        } catch (ReglaNegocioException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/categorias";
    }
}
