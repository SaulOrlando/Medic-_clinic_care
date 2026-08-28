package org.esfe.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String inicio() {
        return "redirect:/panel";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/panel")
    public String panel(Model model) {
        model.addAttribute("activePage", "panel");
        // Datos de ejemplo para el diseño — se reemplazan cuando haya consultas reales
        if (!model.containsAttribute("citasHoy")) model.addAttribute("citasHoy", 12);
        if (!model.containsAttribute("pacientesRecientes")) model.addAttribute("pacientesRecientes", 48);
        if (!model.containsAttribute("reportesPendientes")) model.addAttribute("reportesPendientes", 5);
        return "panel";
    }
}
