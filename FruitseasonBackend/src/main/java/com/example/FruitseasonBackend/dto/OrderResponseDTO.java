package com.example.FruitseasonBackend.dto;

import com.example.FruitseasonBackend.model.entity.Order;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO para respuestas de pedidos
 * Evita exponer la entidad completa y previene problemas de serialización
 */
public record OrderResponseDTO(
        Long id,
        String orderNumber,
        String plan,
        List<FruitDTO> fruits,
        int fruitsCount,
        String cardHolderName,
        String cardLast4,
        LocalDateTime orderDate,
        String status,
        String username) {
    public static OrderResponseDTO fromOrder(Order order) {
        return new OrderResponseDTO(
                order.getId(),
                order.getOrderNumber(),
                order.getPlan().toString(),
                order.getFruitsAsList().stream()
                        .map(FruitDTO::fromFruitType)
                        .collect(Collectors.toList()),
                order.getFruitsAsList().size(),
                order.getCardHolderName(),
                order.getCardLast4(),
                order.getOrderDate(),
                order.getStatus(),
                order.getUser().getUsername());
    }
}
