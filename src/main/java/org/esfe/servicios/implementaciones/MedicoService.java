package org.esfe.servicios.implementaciones;

import org.esfe.modelos.Medico;
import org.esfe.repositorios.ICitaRepository;
import org.esfe.repositorios.IMedicoRepository;
import org.esfe.servicios.interfaces.IMedicoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class MedicoService implements IMedicoService {

    private final IMedicoRepository medicoRepository;
    private final ICitaRepository citaRepository;

    public MedicoService(IMedicoRepository medicoRepository, ICitaRepository citaRepository) {
        this.medicoRepository = medicoRepository;
        this.citaRepository = citaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Medico> obtenerTodos() {
        return medicoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Medico> obtenerPorId(Integer idMedico) {
        return medicoRepository.findById(idMedico);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Medico> buscarPorNumeroLicencia(String numeroLicencia) {
        return medicoRepository.findByNumeroLicencia(numeroLicencia);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Medico> buscarPorUsuario(Integer idUsuario) {
        return medicoRepository.findByUsuarioIdUsuario(idUsuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Medico> obtenerPorEspecialidad(String especialidad) {
        return medicoRepository.findByEspecialidad(especialidad);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Medico> obtenerPorDisponible(Boolean disponible) {
        return medicoRepository.findByDisponible(disponible);
    }

    @Override
    @Transactional
    public Medico crearMedico(Medico medico) {
        if (medico == null) {
            throw new IllegalArgumentException("Los datos del médico son obligatorios.");
        }
        if (medico.getIdMedico() != null) {
            throw new IllegalArgumentException("Para crear un médico no debe enviarse el identificador.");
        }
        if (medico.getUsuario() == null || medico.getUsuario().getIdUsuario() == null) {
            throw new IllegalArgumentException("El médico debe estar asociado a un usuario válido.");
        }
        if (!medico.validarLicenciaUnica()) {
            throw new IllegalArgumentException("El número de licencia es obligatorio.");
        }
        if (medicoRepository.existsByNumeroLicencia(medico.getNumeroLicencia())) {
            throw new IllegalStateException("Ya existe un médico con ese número de licencia.");
        }
        if (medicoRepository.existsByUsuarioIdUsuario(medico.getUsuario().getIdUsuario())) {
            throw new IllegalStateException("Ya existe un médico asociado a ese usuario.");
        }
        return medicoRepository.save(medico);
    }

    @Override
    @Transactional
    public Medico editarMedico(Medico medico) {
        if (medico == null || medico.getIdMedico() == null) {
            throw new IllegalArgumentException("El médico a editar debe incluir su identificador.");
        }
        if (!medicoRepository.existsById(medico.getIdMedico())) {
            throw new IllegalArgumentException("Médico no encontrado con id: " + medico.getIdMedico());
        }
        if (!medico.validarLicenciaUnica()) {
            throw new IllegalArgumentException("El número de licencia es obligatorio.");
        }
        medicoRepository.findByNumeroLicencia(medico.getNumeroLicencia())
                .filter(otro -> !Objects.equals(otro.getIdMedico(), medico.getIdMedico()))
                .ifPresent(otro -> {
                    throw new IllegalStateException("Ya existe otro médico con ese número de licencia.");
                });
        return medicoRepository.save(medico);
    }

    @Override
    @Transactional
    public Medico cambiarDisponibilidad(Integer idMedico, Boolean disponible) {
        if (disponible == null) {
            throw new IllegalArgumentException("La disponibilidad es obligatoria.");
        }
        Medico medico = medicoRepository.findById(idMedico)
                .orElseThrow(() -> new IllegalArgumentException("Médico no encontrado con id: " + idMedico));
        medico.cambiarDisponibilidad(disponible);
        return medicoRepository.save(medico);
    }

    @Override
    @Transactional
    public Boolean eliminarMedico(Integer idMedico) {
        if (idMedico == null || !medicoRepository.existsById(idMedico)) {
            return Boolean.FALSE;
        }
        if (tieneCitasAsociadas(idMedico)) {
            return Boolean.FALSE;
        }
        medicoRepository.deleteById(idMedico);
        return Boolean.TRUE;
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean tieneCitasAsociadas(Integer idMedico) {
        if (idMedico == null) {
            return Boolean.FALSE;
        }
        return citaRepository.existsByMedicoIdMedico(idMedico);
    }
}
