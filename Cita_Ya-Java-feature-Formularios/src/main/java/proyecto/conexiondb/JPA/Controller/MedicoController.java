package proyecto.conexiondb.JPA.Controller;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import proyecto.conexiondb.JPA.Entity.Medico;
import proyecto.conexiondb.JPA.Repository.MedicoRepository;

import java.util.Optional;

@Controller
@RequestMapping("/medicos")
public class MedicoController {

    private final MedicoRepository medicoRepository;

    public MedicoController(MedicoRepository medicoRepository) {
        this.medicoRepository = medicoRepository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("medicos", medicoRepository.findAll(Sort.by(Sort.Direction.DESC, "idMedico")));
        return "medicos/index";
    }

    @GetMapping("/create")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("medico", new Medico());
        return "medicos/create";
    }

    @PostMapping("/store")
    public String crear(@ModelAttribute Medico medico, RedirectAttributes redirectAttrs) {
        medicoRepository.save(medico);
        redirectAttrs.addFlashAttribute("success", "Médico creado exitosamente");
        return "redirect:/medicos";
    }

    @GetMapping("/show/{id}")
    public String ver(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttrs) {
        Optional<Medico> medicoOpt = medicoRepository.findById(id);
        if (medicoOpt.isPresent()) {
            model.addAttribute("medico", medicoOpt.get());
            return "medicos/show";
        } else {
            redirectAttrs.addFlashAttribute("error", "Médico no encontrado");
            return "redirect:/medicos";
        }
    }

    @GetMapping("/edit/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttrs) {
        Optional<Medico> medicoOpt = medicoRepository.findById(id);
        if (medicoOpt.isPresent()) {
            model.addAttribute("medico", medicoOpt.get());
            return "medicos/edit";
        } else {
            redirectAttrs.addFlashAttribute("error", "Médico no encontrado");
            return "redirect:/medicos";
        }
    }

    @PostMapping("/update/{id}")
    public String editar(@PathVariable("id") Long id, @ModelAttribute Medico medico, RedirectAttributes redirectAttrs) {
        Optional<Medico> medicoOpt = medicoRepository.findById(id);
        if (medicoOpt.isPresent()) {
            Medico medicoExistente = medicoOpt.get();
            medicoExistente.setNombre(medico.getNombre());
            medicoExistente.setApellido(medico.getApellido());
            medicoExistente.setTipoDoc(medico.getTipoDoc());
            medicoExistente.setNumeroDoc(medico.getNumeroDoc());
            medicoExistente.setGenero(medico.getGenero());
            medicoExistente.setTelefono(medico.getTelefono());
            medicoExistente.setCorreo(medico.getCorreo());
            medicoExistente.setEspecialidad(medico.getEspecialidad());
            medicoExistente.setEstado(medico.getEstado());
            if (medico.getContraseña() != null && !medico.getContraseña().isEmpty()) {
                medicoExistente.setContraseña(medico.getContraseña());
            }
            medicoRepository.save(medicoExistente);
            redirectAttrs.addFlashAttribute("success", "Médico actualizado exitosamente");
            return "redirect:/medicos";
        } else {
            redirectAttrs.addFlashAttribute("error", "Médico no encontrado");
            return "redirect:/medicos";
        }
    }

    @PostMapping("/delete/{id}")
    public String eliminar(@PathVariable("id") Long id, RedirectAttributes redirectAttrs) {
        Optional<Medico> medicoOpt = medicoRepository.findById(id);
        if (medicoOpt.isPresent()) {
            medicoRepository.deleteById(id);
            redirectAttrs.addFlashAttribute("success", "Médico eliminado exitosamente");
        } else {
            redirectAttrs.addFlashAttribute("error", "Médico no encontrado");
        }
        return "redirect:/medicos";
    }
}
