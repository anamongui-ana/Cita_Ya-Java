package proyecto.conexiondb.JPA.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import proyecto.conexiondb.JPA.Entity.Paciente;
import proyecto.conexiondb.JPA.Repository.PacienteRepository;

import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class RegistroController {

    @Autowired
    private PacienteRepository pacienteRepository;

    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("paciente", new Paciente());
        return "auth/registro";
    }

    @PostMapping("/registro")
    public String registrarPaciente(
            @org.springframework.web.bind.annotation.ModelAttribute Paciente paciente,
            @RequestParam(value = "confirmarContrasena", required = false) String confirmarContrasena,
            Model model) {

        try {
            // Validar documento (5-10 dígitos)
            if (paciente.getNumeroDoc() == null || !paciente.getNumeroDoc().matches("\\d{5,10}")) {
                model.addAttribute("error", "El número de documento debe tener entre 5 y 10 dígitos.");
                model.addAttribute("paciente", paciente);
                return "auth/registro";
            }

            // Validar teléfono (10 dígitos)
            if (paciente.getTelefono() == null || !paciente.getTelefono().matches("\\d{10}")) {
                model.addAttribute("error", "El teléfono debe tener exactamente 10 dígitos.");
                model.addAttribute("paciente", paciente);
                return "auth/registro";
            }

            // Validar contraseña (mínimo 10 caracteres, mayúsculas, minúsculas, números y caracteres especiales)
            String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&.#-])[A-Za-z\\d@$!%*?&.#-]{10,}$";
            if (paciente.getContrasena() == null || !paciente.getContrasena().matches(passwordRegex)) {
                model.addAttribute("error", "La contraseña debe tener mínimo 10 caracteres con mayúsculas, minúsculas, números y caracteres especiales.");
                model.addAttribute("paciente", paciente);
                return "auth/registro";
            }

            // Validar que las contraseñas coincidan
            if (!paciente.getContrasena().equals(confirmarContrasena)) {
                model.addAttribute("error", "Las contraseñas no coinciden.");
                model.addAttribute("paciente", paciente);
                return "auth/registro";
            }

            // Validar que el documento no esté registrado
            if (pacienteRepository.findByNumeroDoc(paciente.getNumeroDoc()) != null) {
                model.addAttribute("error", "El número de documento ya está registrado.");
                model.addAttribute("paciente", paciente);
                return "auth/registro";
            }

            // Validar que el correo no esté registrado
            if (pacienteRepository.findByCorreo(paciente.getCorreo()) != null) {
                model.addAttribute("error", "El correo electrónico ya está registrado.");
                model.addAttribute("paciente", paciente);
                return "auth/registro";
            }

            // Validar nombre (mínimo 5 caracteres sin espacios)
            if (paciente.getNombre() == null || paciente.getNombre().replace(" ", "").length() < 5) {
                model.addAttribute("error", "El nombre debe tener al menos 5 caracteres.");
                model.addAttribute("paciente", paciente);
                return "auth/registro";
            }

            // Validar apellido (mínimo 5 caracteres sin espacios)
            if (paciente.getApellido() == null || paciente.getApellido().replace(" ", "").length() < 5) {
                model.addAttribute("error", "El apellido debe tener al menos 5 caracteres.");
                model.addAttribute("paciente", paciente);
                return "auth/registro";
            }

            // Guardar (en producción, deberías encriptar la contraseña)
            pacienteRepository.save(paciente);

            return "redirect:/login?registro=exitoso";

        } catch (Exception e) {
            model.addAttribute("error", "Error al registrar: " + e.getMessage());
            model.addAttribute("paciente", paciente);
            return "auth/registro";
        }
    }
}