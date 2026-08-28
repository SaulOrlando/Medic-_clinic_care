package org.esfe.controladores;

import org.esfe.modelos.Usuario;
import org.esfe.servicios.interfaces.IUsuarioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Base64;
import java.util.Map;

@Controller
@RequestMapping("/configuracion")
public class ConfiguracionController {

    private final IUsuarioService usuarioService;

    public ConfiguracionController(IUsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String perfil(@AuthenticationPrincipal UserDetails userDetails,
                         Model model) {
        cargarVista(userDetails, model);
        return "configuracion";
    }

    @PostMapping
    public String guardarPerfil(@AuthenticationPrincipal UserDetails userDetails,
                                @ModelAttribute("usuarioPerfil") Usuario usuarioPerfil,
                                @RequestParam(value = "fotoArchivo", required = false) MultipartFile fotoArchivo,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        try {
            if (userDetails == null) {
                return "redirect:/login";
            }
            Usuario existente = usuarioService.buscarPorCorreo(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "No se pudo identificar la cuenta del usuario actual."));
            if (usuarioPerfil.getNombreCompleto() == null || usuarioPerfil.getNombreCompleto().isBlank()) {
                throw new IllegalArgumentException("El nombre completo es obligatorio.");
            }
            if (usuarioPerfil.getTelefono() == null || usuarioPerfil.getTelefono().isBlank()) {
                throw new IllegalArgumentException("El número de teléfono es obligatorio.");
            }
            usuarioPerfil.setIdUsuario(existente.getIdUsuario());
            usuarioPerfil.setCorreo(existente.getCorreo());
            usuarioPerfil.setContrasena(existente.getContrasena());
            usuarioPerfil.setRol(existente.getRol());
            usuarioPerfil.setActivo(existente.getActivo());
            if (fotoArchivo != null && !fotoArchivo.isEmpty()) {
                aplicarFoto(usuarioPerfil, fotoArchivo);
            } else {
                usuarioPerfil.setFoto(existente.getFoto());
            }
            usuarioService.editarUsuario(usuarioPerfil);
            redirectAttributes.addFlashAttribute("exito", "Perfil actualizado correctamente.");
            return "redirect:/configuracion";
        } catch (IllegalArgumentException | IllegalStateException | UnsupportedOperationException ex) {
            model.addAttribute("error", ex.getMessage());
            cargarVista(userDetails, model);
            return "configuracion";
        }
    }

    private static final Map<String, String> MIME_POR_EXTENSION = Map.of(
            "jpg", "image/jpeg",
            "jpeg", "image/jpeg",
            "png", "image/png",
            "webp", "image/webp",
            "gif", "image/gif");

    private void aplicarFoto(Usuario usuario, MultipartFile fotoArchivo) {
        long maxBytes = 3L * 1024 * 1024;
        if (fotoArchivo.getSize() > maxBytes) {
            throw new UnsupportedOperationException("La imagen no puede superar los 3 MB.");
        }
        String mime = mimeImagen(fotoArchivo);
        byte[] bytes;
        try {
            bytes = fotoArchivo.getBytes();
        } catch (Exception ex) {
            throw new UnsupportedOperationException("No se pudo leer el archivo de imagen.");
        }
        String dataUri = "data:" + mime + ";base64,"
                + Base64.getEncoder().encodeToString(bytes);
        usuario.setFoto(dataUri);
    }

    private String mimeImagen(MultipartFile fotoArchivo) {
        if (fotoArchivo.getContentType() != null
                && fotoArchivo.getContentType().startsWith("image/")) {
            return fotoArchivo.getContentType();
        }
        String nombre = fotoArchivo.getOriginalFilename();
        if (nombre != null && nombre.contains(".")) {
            String extension = nombre.substring(nombre.lastIndexOf('.') + 1).toLowerCase();
            String mime = MIME_POR_EXTENSION.get(extension);
            if (mime != null) {
                return mime;
            }
        }
        throw new UnsupportedOperationException("El archivo debe ser una imagen (JPG, PNG o WebP).");
    }

    private void cargarVista(UserDetails userDetails, Model model) {
        model.addAttribute("activePage", "configuracion");
        Usuario perfil = userDetails == null ? null
                : usuarioService.buscarPorCorreo(userDetails.getUsername()).orElse(null);
        if (perfil == null) {
            perfil = new Usuario();
        }
        model.addAttribute("usuarioPerfil", perfil);
    }
}