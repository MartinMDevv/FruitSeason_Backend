package com.example.FruitseasonBackend.repository;

import com.example.FruitseasonBackend.model.entity.PaymentMethod;
import com.example.FruitseasonBackend.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

/**
 * PaymentMethodRepository - Acceso a datos de métodos de pago
 */
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
    
    List<PaymentMethod> findByUserId(Long userId);
    
    List<PaymentMethod> findByUser(User user);
    
    // Verificar si un usuario ya tiene una tarjeta registrada con los últimos 4 dígitos
    Optional<PaymentMethod> findByUserAndLast4(User user, String last4);
}