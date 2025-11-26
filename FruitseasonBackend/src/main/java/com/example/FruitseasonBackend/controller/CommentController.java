package com.example.FruitseasonBackend.controller;

import com.example.FruitseasonBackend.model.entity.Comment;
import com.example.FruitseasonBackend.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

/**
 * Controlador de Comentarios/Testimonios
 * 
 * Responsabilidades:
 * - Recibir opiniones anónimas de usuarios sobre la página
 * - Mostrar testimonios en el footer (público)
 * 
 * Características:
 * - NO requiere autenticación (comentarios anónimos)
 * - Validación de email y texto
 * - Sin moderación (confianza en los usuarios)
 * 
 * Endpoints:
 * - POST /comments - Crear comentario anónimo (público)
 * - GET /comments - Listar últimos comentarios para el footer (público)
 */
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    // Inyección por constructor (mejor práctica)
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // ============= DTOs (Data Transfer Objects) =============

    /**
     * DTO para crear comentario anónimo
     * 
     * Campos requeridos:
     * - email: Para identificar al usuario (no se muestra públicamente)
     * - text: Opinión del usuario sobre la página
     */
    public record CommentRequest(
            @NotBlank(message = "El email es obligatorio") @Email(message = "Email inválido") @Size(max = 100, message = "El email es demasiado largo") String email,

            @NotBlank(message = "El comentario no puede estar vacío") @Size(min = 10, max = 500, message = "El comentario debe tener entre 10 y 500 caracteres") String text) {
    }

    /**
     * DTO de respuesta para comentario
     * NO expone el email del usuario (privacidad)
     */
    public record CommentResponse(
            Long id,
            String text,
            String createdAt) {
        public static CommentResponse from(Comment comment) {
            return new CommentResponse(
                    comment.getId(),
                    comment.getText(),
                    comment.getCreatedAt() != null ? comment.getCreatedAt().toString() : null);
        }
    }

    /**
     * DTO para respuesta de error
     */
    public record ErrorResponse(String error, String message) {
    }

    // ============= Endpoints =============

    /**
     * POST /comments
     * Crea un comentario anónimo
     * 
     * @param req - Email y texto del comentario
     * @return ResponseEntity con el comentario creado o error
     * 
     *         Flujo:
     *         1. Valida email y texto
     *         2. Guarda el comentario en la BD
     *         3. Retorna confirmación (sin exponer el email)
     * 
     *         Acceso: Público (NO requiere autenticación)
     * 
     *         Ejemplo de uso desde el frontend:
     *         POST /comments
     *         {
     *         "email": "usuario@example.com",
     *         "text": "Excelente página, muy buenos productos!"
     *         }
     */
    @PostMapping
    public ResponseEntity<?> createComment(@Valid @RequestBody CommentRequest req) {
        try {
            // Crea el comentario (el email NO se muestra públicamente)
            Comment saved = commentService.create(req.email(), req.text());

            // Retorna solo el texto, no el email (privacidad)
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(CommentResponse.from(saved));

        } catch (IllegalArgumentException ex) {
            // Error de validación de negocio
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("VALIDATION_ERROR", ex.getMessage()));

        } catch (Exception ex) {
            // Error inesperado - no exponer detalles internos
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("SERVER_ERROR", "Error al procesar el comentario"));
        }
    }

    /**
     * GET /comments
     * Lista los comentarios para mostrar en el footer
     * 
     * Retorna los últimos comentarios ordenados por fecha (más recientes primero)
     * 
     * Acceso: Público (para mostrar en el footer de la página)
     * 
     * OPCIÓN 1: Retornar todos los comentarios (si son pocos)
     * OPCIÓN 2: Retornar solo los últimos N comentarios (recomendado)
     */
    @GetMapping
    public ResponseEntity<List<CommentResponse>> listComments() {
        try {
            // Obtiene todos los comentarios
            List<Comment> comments = commentService.listAll();

            // Mapea a DTOs (sin exponer emails)
            List<CommentResponse> response = comments.stream()
                    .map(CommentResponse::from)
                    .toList();

            return ResponseEntity.ok(response);

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /comments/recent
     * Lista solo los últimos N comentarios para el footer
     * 
     * @param limit - Cantidad máxima de comentarios a retornar (default: 10)
     * @return Lista de comentarios recientes
     * 
     *         Recomendado para el footer: mostrar solo los últimos 5-10 comentarios
     *         para no saturar la página
     * 
     *         Ejemplo de uso:
     *         - GET /comments/recent -> Últimos 10 comentarios
     *         - GET /comments/recent?limit=5 -> Últimos 5 comentarios
     */
    @GetMapping("/recent")
    public ResponseEntity<List<CommentResponse>> getRecentComments(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            // Limita la cantidad máxima para prevenir abuso
            if (limit > 50)
                limit = 50;
            if (limit < 1)
                limit = 10;

            // Obtiene los comentarios más recientes
            List<Comment> comments = commentService.findRecent(limit);

            // Mapea a DTOs
            List<CommentResponse> response = comments.stream()
                    .map(CommentResponse::from)
                    .toList();

            return ResponseEntity.ok(response);

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ============= NOTA SOBRE SEGURIDAD =============
    /*
     * PROTECCIÓN CONTRA SPAM:
     * 
     * Aunque no hay moderación, considera implementar:
     * 
     * 1. Rate Limiting: Limitar comentarios por IP
     * - Máximo 3 comentarios por hora por IP
     * - Usa @RateLimiter de Resilience4j
     * 
     * 2. Validación de Email Real:
     * - Opcional: Enviar email de confirmación
     * - Solo mostrar comentarios confirmados
     * 
     * 3. Filtro de Palabras Prohibidas:
     * - Lista básica de palabras ofensivas
     * - Rechazar comentarios que las contengan
     * 
     * 4. Longitud Mínima:
     * - Ya implementado: min 10 caracteres
     * - Evita comentarios tipo "aaa" o "123"
     */
}