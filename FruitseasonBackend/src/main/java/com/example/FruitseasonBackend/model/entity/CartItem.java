package com.example.FruitseasonBackend.model.entity;

import jakarta.persistence.*;

/**
 * CartItem - Item individual del carrito (fruta seleccionada)
 * 
 * Responsabilidades:
 * - Representar una fruta seleccionada en el carrito
 * - Garantizar que no se repitan frutas en el mismo carrito
 */
@Entity
@Table(name = "cart_items", uniqueConstraints = @UniqueConstraint(columnNames = { "cart_id", "fruit_type" }))
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @Enumerated(EnumType.STRING)
    @Column(name = "fruit_type", nullable = false)
    private FruitType fruitType;

    // Constructores
    public CartItem() {
    }

    public CartItem(Cart cart, FruitType fruitType) {
        this.cart = cart;
        this.fruitType = fruitType;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public FruitType getFruitType() {
        return fruitType;
    }

    public void setFruitType(FruitType fruitType) {
        this.fruitType = fruitType;
    }
}
