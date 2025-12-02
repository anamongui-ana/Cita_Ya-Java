package proyecto.conexiondb.JPA.Controller;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import proyecto.conexiondb.JPA.Entity.Administrador;
import proyecto.conexiondb.JPA.Repository.AdministradorRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/administrador")
public class AdministradorController {

    private final AdministradorRepository administradorRepository;

    public AdministradorController(AdministradorRepository administradorRepository) {
        this.administradorRepository = administradorRepository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("administradores", administradorRepository.findAll(Sort.by(Sort.Direction.DESC, "nombre")));
        return "administrador/formulario";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("administrador", new Administrador());
        return "administrador/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Administrador administrador, RedirectAttributes redirectAttrs) {
        administradorRepository.save(administrador);
        redirectAttrs.addFlashAttribute("mensaje", "Administrador guardado correctamente");
        return "redirect:/administrador";
    }
}
