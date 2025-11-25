package com.example.FruitseasonBackend.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * PaymentMethod
 *
 * Representa un método de pago vinculado a un usuario.
 * NOTA DE SEGURIDAD: no se debe almacenar el número completo de la tarjeta.
 * Aquí almacenamos sólo la versión enmascarada y los últimos 4 dígitos.
 */
@Entity
public class PaymentMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cardHolderName;

    // formato enmascarado, por ejemplo "**** **** **** 1234"
    private String maskedNumber;

    // últimos 4 dígitos para referencia
    private String last4;

    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    private User user;

    public PaymentMethod() {}

    public PaymentMethod(String cardHolderName, String maskedNumber, String last4, User user) {
        this.cardHolderName = cardHolderName;
        this.maskedNumber = maskedNumber;
        this.last4 = last4;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getMaskedNumber() {
        return maskedNumber;
    }

    public void setMaskedNumber(String maskedNumber) {
        this.maskedNumber = maskedNumber;
    }

    public String getLast4() {
        return last4;
    }

    public void setLast4(String last4) {
        this.last4 = last4;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
