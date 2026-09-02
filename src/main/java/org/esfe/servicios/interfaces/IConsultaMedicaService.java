package org.esfe.servicios.interfaces;

import org.esfe.modelos.ConsultaMedica;

import java.util.List;
import java.util.Optional;

public interface IConsultaMedicaService {

    List<ConsultaMedica> obtenerTodos();

    Optional<ConsultaMedica> obtenerPorId(Integer idConsulta);

    Optional<ConsultaMedica> buscarPorCita(Integer idCita);

    List<ConsultaMedica> buscarPorMedico(Integer idMedico);

    List<ConsultaMedica> buscarPorPaciente(Integer idPaciente);

    List<ConsultaMedica> buscarPorMedicoYBusqueda(Integer idMedico, String busqueda);

    List<ConsultaMedica> buscarPorBusqueda(String busqueda);

    ConsultaMedica registrarConsulta(ConsultaMedica consulta);

    ConsultaMedica guardar(ConsultaMedica consulta);

    void eliminar(Integer idConsulta);
}
