package proyecto.conexiondb.JPA.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import proyecto.conexiondb.JPA.Entity.Paciente;
import proyecto.conexiondb.JPA.Repository.PacienteRepository;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Paciente registrarPaciente(Paciente paciente) {
        // Encripta automáticamente la contraseña
        String passwordEncriptada = passwordEncoder.encode(paciente.getContrasena());
        paciente.setContrasena(passwordEncriptada);
        
        return pacienteRepository.save(paciente);
    }
}