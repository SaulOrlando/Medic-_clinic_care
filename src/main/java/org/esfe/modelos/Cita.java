package org.esfe.modelos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.esfe.modelos.enums.EstadoCita;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "citas")
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cita")
    private Integer idCita;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_paciente", nullable = false)
    private Paciente paciente;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_medico", nullable = false)
    private Medico medico;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario_gestor", nullable = false)
    private Usuario usuarioGestor;

    @NotNull
    @FutureOrPresent
    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @NotNull
    @Positive
    @Column(name = "duracion_minutos", nullable = false)
    private Integer duracionMinutos;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 30)
    private EstadoCita estado = EstadoCita.PROGRAMADA;

    @OneToOne(mappedBy = "cita")
    private ConsultaMedica consultaMedica;

    @OneToMany(mappedBy = "cita")
    private List<HistorialCita> historialCitas = new ArrayList<>();

    public void programarCita() {
        this.estado = EstadoCita.PROGRAMADA;
    }

    public void reagendarCita(LocalDateTime nuevaFechaHora, String motivo) {
        this.fechaHora = nuevaFechaHora;
        this.estado = EstadoCita.REAGENDADA;
    }

    public void cancelarCita(String motivo) {
        this.estado = EstadoCita.CANCELADA;
    }

    public Boolean estaDisponible(Medico medico, LocalDateTime fechaHora, Integer duracion) {
        return medico != null
                && Boolean.TRUE.equals(medico.getDisponible())
                && fechaHora != null
                && !fechaHora.isBefore(LocalDateTime.now())
                && duracion != null
                && duracion > 0;
    }

    public Integer getIdCita() {
        return idCita;
    }

    public void setIdCita(Integer idCita) {
        this.idCita = idCita;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public Usuario getUsuarioGestor() {
        return usuarioGestor;
    }

    public void setUsuarioGestor(Usuario usuarioGestor) {
        this.usuarioGestor = usuarioGestor;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public Integer getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(Integer duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    public EstadoCita getEstado() {
        return estado;
    }

    public void setEstado(EstadoCita estado) {
        this.estado = estado;
    }

    public ConsultaMedica getConsultaMedica() {
        return consultaMedica;
    }

    public void setConsultaMedica(ConsultaMedica consultaMedica) {
        this.consultaMedica = consultaMedica;
    }

    public List<HistorialCita> getHistorialCitas() {
        return historialCitas;
    }

    public void setHistorialCitas(List<HistorialCita> historialCitas) {
        this.historialCitas = historialCitas;
    }
}
