package com.example.FruitseasonBackend.controller;

import com.example.FruitseasonBackend.dto.OrderResponseDTO;
import com.example.FruitseasonBackend.model.entity.Order;
import com.example.FruitseasonBackend.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@Tag(name = "Pedidos", description = "Gestión de pedidos y checkout")
@SecurityRequirement(name = "bearer-jwt")
@RestController
@RequestMapping("/api/orders")
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
        @Operation(summary = "Crear pedido (Checkout)", description = "Crea un pedido a partir del carrito actual. Requiere que el carrito tenga un plan seleccionado y el número mínimo de frutas según el plan.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Ped ido creado exitosamente"),
                        @ApiResponse(responseCode = "400", description = "Carrito inválido o faltan datos"),
                        @ApiResponse(responseCode = "401", description = "No autenticado")
        })
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
        @Operation(summary = "Obtener mis pedidos", description = "Retorna todos los pedidos del usuario autenticado ordenados por fecha")
        @ApiResponse(responseCode = "200", description = "Lista de pedidos del usuario")
        @GetMapping
        public ResponseEntity<?> getUserOrders(Principal principal) {
                try {
                        String username = principal.getName();
                        List<Order> orders = orderService.getUserOrders(username);

                        List<OrderResponseDTO> orderList = orders.stream()
                                        .map(OrderResponseDTO::fromOrder)
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
        @Operation(summary = "Obtener detalle de pedido", description = "Retorna los detalles completos de un pedido específico. Solo accesible para el dueño del pedido.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Detalle del pedido"),
                        @ApiResponse(responseCode = "403", description = "No tiene permisos para ver este pedido"),
                        @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
        })
        @GetMapping("/{orderNumber}")
        public ResponseEntity<?> getOrderByNumber(
                        @Parameter(description = "Número único del pedido (UUID)") @PathVariable String orderNumber,
                        Principal principal) {
                try {
                        Order order = orderService.getOrderByNumber(orderNumber);

                        // Verifica que el pedido pertenezca al usuario autenticado
                        if (!order.getUser().getUsername().equals(principal.getName())) {
                                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                                .body(new ErrorResponse("FORBIDDEN",
                                                                "No tiene permisos para ver este pedido"));
                        }

                        return ResponseEntity.ok(OrderResponseDTO.fromOrder(order));

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
         * IMPORTANTE: Solo accesible para usuarios con rol ADMIN
         */
        @Operation(summary = "Obtener TODOS los pedidos (ADMIN)", description = "Retorna todos los pedidos del sistema. SOLO accesible para usuarios con rol ADMIN.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista completa de pedidos"),
                        @ApiResponse(responseCode = "403", description = "Acceso denegado - requiere rol ADMIN")
        })
        @PreAuthorize("hasRole('ADMIN')")
        @GetMapping("/all")
        public ResponseEntity<?> getAllOrders() {
                try {
                        List<Order> orders = orderService.getAllOrders();

                        List<OrderResponseDTO> orderList = orders.stream()
                                        .map(OrderResponseDTO::fromOrder)
                                        .collect(Collectors.toList());

                        return ResponseEntity.ok(Map.of(
                                        "orders", orderList,
                                        "total", orderList.size()));

                } catch (Exception ex) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ErrorResponse("SERVER_ERROR", "Error al obtener los pedidos"));
                }
        }

}
