package com.example.FruitseasonBackend.controller;

import com.example.FruitseasonBackend.model.entity.Cart;
import com.example.FruitseasonBackend.model.entity.FruitType;
import com.example.FruitseasonBackend.model.entity.SubscriptionPlan;
import com.example.FruitseasonBackend.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * CartController - Controlador de carrito de compras
 * 
 * Endpoints:
 * - GET /cart - Obtener carrito actual
 * - POST /cart/select-plan - Seleccionar plan de suscripción
 * - POST /cart/add-fruit - Agregar fruta al carrito
 * - DELETE /cart/remove-fruit/{fruit} - Remover fruta del carrito
 * - DELETE /cart/clear - Limpiar carrito
 * - GET /cart/available-fruits - Listar frutas disponibles
 */
@Tag(name = "Carrito", description = "Gestión del carrito de compras y selección de frutas")
@SecurityRequirement(name = "bearer-jwt")
@RestController
@RequestMapping("/cart")
public class CartController {

        private final CartService cartService;

        public CartController(CartService cartService) {
                this.cartService = cartService;
        }

        // ============= DTOs =============

        public record SelectPlanRequest(
                        @NotBlank(message = "El plan es obligatorio") String plan) {
        }

        public record AddFruitRequest(
                        @NotBlank(message = "La fruta es obligatoria") String fruit) {
        }

        public record ErrorResponse(String error, String message) {
        }

        // ============= Endpoints =============

