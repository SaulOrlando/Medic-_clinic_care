package org.esfe.servicios.interfaces;

import org.esfe.modelos.CategoriaMedicamento;

import java.util.List;
import java.util.Optional;

public interface ICategoriaMedicamentoService {

    List<CategoriaMedicamento> obtenerTodos();

    Optional<CategoriaMedicamento> obtenerPorId(Integer idCategoria);

    Optional<CategoriaMedicamento> buscarPorNombre(String nombre);

    CategoriaMedicamento crearCategoria(CategoriaMedicamento categoria);

    CategoriaMedicamento editarCategoria(CategoriaMedicamento categoria);

    Boolean eliminarCategoria(Integer idCategoria);

    Boolean tieneMedicamentosAsociados(Integer idCategoria);
}
