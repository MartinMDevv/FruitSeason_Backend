package com.example.FruitseasonBackend.controller;

import com.example.FruitseasonBackend.model.entity.User;
import com.example.FruitseasonBackend.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

/**
 * Controlador de Suscripciones
 * 
 * Responsabilidades:
 * - Gestionar compra/actualización de planes de suscripción
 * - Endpoints protegidos (requieren autenticación JWT)
 */
@RestController
@RequestMapping("/subscription")
public class SubscriptionController {

    private final UserService userService;

    public SubscriptionController(UserService userService) {
        this.userService = userService;
    }

    // ============= DTOs =============

    /**
     * DTO para solicitud de compra de suscripción
     */
    public record SubscribeRequest(
            @NotBlank(message = "El plan de suscripción es obligatorio") String subscription,

            @NotBlank(message = "El nombre del titular es obligatorio") String cardHolderName,

            @NotBlank(message = "El número de tarjeta es obligatorio") String cardNumber) {
    }

    /**
     * DTO para respuestas de error
     */
    public record ErrorResponse(String error, String message) {
    }

    // ============= Endpoints =============

    /**
     * POST /subscription/purchase
     * Permite a un usuario autenticado comprar o actualizar su suscripción
     * 
     * @param req       - Datos de suscripción (subscription, cardHolderName,
     *                  cardNumber)
     * @param principal - Usuario autenticado (inyectado automáticamente por Spring
     *                  Security)
     * @return ResponseEntity con datos actualizados del usuario
     * 
     *         REQUIERE: Token JWT válido en header Authorization: Bearer <token>
     * 
     *         Flujo:
     *         1. Spring Security valida el token JWT del header
     *         2. Extrae el username del token y lo inyecta en 'principal'
     *         3. Valida los datos de entrada (plan, tarjeta)
     *         4. Actualiza la suscripción del usuario en la BD
     *         5. Guarda el método de pago (solo últimos 4 dígitos)
     *         6. Retorna confirmación con el nuevo plan
     */
    @PostMapping("/purchase")
    public ResponseEntity<?> purchaseSubscription(
            @Valid @RequestBody SubscribeRequest req,
            Principal principal) {
        try {
            // Obtiene el username del usuario autenticado desde el token JWT
            // Spring Security lo inyecta automáticamente si el token es válido
            String username = principal.getName();

            // Procesa la compra de suscripción
            User updatedUser = userService.purchaseSubscription(
                    username,
                    req.subscription(),
                    req.cardHolderName(),
                    req.cardNumber());

            // Respuesta exitosa
            return ResponseEntity.ok(Map.of(
                    "message", "Suscripción actualizada exitosamente",
                    "username", updatedUser.getUsername(),
                    "subscription", updatedUser.getSubscription().toString(),
                    "email", updatedUser.getEmail()));

        } catch (IllegalArgumentException ex) {
            // Error de validación (tarjeta inválida, plan inválido, datos faltantes)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("VALIDATION_ERROR", ex.getMessage()));

        } catch (Exception ex) {
            // Error inesperado del servidor
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("SERVER_ERROR", "Error al procesar la suscripción"));
        }
    }

    /**
     * GET /subscription/plans
     * Lista los planes de suscripción disponibles (endpoint público)
     * 
     * @return Lista de planes con descripción
     */
    @GetMapping("/plans")
    public ResponseEntity<?> getPlans() {
        return ResponseEntity.ok(Map.of(
                "plans", Map.of(
                        "NO_SUBSCRIBED", "Sin suscripción (gratuito)",
                        "BASIC", "Plan básico - Acceso a 4 kg de frutas por semana",
                        "FAMILY", "Plan familiar - Acceso a 8 kg de frutas por semana",
                        "PREMIUM", "Plan premium - Acceso a 12 kg de frutas por semana"),
                "message", "Planes disponibles"));
    }
}