package com.example.FruitseasonBackend.repository;

import com.example.FruitseasonBackend.model.entity.Cart;
import com.example.FruitseasonBackend.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * CartRepository - Repositorio para carritos de compra
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    /**
     * Busca el carrito de un usuario específico
     */
    Optional<Cart> findByUser(User user);

    /**
     * Busca el carrito por ID de usuario
     */
    Optional<Cart> findByUserId(Long userId);

    /**
     * Verifica si un usuario tiene un carrito
     */
    boolean existsByUserId(Long userId);
}
