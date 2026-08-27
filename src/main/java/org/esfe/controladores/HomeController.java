package org.esfe.controladores;

import org.esfe.modelos.Usuario;
import org.esfe.repositorios.IUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
public class HomeController {

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @GetMapping("/")
    public String inicio() {
        return "redirect:/panel";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/panel")
    public String panel(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Optional<Usuario> usuario = usuarioRepository.findByCorreo(userDetails.getUsername());
        usuario.ifPresent(u -> model.addAttribute("usuario", u));
        return "panel";
    }
}
