package org.esfe.servicios.interfaces;

import org.esfe.modelos.HistorialCita;

import java.util.List;
import java.util.Optional;

public interface IHistorialCitaService {

    List<HistorialCita> obtenerTodos();

    Optional<HistorialCita> obtenerPorId(Integer idHistorial);

    List<HistorialCita> buscarPorCita(Integer idCita);

    List<HistorialCita> buscarPorUsuario(Integer idUsuario);

    HistorialCita guardar(HistorialCita historialCita);
}
