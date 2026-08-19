package org.esfe.repositorios;

import org.esfe.modelos.MovimientoInventario;
import org.esfe.modelos.enums.TipoMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IMovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Integer> {

    List<MovimientoInventario> findByMedicamentoIdMedicamento(Integer idMedicamento);

    List<MovimientoInventario> findByUsuarioIdUsuario(Integer idUsuario);

    List<MovimientoInventario> findByTipoMovimiento(TipoMovimiento tipoMovimiento);
}
