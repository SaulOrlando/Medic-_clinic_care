package org.esfe.modelos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "consultas_medicas")
public class ConsultaMedica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_consulta")
    private Integer idConsulta;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cita", nullable = false, unique = true)
    private Cita cita;

    @NotNull
    @Column(name = "fecha_consulta", nullable = false)
    private LocalDateTime fechaConsulta = LocalDateTime.now();

    @NotBlank
    @Size(max = 1000)
    @Column(name = "motivo_consulta", nullable = false, columnDefinition = "nvarchar(MAX)")
    private String motivoConsulta;

    @NotBlank
    @Size(max = 1000)
    @Column(name = "sintomatologia", nullable = false, columnDefinition = "nvarchar(MAX)")
    private String sintomatologia;

    @NotBlank
    @Size(max = 1000)
    @Column(name = "diagnostico", nullable = false, columnDefinition = "nvarchar(MAX)")
    private String diagnostico;

    @Size(max = 2000)
    @Column(name = "plan_tratamiento", columnDefinition = "nvarchar(MAX)")
    private String planTratamiento;

    @OneToMany(mappedBy = "consulta")
    private List<RecetaDetalle> recetasDetalle = new ArrayList<>();

    public void registrarConsulta() {
        this.fechaConsulta = LocalDateTime.now();
    }

    public Integer getIdConsulta() {
        return idConsulta;
    }

    public void setIdConsulta(Integer idConsulta) {
        this.idConsulta = idConsulta;
    }

    public Cita getCita() {
        return cita;
    }

    public void setCita(Cita cita) {
        this.cita = cita;
    }

    public LocalDateTime getFechaConsulta() {
        return fechaConsulta;
    }

    public void setFechaConsulta(LocalDateTime fechaConsulta) {
        this.fechaConsulta = fechaConsulta;
    }

    public String getMotivoConsulta() {
        return motivoConsulta;
    }

    public void setMotivoConsulta(String motivoConsulta) {
        this.motivoConsulta = motivoConsulta;
    }

    public String getSintomatologia() {
        return sintomatologia;
    }

    public void setSintomatologia(String sintomatologia) {
        this.sintomatologia = sintomatologia;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getPlanTratamiento() {
        return planTratamiento;
    }

    public void setPlanTratamiento(String planTratamiento) {
        this.planTratamiento = planTratamiento;
    }

    public List<RecetaDetalle> getRecetasDetalle() {
        return recetasDetalle;
    }

    public void setRecetasDetalle(List<RecetaDetalle> recetasDetalle) {
        this.recetasDetalle = recetasDetalle;
    }
}
