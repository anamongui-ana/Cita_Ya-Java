package proyecto.conexiondb.JPA.Controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;
import proyecto.conexiondb.JPA.Entity.Paciente;
import proyecto.conexiondb.JPA.Repository.PacienteRepository;

@Controller
public class DashboardController {

    @Autowired
    private PacienteRepository pacienteRepository;

    @GetMapping("/Dashboard/paciente")
    public String dashboardPaciente(Authentication authentication, Model model) {
        String numeroDoc = authentication.getName();
        Paciente paciente = pacienteRepository.findByNumeroDoc(numeroDoc);
        
        model.addAttribute("paciente", paciente);
        return "Dashboard/paciente";  // paciente.html en templates/Dashboard/
    }
}