package com.example.FruitseasonBackend.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Cart - Carrito de compras del usuario
 * 
 * Responsabilidades:
 * - Almacenar temporalmente el plan de suscripción seleccionado
 * - Gestionar los items (frutas seleccionadas) antes de la compra
 * - Validar que se cumplan los mínimos de frutas según el plan
 * 
 * Relación: Un usuario tiene un carrito, un carrito tiene múltiples items
 */
@Entity
@Table(name = "carts")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "selected_plan")
    private SubscriptionPlan selectedPlan;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Constructores
    public Cart() {
        this.updatedAt = LocalDateTime.now();
    }

    public Cart(User user) {
        this.user = user;
        this.updatedAt = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public SubscriptionPlan getSelectedPlan() {
        return selectedPlan;
    }

    public void setSelectedPlan(SubscriptionPlan selectedPlan) {
        this.selectedPlan = selectedPlan;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Métodos de utilidad
    public void addItem(CartItem item) {
        items.add(item);
        item.setCart(this);
        this.updatedAt = LocalDateTime.now();
    }

    public void removeItem(CartItem item) {
        items.remove(item);
        item.setCart(null);
        this.updatedAt = LocalDateTime.now();
    }

    public void clearItems() {
        items.clear();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Obtiene el mínimo de frutas requeridas según el plan seleccionado
     */
    public int getRequiredFruitCount() {
        if (selectedPlan == null)
            return 0;
        return switch (selectedPlan) {
            case BASIC -> 4;
            case FAMILY -> 8;
            case PREMIUM -> 12;
            default -> 0;
        };
    }

    /**
     * Valida si el carrito tiene la cantidad mínima de frutas
     */
    public boolean hasRequiredFruits() {
        return items.size() >= getRequiredFruitCount();
    }
}
