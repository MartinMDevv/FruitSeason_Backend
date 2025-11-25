package com.example.FruitseasonBackend.model.entity;

/**
 * SubscriptionPlan - Planes de suscripción disponibles
 * 
 * Niveles:
 * - NO_SUBSCRIBED: Usuario registrado sin suscripción activa
 * - BASIC: Plan básico
 * - FAMILY: Plan familiar
 * - PREMIUM: Plan premium
 */
public enum SubscriptionPlan {
    NO_SUBSCRIBED,
    BASIC,
    FAMILY,
    PREMIUM
}