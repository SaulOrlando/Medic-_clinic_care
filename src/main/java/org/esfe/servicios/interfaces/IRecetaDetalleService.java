package org.esfe.servicios.interfaces;

import org.esfe.modelos.RecetaDetalle;
import org.esfe.modelos.enums.EstadoReceta;

import java.util.List;
import java.util.Optional;

public interface IRecetaDetalleService {

    List<RecetaDetalle> obtenerTodos();

    Optional<RecetaDetalle> obtenerPorId(Integer idRecetaDetalle);

    List<RecetaDetalle> buscarPorConsulta(Integer idConsulta);

    List<RecetaDetalle> buscarPorMedicamento(Integer idMedicamento);

    List<RecetaDetalle> buscarPorEstado(EstadoReceta estado);

    RecetaDetalle guardar(RecetaDetalle recetaDetalle);

    RecetaDetalle dispensar(Integer idRecetaDetalle);

    void eliminar(Integer idRecetaDetalle);
}
