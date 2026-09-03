package org.esfe.modelos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categorias_medicamentos")
public class CategoriaMedicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Integer idCategoria;

    @NotBlank
    @Size(max = 100)
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Size(max = 1000)
    @Column(name = "descripcion", columnDefinition = "nvarchar(MAX)")
    private String descripcion;

    @Column(name = "activa", nullable = false, columnDefinition = "bit default 1")
    private Boolean activa = Boolean.TRUE;

    @OneToMany(mappedBy = "categoria")
    private List<Medicamento> medicamentos = new ArrayList<>();

    public void crearCategoria() {
        if (activa == null) {
            activa = Boolean.TRUE;
        }
    }

    public void editarCategoria() {
    }

    public Boolean eliminarCategoria() {
        return !tieneMedicamentosAsociados();
    }

    public Boolean tieneMedicamentosAsociados() {
        return medicamentos != null && !medicamentos.isEmpty();
    }

    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getActiva() {
        return activa;
    }

    public void setActiva(Boolean activa) {
        this.activa = activa;
    }

    public List<Medicamento> getMedicamentos() {
        return medicamentos;
    }

    public void setMedicamentos(List<Medicamento> medicamentos) {
        this.medicamentos = medicamentos;
    }
}
