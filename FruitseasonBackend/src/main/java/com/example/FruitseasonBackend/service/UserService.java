package com.example.FruitseasonBackend.service;

import com.example.FruitseasonBackend.model.entity.PaymentMethod;
import com.example.FruitseasonBackend.model.entity.SubscriptionPlan;
import com.example.FruitseasonBackend.model.entity.User;
import com.example.FruitseasonBackend.repository.PaymentMethodRepository;
import com.example.FruitseasonBackend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserService - Lógica de negocio para usuarios
 * 
 * Responsabilidades:
 * - Registro de usuarios con validación y hash de contraseña
 * - Compra/actualización de suscripciones
 * - Validación de tarjetas con algoritmo de Luhn
 * - Almacenamiento seguro de métodos de pago (solo últimos 4 dígitos)
 */
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PaymentMethodRepository paymentMethodRepository;

    public UserService(UserRepository userRepository, 
                      PasswordEncoder passwordEncoder,
                      PaymentMethodRepository paymentMethodRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.paymentMethodRepository = paymentMethodRepository;
    }

    /**
     * Registra un nuevo usuario
     * 
     * @param username - Nombre de usuario único
     * @param email - Email único
     * @param rawPassword - Contraseña en texto plano (se hasheará con BCrypt)
     * @return Usuario creado con suscripción NO_SUBSCRIBED
     * @throws IllegalArgumentException si datos inválidos o usuario ya existe
     */
    public User register(String username, String email, String rawPassword) {
        // Validaciones básicas
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("El nombre de usuario es obligatorio");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }
        if (rawPassword.length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres");
        }

        // Verifica username único
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso");
        }

        // Verifica email único
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        // Hashea la contraseña con BCrypt
        String hashedPassword = passwordEncoder.encode(rawPassword);
        
        // Crea el usuario con suscripción NO_SUBSCRIBED por defecto
        User user = new User(username, email, hashedPassword);
        return userRepository.save(user);
    }

    /**
     * Compra o actualiza la suscripción de un usuario
     * 
     * IMPORTANTE: Este método valida y almacena datos de tarjeta de forma básica.
     * En producción, usar servicios de pago como Stripe, PayPal, etc.
     * 
     * @param username - Usuario autenticado
     * @param subscriptionPlanStr - Plan: NO_SUBSCRIBED, BASIC, FAMILY, PREMIUM
     * @param cardHolderName - Nombre del titular de la tarjeta
     * @param cardNumber - Número de tarjeta (se valida con Luhn, NO se almacena completo)
     * @return Usuario con suscripción actualizada
     * @throws IllegalArgumentException si datos inválidos
     */
    public User purchaseSubscription(String username, String subscriptionPlanStr, 
                                    String cardHolderName, String cardNumber) {
        // Validación de usuario
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Usuario requerido");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Validación de datos de tarjeta
        if (cardHolderName == null || cardHolderName.isBlank()) {
            throw new IllegalArgumentException("El nombre del titular es obligatorio");
        }
        if (cardNumber == null || cardNumber.isBlank()) {
            throw new IllegalArgumentException("El número de tarjeta es obligatorio");
        }

        // Limpia y valida el número de tarjeta
        String digits = cardNumber.replaceAll("\\D", "");
        if (!isValidCardNumber(digits)) {
            throw new IllegalArgumentException("Número de tarjeta inválido");
        }

        // Valida el plan de suscripción
        SubscriptionPlan plan;
        try {
            plan = SubscriptionPlan.valueOf(subscriptionPlanStr.trim().toUpperCase());
        } catch (Exception ex) {
            throw new IllegalArgumentException("Plan de suscripción inválido. Opciones: NO_SUBSCRIBED, BASIC, FAMILY, PREMIUM");
        }

        // Extrae últimos 4 dígitos y crea versión enmascarada
        String last4 = digits.length() >= 4 ? digits.substring(digits.length() - 4) : digits;
        String maskedNumber = "**** **** **** " + last4;

        // Guarda el método de pago (solo datos enmascarados)
        PaymentMethod paymentMethod = new PaymentMethod(cardHolderName, maskedNumber, last4, user);
        paymentMethodRepository.save(paymentMethod);

        // Actualiza la suscripción del usuario
        user.setSubscription(plan);
        return userRepository.save(user);
    }

    /**
     * Algoritmo de Luhn para validar números de tarjeta
     * 
     * Valida que el número de tarjeta sea matemáticamente válido
     * (no verifica que la tarjeta exista o tenga fondos)
     * 
     * @param number - Número de tarjeta sin espacios ni guiones
     * @return true si pasa la validación de Luhn
     */
    private boolean isValidCardNumber(String number) {
        if (number == null || number.length() < 12 || number.length() > 19) {
            return false;
        }

        int sum = 0;
        boolean alternate = false;
        
        // Recorre de derecha a izquierda
        for (int i = number.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(number.charAt(i));
            
            if (digit < 0 || digit > 9) {
                return false; // Carácter no numérico
            }
            
            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit = (digit % 10) + 1;
                }
            }
            
            sum += digit;
            alternate = !alternate;
        }
        
        return (sum % 10 == 0);
    }
}