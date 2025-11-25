package com.example.FruitseasonBackend.controller;

import com.example.FruitseasonBackend.model.entity.Order;
import com.example.FruitseasonBackend.service.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * OrderController - Controlador de pedidos
 * 
 * Endpoints:
 * - POST /orders/checkout - Crear pedido desde el carrito
 * - GET /orders - Obtener pedidos del usuario
 * - GET /orders/{orderNumber} - Obtener detalle de un pedido
 * - GET /orders/all - Obtener todos los pedidos (admin)
 */
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // ============= DTOs =============

    public record CheckoutRequest(
            @NotBlank(message = "El nombre del titular es obligatorio") String cardHolderName,

            @NotBlank(message = "El número de tarjeta es obligatorio") String cardNumber) {
    }

    public record ErrorResponse(String error, String message) {
    }

    // ============= Endpoints =============

    /**
     * POST /orders/checkout
     * Crea un pedido a partir del carrito actual
     * 
     * Flujo:
     * 1. Valida el carrito (plan seleccionado, frutas mínimas)
     * 2. Valida datos de pago
     * 3. Crea el pedido
     * 4. Actualiza la suscripción del usuario
     * 5. Guarda método de pago
     * 6. Limpia el carrito
     */
    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(
            @Valid @RequestBody CheckoutRequest req,
            Principal principal) {
        try {
            String username = principal.getName();

            Order order = orderService.createOrderFromCart(
                    username,
                    req.cardHolderName(),
                    req.cardNumber());

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "Pedido creado exitosamente",
                    "orderId", order.getId(),
                    "orderNumber", order.getOrderNumber(),
                    "plan", order.getPlan().toString(),
                    "fruitsCount", order.getFruitsAsList().size(),
                    "orderDate", order.getOrderDate().format(DateTimeFormatter.ISO_DATE_TIME),
                    "status", order.getStatus()));

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("VALIDATION_ERROR", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("SERVER_ERROR", "Error al procesar el pedido"));
        }
    }

    /**
     * GET /orders
     * Obtiene todos los pedidos del usuario autenticado
     */
    @GetMapping
    public ResponseEntity<?> getUserOrders(Principal principal) {
        try {
            String username = principal.getName();
            List<Order> orders = orderService.getUserOrders(username);

            List<Map<String, Object>> orderList = orders.stream()
                    .map(this::orderToMap)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                    "orders", orderList,
                    "total", orderList.size()));

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("SERVER_ERROR", "Error al obtener los pedidos"));
        }
    }

    /**
     * GET /orders/{orderNumber}
     * Obtiene el detalle de un pedido específico
     */
    @GetMapping("/{orderNumber}")
    public ResponseEntity<?> getOrderByNumber(
            @PathVariable String orderNumber,
            Principal principal) {
        try {
            Order order = orderService.getOrderByNumber(orderNumber);

            // Verifica que el pedido pertenezca al usuario autenticado
            if (!order.getUser().getUsername().equals(principal.getName())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse("FORBIDDEN", "No tiene permisos para ver este pedido"));
            }

            return ResponseEntity.ok(orderToMap(order));

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("NOT_FOUND", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("SERVER_ERROR", "Error al obtener el pedido"));
        }
    }

    /**
     * GET /orders/all
     * Obtiene todos los pedidos del sistema (solo admin)
     * Nota: Requeriría agregar validación de rol ADMIN
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllOrders() {
        try {
            List<Order> orders = orderService.getAllOrders();

            List<Map<String, Object>> orderList = orders.stream()
                    .map(this::orderToMap)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                    "orders", orderList,
                    "total", orderList.size()));

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("SERVER_ERROR", "Error al obtener los pedidos"));
        }
    }

    // ============= Helpers =============

    /**
     * Convierte un Order a Map para respuesta JSON
     */
    private Map<String, Object> orderToMap(Order order) {
        return Map.of(
                "id", order.getId(),
                "orderNumber", order.getOrderNumber(),
                "plan", order.getPlan().toString(),
                "fruits", order.getFruitsAsList().stream()
                        .map(fruit -> Map.of(
                                "type", fruit.name(),
                                "name", fruit.getDisplayName(),
                                "category", fruit.getCategory()))
                        .collect(Collectors.toList()),
                "fruitsCount", order.getFruitsAsList().size(),
                "cardHolderName", order.getCardHolderName(),
                "cardLast4", order.getCardLast4(),
                "orderDate", order.getOrderDate().format(DateTimeFormatter.ISO_DATE_TIME),
                "status", order.getStatus(),
                "username", order.getUser().getUsername());
    }
}
