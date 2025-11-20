package proyecto.conexiondb.JPA.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import proyecto.conexiondb.JPA.Entity.Historia_Clinica;



public interface HistoriaClinicaRepository extends JpaRepository<Historia_Clinica, Long> {}