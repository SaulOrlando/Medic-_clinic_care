package org.esfe.servicios.interfaces;

import org.esfe.modelos.Medico;

import java.util.List;
import java.util.Optional;

public interface IMedicoService {

    List<Medico> obtenerTodos();

    Optional<Medico> obtenerPorId(Integer idMedico);

    Optional<Medico> buscarPorNumeroLicencia(String numeroLicencia);

    Optional<Medico> buscarPorUsuario(Integer idUsuario);

    List<Medico> obtenerPorEspecialidad(String especialidad);

    List<Medico> obtenerPorDisponible(Boolean disponible);

    Medico crearMedico(Medico medico);

    Medico editarMedico(Medico medico);

    Medico cambiarDisponibilidad(Integer idMedico, Boolean disponible);

    Boolean eliminarMedico(Integer idMedico);

    Boolean tieneCitasAsociadas(Integer idMedico);
}
