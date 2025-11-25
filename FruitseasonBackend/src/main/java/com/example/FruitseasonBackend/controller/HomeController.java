package com.example.FruitseasonBackend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

/**
 * HomeController - Documentación de la API
 * 
 * Proporciona información sobre endpoints disponibles
 */
@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> home() {
        return Map.of(
            "message", "FruitSeason API - Sistema de suscripciones con JWT",
            "version", "1.0.0",
            "endpoints", Map.of(
                "public", Map.of(
                    "POST /auth/register", "Registrar nuevo usuario - Body: {username, email, password}",
                    "POST /auth/login", "Login - Body: {username, password} - Retorna: {token, username, message}",
                    "GET /subscription/plans", "Ver planes de suscripción disponibles",
                    "POST /comments", "Crear comentario anónimo - Body: {email, text}",
                    "GET /comments", "Listar todos los comentarios",
                    "GET /comments/recent?limit=N", "Obtener últimos N comentarios (default: 10)"
                ),
                "protected", Map.of(
                    "POST /subscription/purchase", "Comprar suscripción (requiere JWT) - Body: {subscription, cardHolderName, cardNumber}",
                    "description", "Endpoints protegidos requieren header: Authorization: Bearer <token>"
                )
            ),
            "subscriptionPlans", Map.of(
                "NO_SUBSCRIBED", "Sin suscripción (por defecto al registrarse)",
                "BASIC", "Plan básico",
                "FAMILY", "Plan familiar",
                "PREMIUM", "Plan premium"
            ),
            "example", Map.of(
                "1_register", "POST http://localhost:8080/auth/register\nBody: {\"username\":\"user1\",\"email\":\"user1@example.com\",\"password\":\"password123\"}",
                "2_login", "POST http://localhost:8080/auth/login\nBody: {\"username\":\"user1\",\"password\":\"password123\"}\nResponse: {\"token\":\"eyJ...\",\"username\":\"user1\",\"message\":\"Login exitoso\"}",
                "3_view_plans", "GET http://localhost:8080/subscription/plans\n(No requiere autenticación)",
                "4_subscribe", "POST http://localhost:8080/subscription/purchase\nHeader: Authorization: Bearer eyJ...\nBody: {\"subscription\":\"PREMIUM\",\"cardHolderName\":\"John Doe\",\"cardNumber\":\"4111111111111111\"}",
                "5_comment", "POST http://localhost:8080/comments\nBody: {\"email\":\"user@example.com\",\"text\":\"Excelente página!\"}"
            )
        );
    }
}