package com.example.FruitseasonBackend.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * User entity
 *
 * Se usa para persistir usuarios en la base de datos. Contiene:
 * - id: clave primaria
 * - username: nombre de usuario único
 * - email: correo electrónico único
 * - password: contraseña encriptada (BCrypt)
 * - subscription: plan de suscripción del usuario (enum SubscriptionPlan)
 *
 * Notas:
 * - La contraseña debe guardarse siempre en formato hash.
 * - El campo subscription se inicializa con `BASIC` si no se especifica.
 */
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String password;

    // Suscripción del usuario (por defecto: NO_SUBSCRIBED)
    private SubscriptionPlan subscription = SubscriptionPlan.NO_SUBSCRIBED;

    public User() {}

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.subscription = SubscriptionPlan.NO_SUBSCRIBED;
    }

    public User(String username, String email, String password, SubscriptionPlan subscription) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.subscription = subscription == null ? SubscriptionPlan.BASIC : subscription;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public SubscriptionPlan getSubscription() {
        return subscription;
    }

    public void setSubscription(SubscriptionPlan subscription) {
        this.subscription = subscription;
    }
}
