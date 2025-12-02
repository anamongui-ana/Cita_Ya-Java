package proyecto.conexiondb.JPA.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/inicioSesion")
    public String inicioSesion() {
        return "auth/inicioSesion";
    }

    @GetMapping("/registro")
    public String registro() {
        return "auth/registro";
    }

    @GetMapping("/restablecer")
    public String restablecer() {
        return "auth/restablecer";
    }

    @GetMapping("/Dashboard/medico")
    public String DashBoardMedico() {
        return "Dashboard/medico";
    }

    @GetMapping("/Dashboard/paciente")
    public String DashBoardPaciente() {
        return "Dashboard/paciente";
    }
}
