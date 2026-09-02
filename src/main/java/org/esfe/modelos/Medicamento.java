package org.esfe.modelos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import jakarta.persistence.Transient;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medicamentos")
public class Medicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_medicamento")
    private Integer idMedicamento;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_categoria", nullable = false)
    private CategoriaMedicamento categoria;

    @NotBlank
    @Size(max = 150)
    @Column(name = "nombre_comercial", nullable = false, length = 150)
    private String nombreComercial;

    @NotBlank
    @Size(max = 150)
    @Column(name = "nombre_generico", nullable = false, length = 150)
    private String nombreGenerico;

    @NotBlank
    @Size(max = 50)
    @Column(name = "presentacion", nullable = false, length = 50)
    private String presentacion;

    @NotBlank
    @Size(max = 30)
    @Column(name = "unidad_medida", nullable = false, length = 30)
    private String unidadMedida;

    @NotBlank
    @Size(max = 50)
    @Column(name = "concentracion", nullable = false, length = 50)
    private String concentracion;

    @NotNull
    @PositiveOrZero
    @Column(name = "stock_inicial", nullable = false)
    private Integer stockInicial;

    @NotNull
    @PositiveOrZero
    @Column(name = "stock_disponible", nullable = false)
    private Integer stockDisponible;

    @NotNull
    @Future
    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @OneToMany(mappedBy = "medicamento")
    private List<RecetaDetalle> recetasDetalle = new ArrayList<>();

    @OneToMany(mappedBy = "medicamento")
    private List<MovimientoInventario> movimientosInventario = new ArrayList<>();

    @Transient
    private Integer categoriaId;

    public Boolean validarCaducidad() {
        return fechaVencimiento != null && fechaVencimiento.isAfter(LocalDate.now());
    }

    public void actualizarStock(Integer cantidad) {
        if (cantidad == null) {
            return;
        }

        int stockActual = stockDisponible == null ? 0 : stockDisponible;
        this.stockDisponible = Math.max(0, stockActual + cantidad);
    }

    public Integer getIdMedicamento() {
        return idMedicamento;
    }

    public void setIdMedicamento(Integer idMedicamento) {
        this.idMedicamento = idMedicamento;
    }

    public CategoriaMedicamento getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaMedicamento categoria) {
        this.categoria = categoria;
    }

    public String getNombreComercial() {
        return nombreComercial;
    }

    public void setNombreComercial(String nombreComercial) {
        this.nombreComercial = nombreComercial;
    }

    public String getNombreGenerico() {
        return nombreGenerico;
    }

    public void setNombreGenerico(String nombreGenerico) {
        this.nombreGenerico = nombreGenerico;
    }

    public String getPresentacion() {
        return presentacion;
    }

    public void setPresentacion(String presentacion) {
        this.presentacion = presentacion;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public String getConcentracion() {
        return concentracion;
    }

    public void setConcentracion(String concentracion) {
        this.concentracion = concentracion;
    }

    public Integer getStockInicial() {
        return stockInicial;
    }

    public void setStockInicial(Integer stockInicial) {
        this.stockInicial = stockInicial;
    }

    public Integer getStockDisponible() {
        return stockDisponible;
    }

    public void setStockDisponible(Integer stockDisponible) {
        this.stockDisponible = stockDisponible;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public List<RecetaDetalle> getRecetasDetalle() {
        return recetasDetalle;
    }

    public void setRecetasDetalle(List<RecetaDetalle> recetasDetalle) {
        this.recetasDetalle = recetasDetalle;
    }

    public List<MovimientoInventario> getMovimientosInventario() {
        return movimientosInventario;
    }

    public void setMovimientosInventario(List<MovimientoInventario> movimientosInventario) {
        this.movimientosInventario = movimientosInventario;
    }

    public Integer getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Integer categoriaId) {
        this.categoriaId = categoriaId;
    }
  }
