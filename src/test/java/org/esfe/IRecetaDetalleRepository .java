package org.esfe.repositorios;

import org.esfe.modelos.RecetaDetalle;
import org.esfe.modelos.enums.EstadoReceta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IRecetaDetalleRepository extends JpaRepository<RecetaDetalle, Integer> {

    List<RecetaDetalle> findByConsultaIdConsulta(Integer idConsulta);

    List<RecetaDetalle> findByMedicamentoIdMedicamento(Integer idMedicamento);

    List<RecetaDetalle> findByEstado(EstadoReceta estado);

    boolean existsByConsultaIdConsultaAndMedicamentoIdMedicamento(Integer idConsulta, Integer idMedicamento);

    @Query("SELECT r FROM RecetaDetalle r WHERE r.consulta.cita.medico.idMedico = :idMedico")
    List<RecetaDetalle> findByMedico(@Param("idMedico") Integer idMedico);

    @Query("SELECT r FROM RecetaDetalle r WHERE r.consulta.cita.paciente.idPaciente = :idPaciente")
    List<RecetaDetalle> findByPaciente(@Param("idPaciente") Integer idPaciente);
}
