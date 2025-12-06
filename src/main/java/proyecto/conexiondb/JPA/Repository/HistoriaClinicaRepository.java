package proyecto.conexiondb.JPA.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import proyecto.conexiondb.JPA.Entity.Historia_Clinica;
import proyecto.conexiondb.JPA.Entity.Medico;
import proyecto.conexiondb.JPA.Entity.Paciente;

import java.util.List;

public interface HistoriaClinicaRepository extends JpaRepository<Historia_Clinica, Long> {
    
    // Buscar historias clínicas de un paciente específico ordenadas por fecha de atención
    List<Historia_Clinica> findByPacienteOrderByFechaAtencionDesc(Paciente paciente);
    
    // Buscar historias clínicas creadas por un médico ordenadas por fecha de atención
    List<Historia_Clinica> findByMedicoOrderByFechaAtencionDesc(Medico medico);
    
    // Buscar historias clínicas de un paciente creadas por un médico específico
    @Query("SELECT h FROM Historia_Clinica h WHERE h.paciente = :paciente AND h.medico = :medico ORDER BY h.fechaAtencion DESC")
    List<Historia_Clinica> findByPacienteAndMedico(@Param("paciente") Paciente paciente, @Param("medico") Medico medico);
}