package proyecto.conexiondb.JPA.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import proyecto.conexiondb.JPA.Entity.Paciente;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    Paciente findByNumeroDoc(String numeroDoc);
    
    Paciente findByCorreo(String correo);
}