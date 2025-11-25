package com.example.FruitseasonBackend.dto;

import com.example.FruitseasonBackend.model.entity.FruitType;

/**
 * DTO para representar una fruta en las respuestas de la API
 */
public record FruitDTO(
        String type,
        String name,
        String category) {
    public static FruitDTO fromFruitType(FruitType fruitType) {
        return new FruitDTO(
                fruitType.name(),
                fruitType.getDisplayName(),
                fruitType.getCategory());
    }
}
