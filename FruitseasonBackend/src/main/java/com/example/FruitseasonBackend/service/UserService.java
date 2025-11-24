package com.example.FruitseasonBackend.service;

import com.example.FruitseasonBackend.model.entity.User;
import com.example.FruitseasonBackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    /*
     * UserService
     *
     * Servicio que implementa la lógica de negocio para usuarios:
     * - register(...) : valida y crea usuarios (hash de contraseña, asigna plan)
     * - login(...)    : verifica credenciales comparando hashes
     *
     * Depende de `UserRepository` y de un `PasswordEncoder` (BCrypt).
     */

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Registrar un usuario.
     *
     * La lógica de suscripción está centralizada en la entidad `User` y
     * no se asignan planes desde el registro. Al crear un usuario, su
     * `subscription` será por defecto `NO_SUBSCRIBED`.
     *
     * @param username nombre de usuario (requerido)
     * @param email correo (requerido)
     * @param rawPassword contraseña en texto plano (requerido)
     * @return usuario creado persistido
     */
    public User register(String username, String email, String rawPassword) {
        if (username == null || username.isBlank()) throw new IllegalArgumentException("username required");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("email required");
        if (rawPassword == null || rawPassword.isBlank()) throw new IllegalArgumentException("password required");

        Optional<User> byUser = userRepository.findByUsername(username);
        if (byUser.isPresent()) throw new IllegalArgumentException("username already exists");

        Optional<User> byEmail = userRepository.findByEmail(email);
        if (byEmail.isPresent()) throw new IllegalArgumentException("email already exists");

        String encoded = passwordEncoder.encode(rawPassword);
        User u = new User(username, email, encoded);
        return userRepository.save(u);
    }

    /**
     * Compra/actualiza la suscripción de un usuario.
     *
     * Requiere las credenciales (username + rawPassword) para confirmar la operación.
     * @param username usuario
     * @param rawPassword contraseña en texto plano para validar
     * @param subscriptionPlanStr nombre del plan (NO_SUBSCRIBED, BASIC, FAMILY, PREMIUM)
     * @return usuario con la suscripción actualizada
     */
    public User purchaseSubscription(String username, String rawPassword, String subscriptionPlanStr) {
        if (username == null || rawPassword == null) throw new IllegalArgumentException("username and password required");

        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) throw new IllegalArgumentException("user not found");

        User user = userOpt.get();
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("invalid credentials");
        }

        com.example.FruitseasonBackend.model.entity.SubscriptionPlan plan;
        try {
            plan = com.example.FruitseasonBackend.model.entity.SubscriptionPlan.valueOf(subscriptionPlanStr.trim().toUpperCase());
        } catch (Exception ex) {
            throw new IllegalArgumentException("invalid subscription plan");
        }

        user.setSubscription(plan);
        return userRepository.save(user);
    }

    public boolean login(String username, String rawPassword) {
        if (username == null || rawPassword == null) return false;
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) return false;
        User user = userOpt.get();
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }
}
