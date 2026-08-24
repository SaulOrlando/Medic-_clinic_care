package org.esfe.servicios.interfaces;

import org.esfe.modelos.Usuario;
import org.esfe.modelos.enums.RolUsuario;

import java.util.List;
import java.util.Optional;

public interface IUsuarioService {

    List<Usuario> obtenerTodos();

    Optional<Usuario> obtenerPorId(Integer idUsuario);

    Optional<Usuario> buscarPorCorreo(String correo);

    List<Usuario> buscarPorRol(RolUsuario rol);

    Optional<Usuario> autenticar(String correo, String contrasena);

    boolean existeCorreo(String correo);

    Usuario crearUsuario(Usuario usuario);

    Usuario editarUsuario(Usuario usuario);

    Boolean eliminarUsuario(Integer idUsuario);
}
