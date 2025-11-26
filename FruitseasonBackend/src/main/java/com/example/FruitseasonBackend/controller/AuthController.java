package com.example.FruitseasonBackend.controller;

import com.example.FruitseasonBackend.model.entity.User;
import com.example.FruitseasonBackend.service.UserService;
import com.example.FruitseasonBackend.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.Map;

/**
 * Controlador de Autenticación
 * 
 * Responsabilidades:
 * - Registro de nuevos usuarios (/auth/register)
 * - Login y generación de JWT (/auth/login)
 * 
 * Endpoints:
 * - POST /auth/register - Crea cuenta de usuario
 * - POST /auth/login - Autentica y devuelve token JWT
 */
@Tag(name = "Autenticación", description = "Endpoints públicos para registro y login de usuarios")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

        // Inyección por constructor (mejores prácticas)
        private final UserService userService;
        private final AuthenticationManager authenticationManager;
        private final JwtUtil jwtUtil;

        public AuthController(UserService userService,
                        AuthenticationManager authenticationManager,
                        JwtUtil jwtUtil) {
                this.userService = userService;
                this.authenticationManager = authenticationManager;
                this.jwtUtil = jwtUtil;
        }

        // ============= DTOs (Data Transfer Objects) =============

        /**
         * DTO para registro de usuario
         * Valida datos de entrada antes de procesarlos
         */
        public record RegisterRequest(
                        @NotBlank(message = "El nombre de usuario es obligatorio") @Size(min = 3, max = 50, message = "El username debe tener entre 3 y 50 caracteres") String username,

                        @NotBlank(message = "El email es obligatorio") @Email(message = "Email inválido") String email,

                        @NotBlank(message = "La contraseña es obligatoria") @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres") String password) {
        }

        /**
         * DTO para login de usuario
         */
        public record LoginRequest(
                        @NotBlank(message = "El nombre de usuario es obligatorio") String username,

                        @NotBlank(message = "La contraseña es obligatoria") String password) {
        }

        /**
         * DTO para respuestas de autenticación exitosa
         */
        public record AuthResponse(String token, String username, String message) {
        }

        /**
         * DTO para respuestas de error
         */
        public record ErrorResponse(String error, String message) {
        }

        // ============= Endpoints de Autenticación =============

        /**
         * POST /auth/register
         * Registra un nuevo usuario en el sistema
         * 
         * @param req - Datos del usuario (username, email, password)
         * @return ResponseEntity con mensaje de éxito o error
         * 
         *         Flujo:
         *         1. Valida datos de entrada (anotaciones @Valid)
         *         2. Verifica que username/email no existan
         *         3. Hashea la contraseña con BCrypt
         *         4. Guarda el usuario en la BD
         *         5. Retorna mensaje de éxito
         */
        @Operation(summary = "Registrar nuevo usuario", description = "Crea una nueva cuenta de usuario. El username y email deben ser únicos. La contraseña debe tener mínimo 8 caracteres.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
                        @ApiResponse(responseCode = "400", description = "Datos inválidos o usuario/email ya existe")
        })
        @PostMapping("/register")
        public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
                try {
                        // Registra el usuario (la contraseña se hashea en UserService)
                        User user = userService.register(req.username(), req.email(), req.password());

                        // Respuesta de éxito
                        return ResponseEntity.status(HttpStatus.CREATED)
                                        .body(Map.of(
                                                        "message", "Usuario registrado exitosamente",
                                                        "username", user.getUsername()));

                } catch (IllegalArgumentException ex) {
                        // Error de validación de negocio (username/email duplicado)
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(new ErrorResponse("VALIDATION_ERROR", ex.getMessage()));

                } catch (Exception ex) {
                        // Error inesperado - NO exponer detalles internos al cliente
                        // Log interno: ex.printStackTrace() o usar Logger
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ErrorResponse("SERVER_ERROR", "Error al procesar el registro"));
                }
        }

        /**
         * POST /auth/login
         * Autentica un usuario y genera un token JWT
         * 
         * @param req - Credenciales (username, password)
         * @return ResponseEntity con el token JWT o error 401
         * 
         *         Flujo:
         *         1. Valida datos de entrada
         *         2. Verifica credenciales con AuthenticationManager
         *         3. Si son válidas, genera un token JWT
         *         4. Retorna el token al cliente
         *         5. El cliente debe incluir este token en el header Authorization de
         *         peticiones futuras
         */
        @Operation(summary = "Iniciar sesión", description = "Autentica un usuario con username y contraseña. Retorna un token JWT que debe usarse en el header Authorization: Bearer <token> para acceder a endpoints protegidos.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Login exitoso, retorna token JWT"),
                        @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
        })
        @PostMapping("/login")
        public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
                try {
                        // Crea el token de autenticación con username y password
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                        req.username(),
                                        req.password());

                        // Autentica las credenciales (lanza BadCredentialsException si fallan)
                        authenticationManager.authenticate(authToken);

                        // Genera el token JWT para el usuario autenticado
                        String token = jwtUtil.generateToken(req.username());

                        // Respuesta exitosa con el token
                        return ResponseEntity.ok(new AuthResponse(
                                        token,
                                        req.username(),
                                        "Login exitoso"));

                } catch (BadCredentialsException ex) {
                        // Credenciales inválidas
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                        .body(new ErrorResponse("INVALID_CREDENTIALS",
                                                        "Usuario o contraseña incorrectos"));

                } catch (Exception ex) {
                        // Error inesperado
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ErrorResponse("SERVER_ERROR", "Error al procesar el login"));
                }
        }

}