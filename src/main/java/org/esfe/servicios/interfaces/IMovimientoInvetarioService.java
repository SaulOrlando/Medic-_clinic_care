package org.esfe.servicios.interfaces;

import org.esfe.modelos.MovimientoInventario;
import org.esfe.modelos.enums.TipoMovimiento;

import java.util.List;
import java.util.Optional;

public interface IMovimientoInventarioService {

    List<MovimientoInventario> obtenerTodos();

    Optional<MovimientoInventario> obtenerPorId(Integer idMovimiento);

    List<MovimientoInventario> obtenerPorMedicamento(Integer idMedicamento);

    List<MovimientoInventario> obtenerPorUsuario(Integer idUsuario);

    List<MovimientoInventario> obtenerPorTipoMovimiento(TipoMovimiento tipoMovimiento);

    MovimientoInventario registrarMovimiento(MovimientoInventario movimiento);

    void eliminar(Integer idMovimiento);
}
