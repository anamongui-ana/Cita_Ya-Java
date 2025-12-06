package proyecto.conexiondb.JPA.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import proyecto.conexiondb.JPA.Entity.Paciente;
import proyecto.conexiondb.JPA.Repository.PacienteRepository;

@Service
public class PacienteUserDetailsService implements UserDetailsService {


    @Autowired
    private PacienteRepository pacienteRepository;

    @Override
    public UserDetails loadUserByUsername(String numeroDoc) throws UsernameNotFoundException {
        
        System.out.println("🔍 Buscando paciente con documento: " + numeroDoc);
        
        Paciente paciente = pacienteRepository.findByNumeroDoc(numeroDoc);
        
        if (paciente == null) {
            System.out.println("❌ Paciente NO encontrado");
            throw new UsernameNotFoundException("Paciente no encontrado: " + numeroDoc);
        }
        
        System.out.println("✅ Paciente encontrado: " + paciente.getNombre());
        System.out.println("🔑 Contraseña en BD: " + paciente.getContrasena());

        return User.builder()
                .username(paciente.getNumeroDoc())
                .password(paciente.getContrasena())  // Debe estar hasheada con BCrypt
                .roles("PACIENTE")
                .build();
    }
}