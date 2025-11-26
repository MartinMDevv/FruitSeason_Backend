package com.example.FruitseasonBackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.example.FruitseasonBackend.security.JwtAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

/**
 * Configuración de seguridad de Spring Security
 * 
 * Responsabilidades:
 * - Define qué endpoints son públicos y cuáles requieren autenticación
 * - Configura el filtro JWT para validar tokens en cada petición
 * - Proporciona beans para encriptación de contraseñas y gestión de
 * autenticación
 */
@Configuration
@EnableMethodSecurity // Habilita @PreAuthorize, @Secured, etc.
public class SecurityConfig {

    // Inyección por constructor (mejor práctica que @Autowired en campos)
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Configura la cadena de filtros de seguridad
     * 
     * @param http - Constructor de seguridad HTTP
     * @return SecurityFilterChain configurado
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Deshabilita CSRF - Seguro para APIs REST stateless con JWT
                // En APIs REST, cada petición lleva el token, no hay sesiones con cookies
                .csrf(csrf -> csrf.disable())

                // Configura CORS para permitir peticiones desde el frontend
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Define políticas de autorización para endpoints
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos - NO requieren autenticación
                        .requestMatchers("/api/auth/login", "/api/auth/register", "/").permitAll()

                        // ✅ AÑADIDO: Comentarios públicos (crear y listar)
                        .requestMatchers("/api/comments", "/api/comments/**").permitAll()

                        // Swagger UI y OpenAPI docs - Acceso público para documentación
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // Todos los demás endpoints requieren autenticación válida
                        .anyRequest().authenticated())

                // Política de sesión STATELESS - No crea sesiones HTTP
                // JWT maneja el estado de autenticación
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Añade el filtro JWT ANTES del filtro de autenticación estándar
                // Esto permite validar el token antes de procesar la petición
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Bean del gestor de autenticación
     * Usado en AuthController para autenticar usuarios durante el login
     * 
     * @param config - Configuración de autenticación de Spring
     * @return AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Bean del codificador de contraseñas
     * BCrypt es un algoritmo seguro de hashing con salt automático
     * 
     * Usado para:
     * - Hashear contraseñas al registrar usuarios
     * - Verificar contraseñas al hacer login
     * 
     * @return PasswordEncoder usando BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configuración CORS (Cross-Origin Resource Sharing)
     * Permite que el frontend en otro dominio/puerto acceda a la API
     * 
     * IMPORTANTE: Ajusta los orígenes permitidos según tu entorno
     * 
     * @return Fuente de configuración CORS
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Orígenes permitidos - AJUSTA según tu frontend
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000", // React default
                "http://localhost:4200", // Angular default
                "http://localhost:5173" // Vite default
        ));

        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Headers permitidos
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // Permite enviar credenciales (cookies, authorization headers)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}