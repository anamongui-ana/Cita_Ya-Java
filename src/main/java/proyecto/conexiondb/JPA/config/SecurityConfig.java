package proyecto.conexiondb.JPA.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Permite acceso a recursos estáticos y páginas públicas
                .requestMatchers("/", "/login", "/registro", "/restablecer").permitAll()
                .requestMatchers("/css/**", "/js/**", "/img/**", "/Assets/**", "/static/**").permitAll()
                .requestMatchers("/Dashboard/**").authenticated()  // Solo dashboard requiere auth
                .anyRequest().permitAll()  // Cambiado temporalmente para debug
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")  // IMPORTANTE: misma URL que loginPage
                .usernameParameter("numerodoc")
                .passwordParameter("contrasena")
                .defaultSuccessUrl("/Dashboard/paciente", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .csrf(csrf -> csrf.disable());  // Deshabilita CSRF temporalmente para testing

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}