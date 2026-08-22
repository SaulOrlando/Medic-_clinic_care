package org.esfe.servicios.implementaciones;

import org.esfe.modelos.Cita;
import org.esfe.modelos.ConsultaMedica;
import org.esfe.modelos.enums.EstadoCita;
import org.esfe.repositorios.ICitaRepository;
import org.esfe.repositorios.IConsultaMedicaRepository;
import org.esfe.servicios.interfaces.IConsultaMedicaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ConsultaMedicaService implements IConsultaMedicaService {

    private final IConsultaMedicaRepository consultaMedicaRepository;
    private final ICitaRepository citaRepository;

    public ConsultaMedicaService(IConsultaMedicaRepository consultaMedicaRepository,
            ICitaRepository citaRepository) {
        this.consultaMedicaRepository = consultaMedicaRepository;
        this.citaRepository = citaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConsultaMedica> obtenerTodos() {
        return consultaMedicaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ConsultaMedica> obtenerPorId(Integer idConsulta) {
        return consultaMedicaRepository.findById(idConsulta);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ConsultaMedica> buscarPorCita(Integer idCita) {
        return consultaMedicaRepository.findByCitaIdCita(idCita);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConsultaMedica> buscarPorMedico(Integer idMedico) {
        return consultaMedicaRepository.findByMedico(idMedico);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConsultaMedica> buscarPorPaciente(Integer idPaciente) {
        return consultaMedicaRepository.findByPaciente(idPaciente);
    }

    @Override
    @Transactional
    public ConsultaMedica registrarConsulta(ConsultaMedica consulta) {
        if (consulta == null || consulta.getCita() == null) {
            throw new IllegalArgumentException("La consulta debe estar asociada a una cita.");
        }
        Cita cita = citaRepository.findById(consulta.getCita().getIdCita())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Cita no encontrada con id: " + consulta.getCita().getIdCita()));
        if (consulta.getIdConsulta() == null && consultaMedicaRepository.existsByCitaIdCita(cita.getIdCita())) {
            throw new IllegalStateException("La cita ya tiene una consulta médica registrada.");
        }
        consulta.setCita(cita);
        consulta.registrarConsulta();
        ConsultaMedica guardada = consultaMedicaRepository.save(consulta);
        cita.setEstado(EstadoCita.ATENDIDA);
        citaRepository.save(cita);
        return guardada;
    }

    @Override
    @Transactional
    public ConsultaMedica guardar(ConsultaMedica consulta) {
        return consultaMedicaRepository.save(consulta);
    }

    @Override
    @Transactional
    public void eliminar(Integer idConsulta) {
        if (!consultaMedicaRepository.existsById(idConsulta)) {
            throw new IllegalArgumentException("Consulta no encontrada con id: " + idConsulta);
        }
        consultaMedicaRepository.deleteById(idConsulta);
    }
}
