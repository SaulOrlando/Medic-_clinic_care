package org.esfe.servicios.implementaciones;

import org.esfe.modelos.Medicamento;
import org.esfe.modelos.RecetaDetalle;
import org.esfe.modelos.enums.EstadoReceta;
import org.esfe.repositorios.IMedicamentoRepository;
import org.esfe.repositorios.IRecetaDetalleRepository;
import org.esfe.servicios.interfaces.IRecetaDetalleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class RecetaDetalleService implements IRecetaDetalleService {

    private final IRecetaDetalleRepository recetaDetalleRepository;
    private final IMedicamentoRepository medicamentoRepository;

    public RecetaDetalleService(IRecetaDetalleRepository recetaDetalleRepository,
            IMedicamentoRepository medicamentoRepository) {
        this.recetaDetalleRepository = recetaDetalleRepository;
        this.medicamentoRepository = medicamentoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecetaDetalle> obtenerTodos() {
        return recetaDetalleRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RecetaDetalle> obtenerPorId(Integer idRecetaDetalle) {
        return recetaDetalleRepository.findById(idRecetaDetalle);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecetaDetalle> buscarPorConsulta(Integer idConsulta) {
        return recetaDetalleRepository.findByConsultaIdConsulta(idConsulta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecetaDetalle> buscarPorMedicamento(Integer idMedicamento) {
        return recetaDetalleRepository.findByMedicamentoIdMedicamento(idMedicamento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecetaDetalle> buscarPorEstado(EstadoReceta estado) {
        return recetaDetalleRepository.findByEstado(estado);
    }

    @Override
    @Transactional
    public RecetaDetalle guardar(RecetaDetalle recetaDetalle) {
        if (recetaDetalle == null || recetaDetalle.getConsulta() == null || recetaDetalle.getMedicamento() == null) {
            throw new IllegalArgumentException("La receta debe estar asociada a una consulta y un medicamento.");
        }
        if (recetaDetalle.getIdRecetaDetalle() == null
                && recetaDetalleRepository.existsByConsultaIdConsultaAndMedicamentoIdMedicamento(
                        recetaDetalle.getConsulta().getIdConsulta(),
                        recetaDetalle.getMedicamento().getIdMedicamento())) {
            throw new IllegalStateException("Ese medicamento ya está prescrito en la consulta.");
        }
        if (recetaDetalle.getEstado() == null) {
            recetaDetalle.setEstado(EstadoReceta.PRESCRITA);
        }
        return recetaDetalleRepository.save(recetaDetalle);
    }

    @Override
    @Transactional
    public RecetaDetalle dispensar(Integer idRecetaDetalle) {
        RecetaDetalle receta = recetaDetalleRepository.findById(idRecetaDetalle)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Receta no encontrada con id: " + idRecetaDetalle));
        if (receta.getEstado() != EstadoReceta.PRESCRITA) {
            throw new IllegalStateException("Solo se pueden dispensar recetas en estado PRESCRITA.");
        }
        Medicamento medicamento = medicamentoRepository.findById(receta.getMedicamento().getIdMedicamento())
                .orElseThrow(() -> new IllegalArgumentException("Medicamento no encontrado con id: "
                        + receta.getMedicamento().getIdMedicamento()));
        int stockDisponible = Objects.requireNonNullElse(medicamento.getStockDisponible(), 0);
        if (stockDisponible < receta.getCantidad()) {
            throw new IllegalStateException("Stock insuficiente para dispensar el medicamento solicitado.");
        }
        medicamento.actualizarStock(-receta.getCantidad());
        medicamentoRepository.save(medicamento);
        receta.setEstado(EstadoReceta.DISPENSADA);
        return recetaDetalleRepository.save(receta);
    }

    @Override
    @Transactional
    public void eliminar(Integer idRecetaDetalle) {
        if (!recetaDetalleRepository.existsById(idRecetaDetalle)) {
            throw new IllegalArgumentException("Receta no encontrada con id: " + idRecetaDetalle);
        }
        recetaDetalleRepository.deleteById(idRecetaDetalle);
    }
}
