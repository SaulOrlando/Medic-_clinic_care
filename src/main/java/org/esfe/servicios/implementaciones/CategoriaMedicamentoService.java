package org.esfe.servicios.implementaciones;

import org.esfe.modelos.CategoriaMedicamento;
import org.esfe.repositorios.ICategoriaMedicamentoRepository;
import org.esfe.repositorios.IMedicamentoRepository;
import org.esfe.servicios.interfaces.ICategoriaMedicamentoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CategoriaMedicamentoService implements ICategoriaMedicamentoService {

    private final ICategoriaMedicamentoRepository categoriaMedicamentoRepository;
    private final IMedicamentoRepository medicamentoRepository;

    public CategoriaMedicamentoService(ICategoriaMedicamentoRepository categoriaMedicamentoRepository,
            IMedicamentoRepository medicamentoRepository) {
        this.categoriaMedicamentoRepository = categoriaMedicamentoRepository;
        this.medicamentoRepository = medicamentoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaMedicamento> obtenerTodos() {
        return categoriaMedicamentoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CategoriaMedicamento> obtenerPorId(Integer idCategoria) {
        return categoriaMedicamentoRepository.findById(idCategoria);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CategoriaMedicamento> buscarPorNombre(String nombre) {
        return categoriaMedicamentoRepository.findByNombre(nombre);
    }

    @Override
    @Transactional
    public CategoriaMedicamento crearCategoria(CategoriaMedicamento categoria) {
        if (categoria == null || categoria.getNombre() == null || categoria.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio.");
        }
        if (categoria.getIdCategoria() != null) {
            throw new IllegalArgumentException("Para crear una categoría no debe enviarse el identificador.");
        }
        if (categoriaMedicamentoRepository.existsByNombre(categoria.getNombre())) {
            throw new IllegalStateException("Ya existe una categoría con ese nombre.");
        }
        categoria.crearCategoria();
        return categoriaMedicamentoRepository.save(categoria);
    }

    @Override
    @Transactional
    public CategoriaMedicamento editarCategoria(CategoriaMedicamento categoria) {
        if (categoria == null || categoria.getIdCategoria() == null) {
            throw new IllegalArgumentException("La categoría a editar debe incluir su identificador.");
        }
        if (!categoriaMedicamentoRepository.existsById(categoria.getIdCategoria())) {
            throw new IllegalArgumentException(
                    "Categoría no encontrada con id: " + categoria.getIdCategoria());
        }
        categoriaMedicamentoRepository.findByNombre(categoria.getNombre())
                .filter(otro -> !Objects.equals(otro.getIdCategoria(), categoria.getIdCategoria()))
                .ifPresent(otro -> {
                    throw new IllegalStateException("Ya existe otra categoría con ese nombre.");
                });
        categoria.editarCategoria();
        return categoriaMedicamentoRepository.save(categoria);
    }

    @Override
    @Transactional
    public Boolean eliminarCategoria(Integer idCategoria) {
        if (idCategoria == null || !categoriaMedicamentoRepository.existsById(idCategoria)) {
            return Boolean.FALSE;
        }
        if (tieneMedicamentosAsociados(idCategoria)) {
            return Boolean.FALSE;
        }
        categoriaMedicamentoRepository.deleteById(idCategoria);
        return Boolean.TRUE;
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean tieneMedicamentosAsociados(Integer idCategoria) {
        if (idCategoria == null) {
            return Boolean.FALSE;
        }
        return medicamentoRepository.existsByCategoriaIdCategoria(idCategoria);
    }
}
