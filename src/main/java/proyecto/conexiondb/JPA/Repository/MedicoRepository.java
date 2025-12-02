package proyecto.conexiondb.JPA.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import proyecto.conexiondb.JPA.Entity.Medico;



public interface MedicoRepository extends JpaRepository<Medico, Long> {}