package com.example.FruitseasonBackend.service;

import com.example.FruitseasonBackend.model.entity.*;
import com.example.FruitseasonBackend.repository.CartItemRepository;
import com.example.FruitseasonBackend.repository.CartRepository;
import com.example.FruitseasonBackend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CartService - Servicio de lógica de negocio para carritos
 * 
 * Responsabilidades:
 * - Crear y gestionar carritos de usuarios
 * - Agregar/remover frutas del carrito
 * - Validar selección de frutas según el plan
 * - Garantizar que no se repitan frutas
 */
@Service
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    public CartService(CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
    }

    /**
     * Obtiene o crea el carrito de un usuario
     */
    public Cart getOrCreateCart(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart(user);
                    return cartRepository.save(newCart);
                });
    }

    /**
     * Selecciona un plan para el carrito
     */
    public Cart selectPlan(String username, SubscriptionPlan plan) {
        if (plan == null || plan == SubscriptionPlan.NO_SUBSCRIBED) {
            throw new IllegalArgumentException("Debe seleccionar un plan válido (BASIC, FAMILY o PREMIUM)");
        }

        Cart cart = getOrCreateCart(username);

        // Si cambia el plan, limpia las frutas anteriores
        if (cart.getSelectedPlan() != plan) {
            cart.clearItems();
        }

        cart.setSelectedPlan(plan);
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    /**
     * Agrega una fruta al carrito
     */
    public Cart addFruit(String username, FruitType fruit) {
        Cart cart = getOrCreateCart(username);

        // Verifica que haya un plan seleccionado
        if (cart.getSelectedPlan() == null || cart.getSelectedPlan() == SubscriptionPlan.NO_SUBSCRIBED) {
            throw new IllegalArgumentException("Debe seleccionar un plan antes de agregar frutas");
        }

        // Verifica que no se exceda el límite
        int maxFruits = cart.getRequiredFruitCount();
        if (cart.getItems().size() >= maxFruits) {
            throw new IllegalArgumentException(
                    String.format("El plan %s solo permite %d frutas",
                            cart.getSelectedPlan(), maxFruits));
        }

        // Verifica que la fruta no esté ya en el carrito
        boolean fruitExists = cart.getItems().stream()
                .anyMatch(item -> item.getFruitType() == fruit);

        if (fruitExists) {
            throw new IllegalArgumentException(
                    String.format("La fruta %s ya está en el carrito", fruit.getDisplayName()));
        }

        // Agrega la fruta
        CartItem newItem = new CartItem(cart, fruit);
        cart.addItem(newItem);

        return cartRepository.save(cart);
    }

    /**
     * Remueve una fruta del carrito
     */
    public Cart removeFruit(String username, FruitType fruit) {
        Cart cart = getOrCreateCart(username);

        CartItem itemToRemove = cart.getItems().stream()
                .filter(item -> item.getFruitType() == fruit)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("La fruta %s no está en el carrito", fruit.getDisplayName())));

        cart.removeItem(itemToRemove);
        return cartRepository.save(cart);
    }

    /**
     * Limpia el carrito completamente
     */
    public Cart clearCart(String username) {
        Cart cart = getOrCreateCart(username);
        cart.clearItems();
        cart.setSelectedPlan(null);
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    /**
     * Obtiene el carrito actual del usuario
     */
    public Cart getCart(String username) {
        return getOrCreateCart(username);
    }

    /**
     * Valida si el carrito está listo para checkout
     */
    public void validateCartForCheckout(Cart cart) {
        if (cart.getSelectedPlan() == null || cart.getSelectedPlan() == SubscriptionPlan.NO_SUBSCRIBED) {
            throw new IllegalArgumentException("Debe seleccionar un plan de suscripción");
        }

        int required = cart.getRequiredFruitCount();
        int current = cart.getItems().size();

        if (current < required) {
            throw new IllegalArgumentException(
                    String.format("El plan %s requiere %d frutas, pero solo tiene %d seleccionadas",
                            cart.getSelectedPlan(), required, current));
        }
    }

    /**
     * Obtiene las frutas del carrito como lista de FruitType
     */
    public List<FruitType> getCartFruits(Cart cart) {
        return cart.getItems().stream()
                .map(CartItem::getFruitType)
                .collect(Collectors.toList());
    }
}
