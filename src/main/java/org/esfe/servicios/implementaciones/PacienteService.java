package org.esfe.servicios.implementaciones;

import org.esfe.modelos.Paciente;
import org.esfe.repositorios.ICitaRepository;
import org.esfe.repositorios.IPacienteRepository;
import org.esfe.servicios.interfaces.IPacienteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PacienteService implements IPacienteService {

    private final IPacienteRepository pacienteRepository;
    private final ICitaRepository citaRepository;

    public PacienteService(IPacienteRepository pacienteRepository, ICitaRepository citaRepository) {
        this.pacienteRepository = pacienteRepository;
        this.citaRepository = citaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Paciente> obtenerTodos() {
        return pacienteRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Paciente> obtenerPorId(Integer idPaciente) {
        return pacienteRepository.findById(idPaciente);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Paciente> buscarPorCodigoExpediente(String codigoExpediente) {
        if (codigoExpediente == null || codigoExpediente.isBlank()) {
            throw new IllegalArgumentException("El código de expediente es obligatorio.");
        }
        return pacienteRepository.findByCodigoExpediente(codigoExpediente);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Paciente> buscarPorDocumentoIdentidad(String documentoIdentidad) {
        if (documentoIdentidad == null || documentoIdentidad.isBlank()) {
            throw new IllegalArgumentException("El documento de identidad es obligatorio.");
        }
        return pacienteRepository.findByDocumentoIdentidad(documentoIdentidad);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Paciente> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre a buscar es obligatorio.");
        }
        return pacienteRepository.findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCase(nombre, nombre);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeDocumentoIdentidad(String documentoIdentidad) {
        if (documentoIdentidad == null || documentoIdentidad.isBlank()) {
            return false;
        }
        return pacienteRepository.existsByDocumentoIdentidad(documentoIdentidad);
    }

    @Override
    @Transactional
    public Paciente crearPaciente(Paciente paciente) {
        if (paciente == null) {
            throw new IllegalArgumentException("Los datos del paciente son obligatorios.");
        }
        if (paciente.getIdPaciente() != null) {
            throw new IllegalArgumentException("Para crear un paciente no debe enviarse el identificador.");
        }
        validarDatosObligatorios(paciente);
        if (pacienteRepository.existsByCodigoExpediente(paciente.getCodigoExpediente())) {
            throw new IllegalStateException("Ya existe un paciente con ese código de expediente.");
        }
        if (pacienteRepository.existsByDocumentoIdentidad(paciente.getDocumentoIdentidad())) {
            throw new IllegalStateException("Ya existe un paciente con ese documento de identidad.");
        }
        return pacienteRepository.save(paciente);
    }

    @Override
    @Transactional
    public Paciente editarPaciente(Paciente paciente) {
        if (paciente == null || paciente.getIdPaciente() == null) {
            throw new IllegalArgumentException("El paciente a editar debe incluir su identificador.");
        }
        if (!pacienteRepository.existsById(paciente.getIdPaciente())) {
            throw new IllegalArgumentException(
                    "Paciente no encontrado con id: " + paciente.getIdPaciente());
        }
        validarDatosObligatorios(paciente);
        validarDuplicadosAlEditar(paciente);
        return pacienteRepository.save(paciente);
    }

    @Override
    @Transactional
    public Boolean eliminarPaciente(Integer idPaciente) {
        if (idPaciente == null || !pacienteRepository.existsById(idPaciente)) {
            return Boolean.FALSE;
        }
        if (tieneCitasAsociadas(idPaciente)) {
            return Boolean.FALSE;
        }
        pacienteRepository.deleteById(idPaciente);
        return Boolean.TRUE;
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean tieneCitasAsociadas(Integer idPaciente) {
        if (idPaciente == null) {
            return Boolean.FALSE;
        }
        return !citaRepository.findByPacienteIdPaciente(idPaciente).isEmpty();
    }

    private void validarDatosObligatorios(Paciente paciente) {
        if (paciente.getCodigoExpediente() == null || paciente.getCodigoExpediente().isBlank()) {
            throw new IllegalArgumentException("El código de expediente del paciente es obligatorio.");
        }
        if (paciente.getNombres() == null || paciente.getNombres().isBlank()) {
            throw new IllegalArgumentException("Los nombres del paciente son obligatorios.");
        }
        if (paciente.getApellidos() == null || paciente.getApellidos().isBlank()) {
            throw new IllegalArgumentException("Los apellidos del paciente son obligatorios.");
        }
        if (paciente.getDocumentoIdentidad() == null || paciente.getDocumentoIdentidad().isBlank()) {
            throw new IllegalArgumentException("El documento de identidad del paciente es obligatorio.");
        }
        if (paciente.getFechaNacimiento() == null) {
            throw new IllegalArgumentException("La fecha de nacimiento del paciente es obligatoria.");
        }
        if (paciente.getTelefono() == null || paciente.getTelefono().isBlank()) {
            throw new IllegalArgumentException("El teléfono del paciente es obligatorio.");
        }
        if (paciente.getGenero() == null || paciente.getGenero().isBlank()) {
            throw new IllegalArgumentException("El género del paciente es obligatorio.");
        }
    }

    private void validarDuplicadosAlEditar(Paciente paciente) {
        pacienteRepository.findByCodigoExpediente(paciente.getCodigoExpediente())
                .filter(otro -> !Objects.equals(otro.getIdPaciente(), paciente.getIdPaciente()))
                .ifPresent(otro -> {
                    throw new IllegalStateException("Ya existe otro paciente con ese código de expediente.");
                });
        pacienteRepository.findByDocumentoIdentidad(paciente.getDocumentoIdentidad())
                .filter(otro -> !Objects.equals(otro.getIdPaciente(), paciente.getIdPaciente()))
                .ifPresent(otro -> {
                    throw new IllegalStateException("Ya existe otro paciente con ese documento de identidad.");
                });
    }
}
