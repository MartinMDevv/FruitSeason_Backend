package com.example.FruitseasonBackend.repository;

import com.example.FruitseasonBackend.model.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * CartItemRepository - Repositorio para items del carrito
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
