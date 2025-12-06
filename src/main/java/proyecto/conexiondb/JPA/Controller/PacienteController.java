package proyecto.conexiondb.JPA.Controller;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import proyecto.conexiondb.JPA.Entity.Paciente;
import proyecto.conexiondb.JPA.Repository.PacienteRepository;

import java.util.Optional;

@Controller
@RequestMapping("/pacientes")
public class PacienteController {

    private final PacienteRepository pacienteRepository;
    private final proyecto.conexiondb.JPA.Repository.AgendamientoRepository agendamientoRepository;

    public PacienteController(PacienteRepository pacienteRepository,
                             proyecto.conexiondb.JPA.Repository.AgendamientoRepository agendamientoRepository) {
        this.pacienteRepository = pacienteRepository;
        this.agendamientoRepository = agendamientoRepository;
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model, jakarta.servlet.http.HttpSession session) {
        proyecto.conexiondb.JPA.Entity.Paciente paciente = 
            (proyecto.conexiondb.JPA.Entity.Paciente) session.getAttribute("usuario");
        
        if (paciente == null) {
            return "redirect:/login";
        }
        
        // Obtener las citas del paciente
        java.util.List<proyecto.conexiondb.JPA.Entity.Agendamiento> citas = 
            agendamientoRepository.findByPaciente(paciente);
        
        model.addAttribute("citas", citas);
        model.addAttribute("totalCitas", citas.size());
        
        return "layouts/paciente";
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("pacientes", pacienteRepository.findAll(Sort.by(Sort.Direction.DESC, "idPaciente")));
        model.addAttribute("filtered", false);
        return "pacientes/index";
    }

