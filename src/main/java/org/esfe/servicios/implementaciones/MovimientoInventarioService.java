package org.esfe.servicios.implementaciones;

import org.esfe.modelos.Medicamento;
import org.esfe.modelos.MovimientoInventario;
import org.esfe.modelos.Usuario;
import org.esfe.modelos.enums.TipoMovimiento;
import org.esfe.repositorios.IMedicamentoRepository;
import org.esfe.repositorios.IMovimientoInventarioRepository;
import org.esfe.repositorios.IUsuarioRepository;
import org.esfe.servicios.interfaces.IMovimientoInventarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MovimientoInventarioService implements IMovimientoInventarioService {

    private final IMovimientoInventarioRepository movimientoInventarioRepository;
    private final IMedicamentoRepository medicamentoRepository;
    private final IUsuarioRepository usuarioRepository;

    public MovimientoInventarioService(IMovimientoInventarioRepository movimientoInventarioRepository,
            IMedicamentoRepository medicamentoRepository,
            IUsuarioRepository usuarioRepository) {
        this.movimientoInventarioRepository = movimientoInventarioRepository;
        this.medicamentoRepository = medicamentoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoInventario> obtenerTodos() {
        return movimientoInventarioRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MovimientoInventario> obtenerPorId(Integer idMovimiento) {
        return movimientoInventarioRepository.findById(idMovimiento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoInventario> obtenerPorMedicamento(Integer idMedicamento) {
        return movimientoInventarioRepository.findByMedicamentoIdMedicamento(idMedicamento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoInventario> obtenerPorUsuario(Integer idUsuario) {
        return movimientoInventarioRepository.findByUsuarioIdUsuario(idUsuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoInventario> obtenerPorTipoMovimiento(TipoMovimiento tipoMovimiento) {
        return movimientoInventarioRepository.findByTipoMovimiento(tipoMovimiento);
    }

    @Override
    @Transactional
    public MovimientoInventario registrarMovimiento(MovimientoInventario movimiento) {
        if (movimiento == null || movimiento.getMedicamento() == null || movimiento.getUsuario() == null) {
            throw new IllegalArgumentException("El movimiento debe incluir el medicamento y el usuario responsable.");
        }
        if (movimiento.getIdMovimiento() != null) {
            throw new IllegalArgumentException("Para registrar un movimiento no debe enviarse el identificador.");
        }
        if (movimiento.getTipoMovimiento() == null) {
            throw new IllegalArgumentException("El tipo de movimiento es obligatorio.");
        }
        if (movimiento.getCantidad() == null || movimiento.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad del movimiento debe ser mayor a cero.");
        }

        Medicamento medicamento = medicamentoRepository.findById(movimiento.getMedicamento().getIdMedicamento())
                .orElseThrow(() -> new IllegalArgumentException("Medicamento no encontrado con id: "
                        + movimiento.getMedicamento().getIdMedicamento()));
        Usuario usuario = usuarioRepository.findById(movimiento.getUsuario().getIdUsuario())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id: "
                        + movimiento.getUsuario().getIdUsuario()));

        aplicarMovimiento(medicamento, movimiento);
        medicamentoRepository.save(medicamento);

        movimiento.setMedicamento(medicamento);
        movimiento.setUsuario(usuario);
        if (movimiento.getFechaMovimiento() == null) {
            movimiento.setFechaMovimiento(LocalDateTime.now());
        }
        return movimientoInventarioRepository.save(movimiento);
    }

    @Override
    @Transactional
    public void eliminar(Integer idMovimiento) {
        if (!movimientoInventarioRepository.existsById(idMovimiento)) {
            throw new IllegalArgumentException("Movimiento no encontrado con id: " + idMovimiento);
        }
        movimientoInventarioRepository.deleteById(idMovimiento);
    }

    private void aplicarMovimiento(Medicamento medicamento, MovimientoInventario movimiento) {
        int stockDisponible = medicamento.getStockDisponible() == null ? 0 : medicamento.getStockDisponible();
        int cantidad = movimiento.getCantidad();

        switch (movimiento.getTipoMovimiento()) {
            case ENTRADA -> medicamento.actualizarStock(cantidad);
            case SALIDA -> {
                if (stockDisponible < cantidad) {
                    throw new IllegalStateException(
                            "Stock insuficiente para registrar la salida. Disponible: " + stockDisponible);
                }
                medicamento.actualizarStock(-cantidad);
            }
            case AJUSTE -> medicamento.setStockDisponible(cantidad);
        }
    }
}
