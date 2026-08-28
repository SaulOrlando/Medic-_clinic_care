package org.esfe.servicios.interfaces;

import org.esfe.modelos.Cita;
import org.esfe.modelos.Usuario;

import java.util.List;

public interface IDashboardService {

    long contarCitasHoy(Usuario usuario);

    long contarPacientesRecientes(Usuario usuario);

    long contarInformesPendientes(Usuario usuario);

    List<Long> contarVolumenSemanal(Usuario usuario);

    List<Cita> obtenerProximasCitas(Usuario usuario, int limite);
}