package org.esfe.servicios.implementaciones;

import org.esfe.modelos.Usuario;
import org.esfe.modelos.enums.RolUsuario;
import org.esfe.repositorios.IUsuarioRepository;
import org.esfe.servicios.interfaces.IUsuarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UsuarioService implements IUsuarioService {

    private final IUsuarioRepository usuarioRepository;

    public UsuarioService(IUsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> obtenerPorId(Integer idUsuario) {
        return usuarioRepository.findById(idUsuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> buscarPorRol(RolUsuario rol) {
        if (rol == null) {
            throw new IllegalArgumentException("El rol es obligatorio para la búsqueda.");
        }
        return usuarioRepository.findByRol(rol);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> buscarPorFiltro(String busqueda, RolUsuario rol, Boolean activo) {
        List<Usuario> resultados;
        if (rol == null && activo == null) {
            resultados = usuarioRepository.findAll();
        } else if (rol == null) {
            resultados = usuarioRepository.findByActivo(activo);
        } else if (activo == null) {
            resultados = usuarioRepository.findByRol(rol);
        } else {
            resultados = usuarioRepository.findByRolAndActivo(rol, activo);
        }

        if (busqueda == null || busqueda.isBlank()) {
            return resultados;
        }

        String termino = busqueda.trim().toLowerCase();
        return resultados.stream()
                .filter(u -> coinciden(u, termino))
                .toList();
    }

    private boolean coinciden(Usuario usuario, String termino) {
        return termino == null || termino.isBlank()
                || usuario.getNombreCompleto() != null && usuario.getNombreCompleto().toLowerCase().contains(termino)
                || usuario.getCorreo() != null && usuario.getCorreo().toLowerCase().contains(termino)
                || usuario.getTelefono() != null && usuario.getTelefono().toLowerCase().contains(termino);
    }

    @Override
    @Transactional
    public Usuario restablecerContrasena(Integer idUsuario, String nuevaContrasena) {
        if (idUsuario == null) {
            throw new IllegalArgumentException("El identificador del usuario es obligatorio.");
        }
        if (nuevaContrasena == null || nuevaContrasena.length() < 8) {
            throw new IllegalArgumentException("La nueva contraseña debe tener al menos 8 caracteres.");
        }
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Usuario no encontrado con id: " + idUsuario));
        usuario.setContrasena(nuevaContrasena);
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public Usuario cambiarEstado(Integer idUsuario) {
        if (idUsuario == null) {
            throw new IllegalArgumentException("El identificador del usuario es obligatorio.");
        }
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Usuario no encontrado con id: " + idUsuario));
        if (Boolean.TRUE.equals(usuario.getActivo())) {
            usuario.desactivar();
        } else {
            usuario.activar();
        }
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> autenticar(String correo, String contrasena) {
        if (correo == null || correo.isBlank() || contrasena == null || contrasena.isBlank()) {
            throw new IllegalArgumentException("El correo y la contraseña son obligatorios.");
        }
        return usuarioRepository.findByCorreoAndContrasena(correo, contrasena);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeCorreo(String correo) {
        if (correo == null || correo.isBlank()) {
            return false;
        }
        return usuarioRepository.existsByCorreo(correo);
    }

    @Override
    @Transactional
    public Usuario crearUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Los datos del usuario son obligatorios.");
        }
        if (usuario.getIdUsuario() != null) {
            throw new IllegalArgumentException("Para crear un usuario no debe enviarse el identificador.");
        }
        validarDatosObligatorios(usuario);
        if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
            throw new IllegalStateException("Ya existe un usuario registrado con ese correo.");
        }
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public Usuario editarUsuario(Usuario usuario) {
        if (usuario == null || usuario.getIdUsuario() == null) {
            throw new IllegalArgumentException("El usuario a editar debe incluir su identificador.");
        }
        if (!usuarioRepository.existsById(usuario.getIdUsuario())) {
            throw new IllegalArgumentException(
                    "Usuario no encontrado con id: " + usuario.getIdUsuario());
        }
        validarDatosObligatorios(usuario);
        usuarioRepository.findByCorreo(usuario.getCorreo())
                .filter(otro -> !Objects.equals(otro.getIdUsuario(), usuario.getIdUsuario()))
                .ifPresent(otro -> {
                    throw new IllegalStateException("Ya existe otro usuario registrado con ese correo.");
                });
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public Boolean eliminarUsuario(Integer idUsuario) {
        if (idUsuario == null || !usuarioRepository.existsById(idUsuario)) {
            return Boolean.FALSE;
        }
        usuarioRepository.deleteById(idUsuario);
        return Boolean.TRUE;
    }

    private void validarDatosObligatorios(Usuario usuario) {
        if (usuario.getCorreo() == null || usuario.getCorreo().isBlank()) {
            throw new IllegalArgumentException("El correo del usuario es obligatorio.");
        }
        if (usuario.getContrasena() == null || usuario.getContrasena().isBlank()) {
            throw new IllegalArgumentException("La contraseña del usuario es obligatoria.");
        }
        if (usuario.getContrasena().length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres.");
        }
        if (usuario.getRol() == null) {
            throw new IllegalArgumentException("El rol del usuario es obligatorio.");
        }
        if (usuario.getNombreCompleto() == null || usuario.getNombreCompleto().isBlank()) {
            throw new IllegalArgumentException("El nombre completo del usuario es obligatorio.");
        }
        if (usuario.getTelefono() == null || usuario.getTelefono().isBlank()) {
            throw new IllegalArgumentException("El teléfono del usuario es obligatorio.");
        }
    }
}
