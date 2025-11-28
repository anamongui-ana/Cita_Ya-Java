package proyecto.conexiondb.JPA.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import proyecto.conexiondb.JPA.Entity.Agendamiento;



public interface AgendamientoRepository extends JpaRepository<Agendamiento, Long> {
    
}