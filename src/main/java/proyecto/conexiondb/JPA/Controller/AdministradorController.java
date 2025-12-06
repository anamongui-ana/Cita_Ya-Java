package proyecto.conexiondb.JPA.Controller;

import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import proyecto.conexiondb.JPA.Entity.Administrador;
import proyecto.conexiondb.JPA.Repository.AdministradorRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/administradores")
public class AdministradorController {

    private final AdministradorRepository administradorRepository;
    private final PasswordEncoder passwordEncoder;

    public AdministradorController(AdministradorRepository administradorRepository, PasswordEncoder passwordEncoder) {
        this.administradorRepository = administradorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("administradores", administradorRepository.findAll(Sort.by(Sort.Direction.DESC, "id")));
        return "administradores/index";
    }

    @GetMapping("/create")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("administrador", new Administrador());
        return "administradores/create";
    }

    @PostMapping("/create")
    public String crear(@ModelAttribute Administrador administrador, RedirectAttributes redirectAttrs) {
        administradorRepository.save(administrador);
        redirectAttrs.addFlashAttribute("success", "Administrador creado exitosamente");
        return "redirect:/administradores";
    }

    @GetMapping("/{id}")
    public String ver(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttrs) {
        Optional<Administrador> adminOpt = administradorRepository.findById(id);
        if (adminOpt.isPresent()) {
            model.addAttribute("administrador", adminOpt.get());
            return "administradores/show";
        } else {
            redirectAttrs.addFlashAttribute("error", "Administrador no encontrado");
            return "redirect:/administradores";
        }
    }

    @GetMapping("/edit/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttrs) {
        Optional<Administrador> adminOpt = administradorRepository.findById(id);
        if (adminOpt.isPresent()) {
            model.addAttribute("administrador", adminOpt.get());
            return "administradores/edit";
        } else {
            redirectAttrs.addFlashAttribute("error", "Administrador no encontrado");
            return "redirect:/administradores";
        }
    }

    @PostMapping("/edit/{id}")
    public String editar(@PathVariable("id") Integer id, @ModelAttribute Administrador administrador, RedirectAttributes redirectAttrs) {
        Optional<Administrador> adminOpt = administradorRepository.findById(id);
        if (adminOpt.isPresent()) {
            Administrador adminExistente = adminOpt.get();
            adminExistente.setNombre(administrador.getNombre());
            adminExistente.setApellido(administrador.getApellido());
            adminExistente.setTipoDoc(administrador.getTipoDoc());
            adminExistente.setNumeroDoc(administrador.getNumeroDoc());
            adminExistente.setGenero(administrador.getGenero());
            adminExistente.setTelefono(administrador.getTelefono());
            adminExistente.setCorreo(administrador.getCorreo());
            if (administrador.getContraseña() != null && !administrador.getContraseña().isEmpty()) {
                adminExistente.setContraseña(administrador.getContraseña());
            }
            administradorRepository.save(adminExistente);
            redirectAttrs.addFlashAttribute("success", "Administrador actualizado exitosamente");
            return "redirect:/administradores";
        } else {
            redirectAttrs.addFlashAttribute("error", "Administrador no encontrado");
            return "redirect:/administradores";
        }
    }

    @PostMapping("/delete/{id}")
    public String eliminar(@PathVariable("id") Integer id, RedirectAttributes redirectAttrs) {
        Optional<Administrador> adminOpt = administradorRepository.findById(id);
        if (adminOpt.isPresent()) {
            administradorRepository.deleteById(id);
            redirectAttrs.addFlashAttribute("success", "Administrador eliminado exitosamente");
        } else {
            redirectAttrs.addFlashAttribute("error", "Administrador no encontrado");
        }
        return "redirect:/administradores";
    }

    @PostMapping("/profile/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateProfile(
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String tipoDoc,
            @RequestParam String numeroDoc,
            @RequestParam String genero,
            @RequestParam(required = false) String telefono,
            @RequestParam String correo,
            @RequestParam(required = false) String contraseña,
            Authentication authentication) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Administrador currentAdmin = (Administrador) authentication.getPrincipal();
            Optional<Administrador> adminOpt = administradorRepository.findById(currentAdmin.getId());
            
            if (adminOpt.isPresent()) {
                Administrador admin = adminOpt.get();
                admin.setNombre(nombre);
                admin.setApellido(apellido);
                admin.setTipoDoc(tipoDoc);
                admin.setNumeroDoc(numeroDoc);
                admin.setGenero(genero);
                admin.setTelefono(telefono);
                admin.setCorreo(correo);
                admin.setEstado(true);
                
                
                if (contraseña != null && !contraseña.trim().isEmpty()) {
                    admin.setContraseña(passwordEncoder.encode(contraseña));
                }
                
                administradorRepository.save(admin);
                
                response.put("success", true);
                response.put("message", "Perfil actualizado exitosamente");
            } else {
                response.put("success", false);
                response.put("message", "Administrador no encontrado");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al actualizar el perfil: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
}
