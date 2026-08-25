package org.esfe.servicios.interfaces;

import org.esfe.modelos.Medicamento;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IMedicamentoService {

    List<Medicamento> obtenerTodos();

    Optional<Medicamento> obtenerPorId(Integer idMedicamento);

    Optional<Medicamento> buscarPorNombreComercial(String nombreComercial);

    List<Medicamento> buscarPorNombreComercialConteniendo(String nombreComercial);

    List<Medicamento> obtenerPorCategoria(Integer idCategoria);

    List<Medicamento> obtenerVencidosAntesDe(LocalDate fecha);

    List<Medicamento> obtenerConStockBajo(Integer cantidad);

    Medicamento crearMedicamento(Medicamento medicamento);

    Medicamento editarMedicamento(Medicamento medicamento);

    Boolean eliminarMedicamento(Integer idMedicamento);

    Boolean actualizarStock(Integer idMedicamento, Integer cantidad);
}
