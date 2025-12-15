package proyecto.conexiondb.JPA.config;

// Importaciones necesarias para la configuración de Spring Boot
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Importaciones específicas de Spring Security
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * CONFIGURACIÓN DE SEGURIDAD - Define las reglas de seguridad para toda la aplicación
 * 
 * Esta clase configura:
 * - Reglas de autorización para endpoints
 * - Manejo de autenticación personalizada
 * - Configuración de logout
 * - Encriptación de contraseñas
 * 
 * NOTA IMPORTANTE: Esta configuración permite acceso libre a todas las rutas
 * porque el sistema maneja la autenticación de forma manual en los controladores
 * usando HttpSession en lugar del sistema automático de Spring Security.
 */
@Configuration // Indica que esta clase contiene configuración de Spring
@EnableWebSecurity // Habilita las funcionalidades de seguridad web de Spring Security
public class SecurityConfig {

    // =============== CONFIGURACIÓN DE FILTROS DE SEGURIDAD ===============
    
    /**
     * Configura la cadena de filtros de seguridad de Spring Security
     * 
     * CONFIGURACIÓN ACTUAL:
     * - CSRF deshabilitado (común en desarrollo y APIs REST)
     * - Acceso permitido a todas las rutas sin autenticación automática
     * - Formulario de login por defecto deshabilitado (usamos login personalizado)
     * - Logout configurado para limpiar sesión y redirigir al login
     * 
     * @param http Objeto HttpSecurity para configurar la seguridad
     * @return SecurityFilterChain configurada
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // DESHABILITAR CSRF: Protección contra Cross-Site Request Forgery
                // Se deshabilita porque manejamos la seguridad manualmente con sesiones
                .csrf(csrf -> csrf.disable())
                
                // CONFIGURAR AUTORIZACIÓN DE PETICIONES HTTP
                .authorizeHttpRequests(auth -> auth
                        // PERMITIR ACCESO A TODAS LAS RUTAS sin autenticación automática
                        // La validación de acceso se hace manualmente en cada controlador
                        .anyRequest().permitAll()
                )
                
                // DESHABILITAR FORMULARIO DE LOGIN POR DEFECTO
                // Usamos nuestro propio formulario de login personalizado
                .formLogin(form -> form.disable())
                
                // CONFIGURAR LOGOUT (CIERRE DE SESIÓN)
                .logout(logout -> logout
                        .logoutUrl("/logout")           // URL que ejecuta el cierre de sesión
                        .logoutSuccessUrl("/login")     // Redirige al login después del logout
                        .permitAll()                    // Permite acceso al logout sin autenticación
                );

        return http.build(); // Construye y retorna la configuración de seguridad
    }

    // =============== CONFIGURACIÓN DE ENCRIPTACIÓN DE CONTRASEÑAS ===============
    
    /**
     * Bean para codificar contraseñas usando BCrypt
     * 
     * BCrypt es un algoritmo de hash seguro que:
     * - Genera un salt aleatorio para cada contraseña
     * - Es resistente a ataques de fuerza bruta
     * - Es el estándar recomendado para almacenar contraseñas
     * 
     * USO: Se utiliza en LoginController para verificar contraseñas
     * y en controladores de registro para encriptar nuevas contraseñas
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
