package com.example.FruitseasonBackend.controller;

import com.example.FruitseasonBackend.model.entity.User;
import com.example.FruitseasonBackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    /*
     * AuthController
     *
     * Controlador que expone endpoints de autenticación/registro:
     * - POST /auth/register : registra un nuevo usuario (acepta opcionalmente `subscription`)
     * - POST /auth/login    : verifica credenciales y responde éxito/fallo
     */

    @Autowired
    private UserService userService;

    public static class RegisterRequest {
        public String username;
        public String email;
        public String password;
    }


    public static class LoginRequest {
        public String username;
        public String password;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        try {
            User u = userService.register(req.username, req.email, req.password);
            return ResponseEntity.ok("User registered: " + u.getUsername());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body("Error: " + ex.getMessage());
        }
    }

    public static class SubscribeRequest {
        public String username;
        public String password; // confirmar identidad antes de comprar
        public String subscription; // target plan: NO_SUBSCRIBED, BASIC, FAMILY, PREMIUM
    }

    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@RequestBody SubscribeRequest req) {
        try {
            User updated = userService.purchaseSubscription(req.username, req.password, req.subscription);
            return ResponseEntity.ok("Subscription updated: " + updated.getSubscription());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body("Error: " + ex.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        boolean ok = userService.login(req.username, req.password);
        if (ok) return ResponseEntity.ok("Login successful");
        return ResponseEntity.status(401).body("Invalid credentials");
    }
}
