package org.esfe.servicios.interfaces;

import org.esfe.modelos.Paciente;

import java.util.List;
import java.util.Optional;

public interface IPacienteService {

    List<Paciente> obtenerTodos();

    Optional<Paciente> obtenerPorId(Integer idPaciente);

    Optional<Paciente> buscarPorCodigoExpediente(String codigoExpediente);

    Optional<Paciente> buscarPorDocumentoIdentidad(String documentoIdentidad);

    List<Paciente> buscarPorNombre(String nombre);

    boolean existeDocumentoIdentidad(String documentoIdentidad);

    Paciente crearPaciente(Paciente paciente);

    Paciente editarPaciente(Paciente paciente);

    Boolean eliminarPaciente(Integer idPaciente);

    Boolean tieneCitasAsociadas(Integer idPaciente);
}
