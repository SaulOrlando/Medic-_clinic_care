package org.esfe.modelos;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.esfe.modelos.enums.RolUsuario;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @NotBlank
    @Email
    @Size(max = 150)
    @Column(name = "correo", nullable = false, length = 150, unique = true)
    private String correo;

    @NotBlank
    @Size(min = 8, max = 255)
    @Column(name = "contrasena", nullable = false, length = 255)
    private String contrasena;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false, length = 30)
    private RolUsuario rol;

    @NotBlank
    @Size(max = 150)
    @Column(name = "nombre_completo", nullable = false, length = 150)
    private String nombreCompleto;

    @NotBlank
    @Size(max = 20)
    @Column(name = "telefono", nullable = false, length = 20)
    private String telefono;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
    private Medico medico;

    @OneToMany(mappedBy = "usuarioGestor")
    private List<Cita> citasGestionadas = new ArrayList<>();

    @OneToMany(mappedBy = "usuario")
    private List<HistorialCita> historialCitas = new ArrayList<>();

    @OneToMany(mappedBy = "usuario")
    private List<MovimientoInventario> movimientosInventario = new ArrayList<>();

    public Boolean iniciarSesion() {
        return correo != null && !correo.isBlank() && contrasena != null && !contrasena.isBlank();
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public void setRol(RolUsuario rol) {
        this.rol = rol;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public List<Cita> getCitasGestionadas() {
        return citasGestionadas;
    }

    public void setCitasGestionadas(List<Cita> citasGestionadas) {
        this.citasGestionadas = citasGestionadas;
    }

    public List<HistorialCita> getHistorialCitas() {
        return historialCitas;
    }

    public void setHistorialCitas(List<HistorialCita> historialCitas) {
        this.historialCitas = historialCitas;
    }

    public List<MovimientoInventario> getMovimientosInventario() {
        return movimientosInventario;
    }

    public void setMovimientosInventario(List<MovimientoInventario> movimientosInventario) {
        this.movimientosInventario = movimientosInventario;
    }
}
