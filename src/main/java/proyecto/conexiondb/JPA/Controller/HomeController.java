package proyecto.conexiondb.JPA.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping({"/", "/index"})
    public String index() {
        return "index";
    }

    @GetMapping("/restablecer")
    public String restablecer() {
        return "auth/restablecer";
    }

    // Todas las variantes posibles de rutas de layouts
    @GetMapping({"/layouts/administrador", "/Layout/administrador", "/Layouts/administrador", "/Layout/admin"})
    public String layoutsAdministrador() {
        return "layouts/administrador";
    }

    @GetMapping({"/layouts/medico", "/Layout/medico", "/Layouts/medico"})
    public String layoutsMedico() {
        return "redirect:/medico/dashboard";
    }

    @GetMapping({"/layouts/paciente", "/Layout/paciente", "/Layouts/paciente"})
    public String layoutsPaciente() {
        return "redirect:/pacientes/dashboard";
    }
}
