package proyecto.conexiondb.JPA.Controller;

import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import proyecto.conexiondb.JPA.Entity.Administrador;
import proyecto.conexiondb.JPA.Repository.AdministradorRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/administrador")
public class AdministradorController {

    private final AdministradorRepository administradorRepository;
    private final PasswordEncoder passwordEncoder;

    public AdministradorController(AdministradorRepository administradorRepository, PasswordEncoder passwordEncoder) {
        this.administradorRepository = administradorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("administradores", administradorRepository.findAll(Sort.by(Sort.Direction.DESC, "nombre")));
        return "administradores/index";
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

    @PostMapping("/profile/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateProfile(
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam int tipoDoc,
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
