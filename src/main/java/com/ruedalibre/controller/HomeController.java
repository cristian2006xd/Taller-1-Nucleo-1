package com.ruedalibre.controller;

import com.ruedalibre.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Map<String, Object> datos = dashboardService.obtenerDatos();
        datos.forEach(model::addAttribute);
        return "dashboard";
    }

    @GetMapping("/acceso-denegado")
    public String accesoDenegado() {
        return "acceso-denegado";
    }
}
