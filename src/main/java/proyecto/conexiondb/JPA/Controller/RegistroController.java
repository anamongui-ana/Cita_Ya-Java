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
            @RequestParam("tipodoc") String tipoDoc,
            @RequestParam("numerodoc") String numeroDoc,
            @RequestParam("nombrecompleto") String nombreCompleto,
            @RequestParam("genero") String genero,
            @RequestParam("fechanacimiento") String fechaNacimiento,
            @RequestParam(value = "tiposangre", required = false) String tipoSangre,
            @RequestParam(value = "direccion", required = false) String direccion,
            @RequestParam("telefono") String telefono,
            @RequestParam("correo") String correo,
            @RequestParam("contrasena") String contrasena,
            @RequestParam("confirmarContrasena") String confirmarContrasena,
            Model model) {

        try {
            // Validar documento (6-10 dígitos)
            if (numeroDoc == null || !numeroDoc.matches("\\d{6,10}")) {
                model.addAttribute("error", "El número de documento debe tener entre 6 y 10 dígitos.");
                return "auth/registro";
            }

            // Validar teléfono (7-10 dígitos)
            if (telefono == null || !telefono.matches("\\d{7,10}")) {
                model.addAttribute("error", "El teléfono debe tener entre 7 y 10 dígitos.");
                return "auth/registro";
            }

            // Validar contraseña (mínimo 8 caracteres, mayúsculas, minúsculas, números y caracteres especiales)
            String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&.#-])[A-Za-z\\d@$!%*?&.#-]{8,}$";
            if (contrasena == null || !contrasena.matches(passwordRegex)) {
                model.addAttribute("error", "La contraseña debe tener mínimo 8 caracteres con mayúsculas, minúsculas, números y caracteres especiales.");
                return "auth/registro";
            }

            // Validar que las contraseñas coincidan
            if (!contrasena.equals(confirmarContrasena)) {
                model.addAttribute("error", "Las contraseñas no coinciden.");
                return "auth/registro";
            }

            // Validar que el documento no esté registrado
            if (pacienteRepository.findByNumeroDoc(numeroDoc) != null) {
                model.addAttribute("error", "El número de documento ya está registrado.");
                return "auth/registro";
            }

            // Validar que el correo no esté registrado
            if (pacienteRepository.findByCorreo(correo) != null) {
                model.addAttribute("error", "El correo electrónico ya está registrado.");
                return "auth/registro";
            }

            // Separar nombre completo en nombre y apellido
            String[] partes = nombreCompleto.trim().split("\\s+", 2);
            String nombre = partes[0];
            String apellido = partes.length > 1 ? partes[1] : "";

            // Crear el paciente
            Paciente paciente = new Paciente();
            paciente.setTipoDoc(tipoDoc);
            paciente.setNumeroDoc(numeroDoc);
            paciente.setNombre(nombre);
            paciente.setApellido(apellido);
            paciente.setGenero(genero);
            
            // Convertir fecha
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date fecha = formatter.parse(fechaNacimiento);
            paciente.setFechaNacimiento(fecha);
            
            paciente.setTipoSangre(tipoSangre);
            paciente.setDireccion(direccion);
            paciente.setTelefono(telefono);
            paciente.setCorreo(correo);
            paciente.setContrasena(contrasena); // En producción, deberías encriptar la contraseña

            // Guardar
            pacienteRepository.save(paciente);

            return "redirect:/login?registro=exitoso";

        } catch (Exception e) {
            model.addAttribute("error", "Error al registrar: " + e.getMessage());
            return "auth/registro";
        }
    }
}