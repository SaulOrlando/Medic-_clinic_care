package org.esfe.repositorios;

import org.esfe.modelos.Usuario;
import org.esfe.modelos.enums.RolUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByCorreo(String correo);

    Optional<Usuario> findByCorreoAndContrasena(String correo, String contrasena);

    boolean existsByCorreo(String correo);

    List<Usuario> findByRol(RolUsuario rol);
}
