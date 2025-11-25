package com.example.FruitseasonBackend.repository;

import com.example.FruitseasonBackend.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * OrderRepository - Repositorio para pedidos
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Busca pedidos por ID de usuario
     */
    List<Order> findByUserId(Long userId);

    /**
     * Busca pedidos por ID de usuario ordenados por fecha descendente
     */
    List<Order> findByUserIdOrderByOrderDateDesc(Long userId);

    /**
     * Busca un pedido por su número único
     */
    Optional<Order> findByOrderNumber(String orderNumber);
}
