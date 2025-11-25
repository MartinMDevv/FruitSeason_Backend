package com.example.FruitseasonBackend.service;

import com.example.FruitseasonBackend.model.entity.*;
import com.example.FruitseasonBackend.repository.OrderRepository;
import com.example.FruitseasonBackend.repository.PaymentMethodRepository;
import com.example.FruitseasonBackend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * OrderService - Servicio de lógica de negocio para pedidos
 * 
 * Responsabilidades:
 * - Crear pedidos a partir del carrito
 * - Validar datos de pago
 * - Actualizar suscripción del usuario
 * - Guardar método de pago
 * - Limpiar carrito después de compra exitosa
 */
@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final CartService cartService;

    public OrderService(OrderRepository orderRepository,
            UserRepository userRepository,
            PaymentMethodRepository paymentMethodRepository,
            CartService cartService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.cartService = cartService;
    }

    /**
     * Crea un pedido a partir del carrito del usuario
     * 
     * @param username       - Usuario autenticado
     * @param cardHolderName - Nombre del titular de tarjeta
     * @param cardNumber     - Número de tarjeta (se validará y solo se guardará
     *                       últimos 4 dígitos)
     * @return Pedido creado
     */
    public Order createOrderFromCart(String username, String cardHolderName, String cardNumber) {
        // Obtiene usuario
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Obtiene y valida carrito
        Cart cart = cartService.getCart(username);
        cartService.validateCartForCheckout(cart);

        // Valida datos de pago
        if (cardHolderName == null || cardHolderName.isBlank()) {
            throw new IllegalArgumentException("El nombre del titular es obligatorio");
        }
        if (cardNumber == null || cardNumber.isBlank()) {
            throw new IllegalArgumentException("El número de tarjeta es obligatorio");
        }

        // Limpia y valida el número de tarjeta
        String digits = cardNumber.replaceAll("\\D", "");
        if (!isValidCardNumber(digits)) {
            throw new IllegalArgumentException("Número de tarjeta inválido");
        }

        // Extrae últimos 4 dígitos
        String last4 = digits.length() >= 4 ? digits.substring(digits.length() - 4) : digits;

        // Crea el pedido
        List<FruitType> selectedFruits = cartService.getCartFruits(cart);
        Order order = new Order(user, cart.getSelectedPlan(), selectedFruits, cardHolderName, last4);
        order = orderRepository.save(order);

        // Guarda el método de pago
        String maskedNumber = "**** **** **** " + last4;
        PaymentMethod paymentMethod = new PaymentMethod(cardHolderName, maskedNumber, last4, user);
        paymentMethodRepository.save(paymentMethod);

        // Actualiza la suscripción del usuario
        user.setSubscription(cart.getSelectedPlan());
        userRepository.save(user);

        // Limpia el carrito
        cartService.clearCart(username);

        return order;
    }

    /**
     * Obtiene todos los pedidos de un usuario
     */
    public List<Order> getUserOrders(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        return orderRepository.findByUserIdOrderByOrderDateDesc(user.getId());
    }

    /**
     * Obtiene un pedido por su número
     */
    public Order getOrderByNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
    }

    /**
     * Obtiene todos los pedidos (admin)
     */
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    /**
     * Algoritmo de Luhn para validar números de tarjeta
     */
    private boolean isValidCardNumber(String number) {
        if (number == null || number.length() < 12 || number.length() > 19) {
            return false;
        }

        int sum = 0;
        boolean alternate = false;

        // Recorre de derecha a izquierda
        for (int i = number.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(number.charAt(i));

            if (digit < 0 || digit > 9) {
                return false;
            }

            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit = (digit % 10) + 1;
                }
            }

            sum += digit;
            alternate = !alternate;
        }

        return (sum % 10 == 0);
    }
}
