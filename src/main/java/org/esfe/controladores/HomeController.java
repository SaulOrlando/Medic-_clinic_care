package org.esfe.controladores;

import org.esfe.modelos.Usuario;
import org.esfe.servicios.interfaces.IDashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@Controller
public class HomeController {

    private final IDashboardService dashboardService;

    public HomeController(IDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/")
    public String inicio() {
        return "redirect:/panel";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/panel")
    public String panel(@ModelAttribute("usuario") Usuario usuario, Model model) {
        List<Long> volumenSemanal = dashboardService.contarVolumenSemanal(usuario);
        long volumenMax = volumenSemanal.stream().mapToLong(Long::longValue).max().orElse(0);
        if (volumenMax == 0) {
            volumenMax = 1;
        }

        model.addAttribute("activePage", "panel");
        model.addAttribute("citasHoy", dashboardService.contarCitasHoy(usuario));
        model.addAttribute("pacientesRecientes", dashboardService.contarPacientesRecientes(usuario));
        model.addAttribute("reportesPendientes", dashboardService.contarInformesPendientes(usuario));
        model.addAttribute("volumenSemanal", volumenSemanal);
        model.addAttribute("volumenMax", volumenMax);
        model.addAttribute("proximasCitas", dashboardService.obtenerProximasCitas(usuario, 5));
        return "panel";
    }
}