        /**
         * GET /cart
         * Obtiene el carrito actual del usuario
         */
        @Operation(summary = "Obtener carrito actual", description = "Retorna el carrito del usuario con plan seleccionado y frutas agregadas")
        @ApiResponse(responseCode = "200", description = "Carrito del usuario")
        @GetMapping
        public ResponseEntity<?> getCart(Principal principal) {
                try {
                        String username = principal.getName();
                        Cart cart = cartService.getCart(username);

                        return ResponseEntity.ok(Map.of(
                                        "id", cart.getId(),
                                        "selectedPlan",
                                        cart.getSelectedPlan() != null ? cart.getSelectedPlan().toString() : null,
                                        "requiredFruits", cart.getRequiredFruitCount(),
                                        "selectedFruits", cart.getItems().stream()
                                                        .map(item -> Map.of(
                                                                        "type", item.getFruitType().name(),
                                                                        "name", item.getFruitType().getDisplayName(),
                                                                        "category", item.getFruitType().getCategory()))
                                                        .collect(Collectors.toList()),
                                        "selectedFruitsCount", cart.getItems().size(),
                                        "isComplete", cart.hasRequiredFruits()));

                } catch (Exception ex) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ErrorResponse("SERVER_ERROR", "Error al obtener el carrito"));
                }
        }

        /**
         * POST /cart/select-plan
         * Selecciona un plan de suscripción para el carrito
         */
        @Operation(summary = "Seleccionar plan de suscripción", description = "Selecciona un plan (BASIC, FAMILY, PREMIUM). Si cambia el plan, se limpian las frutas del carrito.")
        @ApiResponse(responseCode = "200", description = "Plan seleccionado exitosamente")
        @PostMapping("/select-plan")
        public ResponseEntity<?> selectPlan(
                        @Valid @RequestBody SelectPlanRequest req,
                        Principal principal) {
                try {
                        String username = principal.getName();
                        SubscriptionPlan plan = SubscriptionPlan.valueOf(req.plan().toUpperCase());

                        Cart cart = cartService.selectPlan(username, plan);

                        return ResponseEntity.ok(Map.of(
                                        "message", "Plan seleccionado exitosamente",
                                        "plan", cart.getSelectedPlan().toString(),
                                        "requiredFruits", cart.getRequiredFruitCount()));

                } catch (IllegalArgumentException ex) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(new ErrorResponse("VALIDATION_ERROR", ex.getMessage()));
                } catch (Exception ex) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ErrorResponse("SERVER_ERROR", "Error al seleccionar el plan"));
                }
        }

        /**
         * POST /cart/add-fruit
         * Agrega una fruta al carrito
         */
        @Operation(summary = "Agregar fruta al carrito", description = "Agrega una fruta al carrito. No se pueden agregar frutas duplicadas ni exceder el límite del plan.")
        @ApiResponse(responseCode = "200", description = "Fruta agregada exitosamente")
        @PostMapping("/add-fruit")
        public ResponseEntity<?> addFruit(
                        @Valid @RequestBody AddFruitRequest req,
                        Principal principal) {
                try {
                        String username = principal.getName();
                        FruitType fruit = FruitType.valueOf(req.fruit().toUpperCase());

                        Cart cart = cartService.addFruit(username, fruit);

                        return ResponseEntity.ok(Map.of(
                                        "message", "Fruta agregada exitosamente",
                                        "fruit", fruit.getDisplayName(),
                                        "selectedFruitsCount", cart.getItems().size(),
                                        "requiredFruits", cart.getRequiredFruitCount(),
                                        "isComplete", cart.hasRequiredFruits()));

                } catch (IllegalArgumentException ex) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(new ErrorResponse("VALIDATION_ERROR", ex.getMessage()));
                } catch (Exception ex) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ErrorResponse("SERVER_ERROR", "Error al agregar la fruta"));
                }
        }

        /**
         * DELETE /cart/remove-fruit/{fruit}
         * Remueve una fruta del carrito
         */
        @Operation(summary = "Remover fruta del carrito", description = "Elimina una fruta específica del carrito")
        @ApiResponse(responseCode = "200", description = "Fruta removida exitosamente")
        @DeleteMapping("/remove-fruit/{fruit}")
        public ResponseEntity<?> removeFruit(
                        @PathVariable String fruit,
                        Principal principal) {
                try {
                        String username = principal.getName();
                        FruitType fruitType = FruitType.valueOf(fruit.toUpperCase());

                        Cart cart = cartService.removeFruit(username, fruitType);

                        return ResponseEntity.ok(Map.of(
                                        "message", "Fruta removida exitosamente",
                                        "selectedFruitsCount", cart.getItems().size(),
                                        "requiredFruits", cart.getRequiredFruitCount()));

                } catch (IllegalArgumentException ex) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(new ErrorResponse("VALIDATION_ERROR", ex.getMessage()));
                } catch (Exception ex) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ErrorResponse("SERVER_ERROR", "Error al remover la fruta"));
                }
        }

        /**
         * DELETE /cart/clear
         * Limpia completamente el carrito
         */
        @Operation(summary = "Limpiar carrito", description = "Elimina todas las frutas y el plan seleccionado del carrito")
        @ApiResponse(responseCode = "200", description = "Carrito limpiado")
        @DeleteMapping("/clear")
        public ResponseEntity<?> clearCart(Principal principal) {
                try {
                        String username = principal.getName();
                        cartService.clearCart(username);

                        return ResponseEntity.ok(Map.of(
                                        "message", "Carrito limpiado exitosamente"));

                } catch (Exception ex) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ErrorResponse("SERVER_ERROR", "Error al limpiar el carrito"));
                }
        }

        /**
         * GET /cart/available-fruits
         * Lista todas las frutas disponibles (público)
         */
        @Operation(summary = "Listar frutas disponibles", description = "Retorna todas las frutas que se pueden agregar al carrito")
        @ApiResponse(responseCode = "200", description = "Lista de frutas disponibles")
        @GetMapping("/available-fruits")
        public ResponseEntity<?> getAvailableFruits() {
                List<Map<String, String>> fruits = Arrays.stream(FruitType.values())
                                .map(fruit -> Map.of(
                                                "type", fruit.name(),
                                                "name", fruit.getDisplayName(),
                                                "category", fruit.getCategory()))
                                .collect(Collectors.toList());

                return ResponseEntity.ok(Map.of(
                                "fruits", fruits,
                                "total", fruits.size()));
        }
}
