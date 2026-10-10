package com.ruedalibre.controller;

import com.ruedalibre.exception.ReglaNegocioException;
import com.ruedalibre.model.Cliente;
import com.ruedalibre.model.Venta;
import com.ruedalibre.service.ClienteService;
import com.ruedalibre.service.VentaService;
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

import java.util.List;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private VentaService ventaService;

    @GetMapping
    public String listar(@RequestParam(required = false) String busqueda,
                         @RequestParam(defaultValue = "0") int pagina,
                         @RequestParam(defaultValue = "10") int tamanio,
                         Model model) {

        Page<Cliente> page = clienteService.buscar(busqueda,
                PageRequest.of(pagina, tamanio, Sort.by("apellidos")));

        model.addAttribute("clientes", page.getContent());
        model.addAttribute("page", page);
        model.addAttribute("busqueda", busqueda);
        return "clientes/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "clientes/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Cliente cliente, BindingResult result,
                          Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "clientes/formulario";
        }
        try {
            clienteService.guardar(cliente);
            redirect.addFlashAttribute("exito", "Cliente guardado correctamente.");
        } catch (ReglaNegocioException e) {
            model.addAttribute("error", e.getMessage());
            return "clientes/formulario";
        }
        return "redirect:/clientes";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("cliente", clienteService.buscarPorId(id));
        return "clientes/formulario";
    }

    @GetMapping("/detalle/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        Cliente cliente = clienteService.buscarPorId(id);
        List<Venta> ventas = ventaService.historialPorCliente(cliente);
        model.addAttribute("cliente", cliente);
        model.addAttribute("ventas", ventas);
        return "clientes/detalle";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            clienteService.eliminar(id);
            redirect.addFlashAttribute("exito", "Cliente eliminado correctamente.");
        } catch (ReglaNegocioException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/clientes";
    }
}
