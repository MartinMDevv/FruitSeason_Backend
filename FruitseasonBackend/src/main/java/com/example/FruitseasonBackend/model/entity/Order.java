package com.example.FruitseasonBackend.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Order - Pedido completado
 * 
 * Responsabilidades:
 * - Almacenar información completa del pedido
 * - Guardar el plan de suscripción adquirido
 * - Registrar las frutas seleccionadas
 * - Almacenar datos de pago (enmascarados)
 * - Generar número de pedido único
 * 
 * Flujo:
 * 1. Usuario selecciona plan y frutas en el carrito
 * 2. Usuario proporciona datos de pago
 * 3. Se crea el pedido con toda la información
 * 4. Se limpia el carrito
 */
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 36)
    private String orderNumber; // UUID único

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionPlan plan;

    // Almacena las frutas seleccionadas como lista separada por comas
    @Column(nullable = false, length = 500)
    private String selectedFruits;

    // Información de pago (datos enmascarados por seguridad)
    @Column(nullable = false, length = 100)
    private String cardHolderName;

    @Column(nullable = false, length = 20)
    private String cardLast4;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime orderDate;

    @Column(nullable = false, length = 20)
    private String status = "COMPLETED"; // COMPLETED, PENDING, CANCELLED

    // Constructores
    public Order() {
        this.orderNumber = UUID.randomUUID().toString();
    }

    public Order(User user, SubscriptionPlan plan, List<FruitType> fruits,
            String cardHolderName, String cardLast4) {
        this();
        this.user = user;
        this.plan = plan;
        this.selectedFruits = String.join(",", fruits.stream()
                .map(FruitType::name)
                .toList());
        this.cardHolderName = cardHolderName;
        this.cardLast4 = cardLast4;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public SubscriptionPlan getPlan() {
        return plan;
    }

    public void setPlan(SubscriptionPlan plan) {
        this.plan = plan;
    }

    public String getSelectedFruits() {
        return selectedFruits;
    }

    public void setSelectedFruits(String selectedFruits) {
        this.selectedFruits = selectedFruits;
    }

    /**
     * Obtiene la lista de frutas como objetos FruitType
     */
    public List<FruitType> getFruitsAsList() {
        if (selectedFruits == null || selectedFruits.isEmpty()) {
            return new ArrayList<>();
        }
        String[] fruitNames = selectedFruits.split(",");
        List<FruitType> fruits = new ArrayList<>();
        for (String name : fruitNames) {
            try {
                fruits.add(FruitType.valueOf(name.trim()));
            } catch (IllegalArgumentException e) {
                // Ignora frutas inválidas
            }
        }
        return fruits;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getCardLast4() {
        return cardLast4;
    }

    public void setCardLast4(String cardLast4) {
        this.cardLast4 = cardLast4;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
