package org.esfe.security;

import org.esfe.modelos.Usuario;
import org.esfe.repositorios.IUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @ModelAttribute("usuario")
    public Usuario usuarioActual(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return null;
        }
        return usuarioRepository.findByCorreo(userDetails.getUsername())
                .orElseGet(() -> {
                    Usuario usuario = new Usuario();
                    usuario.setCorreo(userDetails.getUsername());
                    usuario.setNombreCompleto(userDetails.getUsername());
                    return usuario;
                });
    }

    @ModelAttribute("rolNombre")
    public String rolNombre(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return null;
        }
        return userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(this::etiquetaRol)
                .findFirst()
                .orElse(null);
    }

    private String etiquetaRol(String autoridad) {
        return switch (autoridad) {
            case "ROLE_ADMINISTRADOR" -> "Administrador";
            case "ROLE_MEDICO" -> "Médico";
            case "ROLE_RECEPCIONISTA" -> "Recepcionista";
            case "ROLE_ENCARGADO_INVENTARIO" -> "Encargado de Inventario";
            default -> autoridad.replace("ROLE_", "");
        };
    }
}