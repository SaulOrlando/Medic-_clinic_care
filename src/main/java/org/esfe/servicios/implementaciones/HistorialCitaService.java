package org.esfe.servicios.implementaciones;

import org.esfe.modelos.HistorialCita;
import org.esfe.repositorios.IHistorialCitaRepository;
import org.esfe.servicios.interfaces.IHistorialCitaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class HistorialCitaService implements IHistorialCitaService {

    private final IHistorialCitaRepository historialCitaRepository;

    public HistorialCitaService(IHistorialCitaRepository historialCitaRepository) {
        this.historialCitaRepository = historialCitaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistorialCita> obtenerTodos() {
        return historialCitaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<HistorialCita> obtenerPorId(Integer idHistorial) {
        return historialCitaRepository.findById(idHistorial);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistorialCita> buscarPorCita(Integer idCita) {
        return historialCitaRepository.findByCitaIdCita(idCita);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistorialCita> buscarPorUsuario(Integer idUsuario) {
        return historialCitaRepository.findByUsuarioIdUsuario(idUsuario);
    }

    @Override
    @Transactional
    public HistorialCita guardar(HistorialCita historialCita) {
        if (historialCita == null || historialCita.getCita() == null || historialCita.getUsuario() == null) {
            throw new IllegalArgumentException("El registro de historial requiere la cita y el usuario.");
        }
        if (historialCita.getEstadoAnterior() == null || historialCita.getEstadoNuevo() == null) {
            throw new IllegalArgumentException("El registro de historial requiere el estado anterior y el nuevo.");
        }
        return historialCitaRepository.save(historialCita);
    }
}