    @GetMapping("/create")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("paciente", new Paciente());
        return "pacientes/create";
    }

    @PostMapping("/create")
    public String crear(@ModelAttribute Paciente paciente, RedirectAttributes redirectAttrs) {
        pacienteRepository.save(paciente);
        redirectAttrs.addFlashAttribute("success", "Paciente creado exitosamente");
        return "redirect:/pacientes";
    }

    // Rutas para el paciente logueado (deben ir ANTES de /{id})
    @GetMapping("/perfil")
    public String verPerfil(Model model, jakarta.servlet.http.HttpSession session, RedirectAttributes redirectAttrs) {
        Paciente paciente = (Paciente) session.getAttribute("usuario");
        if (paciente == null) {
            return "redirect:/login";
        }
        
        // Recargar el paciente desde la base de datos para tener datos actualizados
        Optional<Paciente> pacienteOpt = pacienteRepository.findById(paciente.getIdPaciente());
        if (pacienteOpt.isPresent()) {
            model.addAttribute("paciente", pacienteOpt.get());
            return "pacientes/verPerfil"; // Vista específica para pacientes
        } else {
            redirectAttrs.addFlashAttribute("error", "Paciente no encontrado");
            return "redirect:/Layouts/paciente";
        }
    }

    @GetMapping("/editar")
    public String editarPerfil(Model model, jakarta.servlet.http.HttpSession session, RedirectAttributes redirectAttrs) {
        Paciente paciente = (Paciente) session.getAttribute("usuario");
        if (paciente == null) {
            return "redirect:/login";
        }
        
        // Recargar el paciente desde la base de datos para tener datos actualizados
        Optional<Paciente> pacienteOpt = pacienteRepository.findById(paciente.getIdPaciente());
        if (pacienteOpt.isPresent()) {
            model.addAttribute("paciente", pacienteOpt.get());
            return "pacientes/editarPerfil"; // Vista específica para pacientes
        } else {
            redirectAttrs.addFlashAttribute("error", "Paciente no encontrado");
            return "redirect:/Layouts/paciente";
        }
    }

    @PostMapping("/editar")
    public String actualizarPerfil(@ModelAttribute Paciente paciente, 
                                   jakarta.servlet.http.HttpSession session,
                                   RedirectAttributes redirectAttrs) {
        Paciente pacienteSession = (Paciente) session.getAttribute("usuario");
        if (pacienteSession == null) {
            return "redirect:/login";
        }
        
        Optional<Paciente> pacienteOpt = pacienteRepository.findById(pacienteSession.getIdPaciente());
        if (pacienteOpt.isPresent()) {
            Paciente pacienteExistente = pacienteOpt.get();
            pacienteExistente.setNombre(paciente.getNombre());
            pacienteExistente.setApellido(paciente.getApellido());
            pacienteExistente.setTipoDoc(paciente.getTipoDoc());
            pacienteExistente.setNumeroDoc(paciente.getNumeroDoc());
            pacienteExistente.setGenero(paciente.getGenero());
            pacienteExistente.setTelefono(paciente.getTelefono());
            pacienteExistente.setCorreo(paciente.getCorreo());
            pacienteExistente.setFechaNacimiento(paciente.getFechaNacimiento());
            pacienteExistente.setTipoSangre(paciente.getTipoSangre());
            pacienteExistente.setDireccion(paciente.getDireccion());
            if (paciente.getContrasena() != null && !paciente.getContrasena().isEmpty()) {
                pacienteExistente.setContrasena(paciente.getContrasena());
            }
            pacienteRepository.save(pacienteExistente);
            
            // Actualizar la sesión con los datos nuevos
            session.setAttribute("usuario", pacienteExistente);
            session.setAttribute("nombreCompleto", pacienteExistente.getNombre() + " " + pacienteExistente.getApellido());
            
            redirectAttrs.addFlashAttribute("success", "Perfil actualizado exitosamente");
            return "redirect:/Layouts/paciente";
        } else {
            redirectAttrs.addFlashAttribute("error", "Paciente no encontrado");
            return "redirect:/Layouts/paciente";
        }
    }

    @GetMapping("/{id}")
    public String ver(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttrs) {
        Optional<Paciente> pacienteOpt = pacienteRepository.findById(id);
        if (pacienteOpt.isPresent()) {
            model.addAttribute("paciente", pacienteOpt.get());
            return "pacientes/show";
        } else {
            redirectAttrs.addFlashAttribute("error", "Paciente no encontrado");
            return "redirect:/pacientes";
        }
    }

    @GetMapping("/edit/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttrs) {
        Optional<Paciente> pacienteOpt = pacienteRepository.findById(id);
        if (pacienteOpt.isPresent()) {
            model.addAttribute("paciente", pacienteOpt.get());
            model.addAttribute("esPerfilPropio", false); // Indicador para el formulario
            return "pacientes/edit";
        } else {
            redirectAttrs.addFlashAttribute("error", "Paciente no encontrado");
            return "redirect:/pacientes";
        }
    }

    @PostMapping("/edit/{id}")
    public String editar(@PathVariable("id") Long id, @ModelAttribute Paciente paciente, RedirectAttributes redirectAttrs) {
        Optional<Paciente> pacienteOpt = pacienteRepository.findById(id);
        if (pacienteOpt.isPresent()) {
            Paciente pacienteExistente = pacienteOpt.get();
            pacienteExistente.setNombre(paciente.getNombre());
            pacienteExistente.setApellido(paciente.getApellido());
            pacienteExistente.setTipoDoc(paciente.getTipoDoc());
            pacienteExistente.setNumeroDoc(paciente.getNumeroDoc());
            pacienteExistente.setGenero(paciente.getGenero());
            pacienteExistente.setTelefono(paciente.getTelefono());
            pacienteExistente.setCorreo(paciente.getCorreo());
            pacienteExistente.setFechaNacimiento(paciente.getFechaNacimiento());
            pacienteExistente.setTipoSangre(paciente.getTipoSangre());
            pacienteExistente.setDireccion(paciente.getDireccion());
            if (paciente.getContrasena() != null && !paciente.getContrasena().isEmpty()) {
                pacienteExistente.setContrasena(paciente.getContrasena());
            }
            pacienteRepository.save(pacienteExistente);
            redirectAttrs.addFlashAttribute("success", "Paciente actualizado exitosamente");
            return "redirect:/pacientes";
        } else {
            redirectAttrs.addFlashAttribute("error", "Paciente no encontrado");
            return "redirect:/pacientes";
        }
    }

    @PostMapping("/delete/{id}")
    public String eliminar(@PathVariable("id") Long id, RedirectAttributes redirectAttrs) {
        Optional<Paciente> pacienteOpt = pacienteRepository.findById(id);
        if (pacienteOpt.isPresent()) {
            pacienteRepository.deleteById(id);
            redirectAttrs.addFlashAttribute("success", "Paciente eliminado exitosamente");
        } else {
            redirectAttrs.addFlashAttribute("error", "Paciente no encontrado");
        }
        return "redirect:/pacientes";
    }
}
