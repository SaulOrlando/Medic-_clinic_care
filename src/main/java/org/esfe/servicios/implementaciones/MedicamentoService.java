package org.esfe.servicios.implementaciones;

import org.esfe.modelos.CategoriaMedicamento;
import org.esfe.modelos.Medicamento;
import org.esfe.repositorios.ICategoriaMedicamentoRepository;
import org.esfe.repositorios.IMedicamentoRepository;
import org.esfe.repositorios.IMovimientoInventarioRepository;
import org.esfe.repositorios.IRecetaDetalleRepository;
import org.esfe.servicios.interfaces.IMedicamentoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class MedicamentoService implements IMedicamentoService {

    private final IMedicamentoRepository medicamentoRepository;
    private final ICategoriaMedicamentoRepository categoriaMedicamentoRepository;
    private final IRecetaDetalleRepository recetaDetalleRepository;
    private final IMovimientoInventarioRepository movimientoInventarioRepository;

    public MedicamentoService(IMedicamentoRepository medicamentoRepository,
            ICategoriaMedicamentoRepository categoriaMedicamentoRepository,
            IRecetaDetalleRepository recetaDetalleRepository,
            IMovimientoInventarioRepository movimientoInventarioRepository) {
        this.medicamentoRepository = medicamentoRepository;
        this.categoriaMedicamentoRepository = categoriaMedicamentoRepository;
        this.recetaDetalleRepository = recetaDetalleRepository;
        this.movimientoInventarioRepository = movimientoInventarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Medicamento> obtenerTodos() {
        return medicamentoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Medicamento> obtenerPorId(Integer idMedicamento) {
        return medicamentoRepository.findById(idMedicamento);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Medicamento> buscarPorNombreComercial(String nombreComercial) {
        return medicamentoRepository.findByNombreComercial(nombreComercial);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Medicamento> buscarPorNombreComercialConteniendo(String nombreComercial) {
        return medicamentoRepository.findByNombreComercialContainingIgnoreCase(nombreComercial);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Medicamento> obtenerPorCategoria(Integer idCategoria) {
        return medicamentoRepository.findByCategoriaIdCategoria(idCategoria);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Medicamento> obtenerVencidosAntesDe(LocalDate fecha) {
        return medicamentoRepository.findByFechaVencimientoBefore(fecha);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Medicamento> obtenerConStockBajo(Integer cantidad) {
        return medicamentoRepository.findStockBajo(cantidad);
    }

    @Override
    @Transactional
    public Medicamento crearMedicamento(Medicamento medicamento) {
        if (medicamento == null || medicamento.getNombreComercial() == null
                || medicamento.getNombreComercial().isBlank()) {
            throw new IllegalArgumentException("El nombre comercial del medicamento es obligatorio.");
        }
        if (medicamento.getIdMedicamento() != null) {
            throw new IllegalArgumentException("Para crear un medicamento no debe enviarse el identificador.");
        }
        if (medicamento.getCategoria() == null || medicamento.getCategoria().getIdCategoria() == null) {
            throw new IllegalArgumentException("El medicamento debe estar asociado a una categoría.");
        }
        if (!categoriaMedicamentoRepository.existsById(medicamento.getCategoria().getIdCategoria())) {
            throw new IllegalArgumentException(
                    "Categoría no encontrada con id: " + medicamento.getCategoria().getIdCategoria());
        }
        if (medicamentoRepository.findByNombreComercial(medicamento.getNombreComercial()).isPresent()) {
            throw new IllegalStateException("Ya existe un medicamento con ese nombre comercial.");
        }
        if (medicamento.getFechaVencimiento() == null || !medicamento.validarCaducidad()) {
            throw new IllegalArgumentException("La fecha de vencimiento debe ser posterior a la fecha actual.");
        }
        if (medicamento.getStockDisponible() == null) {
            medicamento.setStockDisponible(Objects.requireNonNullElse(medicamento.getStockInicial(), 0));
        }
        return medicamentoRepository.save(medicamento);
    }

    @Override
    @Transactional
    public Medicamento editarMedicamento(Medicamento medicamento) {
        if (medicamento == null || medicamento.getIdMedicamento() == null) {
            throw new IllegalArgumentException("El medicamento a editar debe incluir su identificador.");
        }
        if (!medicamentoRepository.existsById(medicamento.getIdMedicamento())) {
            throw new IllegalArgumentException(
                    "Medicamento no encontrado con id: " + medicamento.getIdMedicamento());
        }
        if (medicamento.getCategoria() != null && medicamento.getCategoria().getIdCategoria() != null
                && !categoriaMedicamentoRepository.existsById(medicamento.getCategoria().getIdCategoria())) {
            throw new IllegalArgumentException(
                    "Categoría no encontrada con id: " + medicamento.getCategoria().getIdCategoria());
        }
        medicamentoRepository.findByNombreComercial(medicamento.getNombreComercial())
                .filter(otro -> !Objects.equals(otro.getIdMedicamento(), medicamento.getIdMedicamento()))
                .ifPresent(otro -> {
                    throw new IllegalStateException("Ya existe otro medicamento con ese nombre comercial.");
                });
        return medicamentoRepository.save(medicamento);
    }

    @Override
    @Transactional
    public Boolean eliminarMedicamento(Integer idMedicamento) {
        if (idMedicamento == null || !medicamentoRepository.existsById(idMedicamento)) {
            return Boolean.FALSE;
        }
        if (!recetaDetalleRepository.findByMedicamentoIdMedicamento(idMedicamento).isEmpty()
                || !movimientoInventarioRepository.findByMedicamentoIdMedicamento(idMedicamento).isEmpty()) {
            return Boolean.FALSE;
        }
        medicamentoRepository.deleteById(idMedicamento);
        return Boolean.TRUE;
    }

    @Override
    @Transactional
    public Boolean actualizarStock(Integer idMedicamento, Integer cantidad) {
        if (idMedicamento == null || cantidad == null) {
            return Boolean.FALSE;
        }
        Optional<Medicamento> medicamentoOpt = medicamentoRepository.findById(idMedicamento);
        if (medicamentoOpt.isEmpty()) {
            return Boolean.FALSE;
        }
        Medicamento medicamento = medicamentoOpt.get();
        int stockDisponible = Objects.requireNonNullElse(medicamento.getStockDisponible(), 0);
        if (cantidad < 0 && stockDisponible < Math.abs(cantidad)) {
            return Boolean.FALSE;
        }
        medicamento.actualizarStock(cantidad);
        medicamentoRepository.save(medicamento);
        return Boolean.TRUE;
    }
}
