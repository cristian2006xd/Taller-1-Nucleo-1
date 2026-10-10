package com.ruedalibre.controller;

import com.ruedalibre.exception.ReglaNegocioException;
import com.ruedalibre.model.Usuario;
import com.ruedalibre.model.enums.RolUsuario;
import com.ruedalibre.service.UsuarioService;
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
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String listar(@RequestParam(required = false) String busqueda,
                         @RequestParam(defaultValue = "0") int pagina,
                         @RequestParam(defaultValue = "10") int tamanio,
                         Model model) {

        Page<Usuario> page = usuarioService.buscar(busqueda,
                PageRequest.of(pagina, tamanio, Sort.by("nombreCompleto")));

        model.addAttribute("usuarios", page.getContent());
        model.addAttribute("page", page);
        model.addAttribute("busqueda", busqueda);
        return "usuarios/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", RolUsuario.values());
        return "usuarios/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Usuario usuario,
                          BindingResult result,
                          @RequestParam(required = false) String nuevaContrasena,
                          Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("roles", RolUsuario.values());
            return "usuarios/formulario";
        }
        try {
            usuarioService.guardar(usuario, nuevaContrasena);
            redirect.addFlashAttribute("exito", "Usuario guardado correctamente.");
        } catch (ReglaNegocioException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("roles", RolUsuario.values());
            return "usuarios/formulario";
        }
        return "redirect:/usuarios";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id);
        usuario.setContrasena("");
        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", RolUsuario.values());
        return "usuarios/formulario";
    }

    @GetMapping("/detalle/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        model.addAttribute("usuario", usuarioService.buscarPorId(id));
        return "usuarios/detalle";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            usuarioService.eliminar(id);
            redirect.addFlashAttribute("exito", "Usuario eliminado correctamente.");
        } catch (ReglaNegocioException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/usuarios";
    }

    @PostMapping("/toggle-activo/{id}")
    public String toggleActivo(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            usuarioService.toggleActivo(id);
            redirect.addFlashAttribute("exito", "Estado del usuario actualizado.");
        } catch (ReglaNegocioException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/usuarios";
    }
}
