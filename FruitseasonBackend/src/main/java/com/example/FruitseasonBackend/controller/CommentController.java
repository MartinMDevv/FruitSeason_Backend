package com.example.FruitseasonBackend.controller;

import com.example.FruitseasonBackend.model.entity.Comment;
import com.example.FruitseasonBackend.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /*
     * CommentController
     *
     * Se encarga de operaciones CRUD simples sobre comentarios:
     * - POST /comments   -> crea un comentario (usa CommentService)
     * - GET  /comments   -> lista todos los comentarios
     *
     * La validación principal (email/text) está en el servicio.
     */

    public static class CommentRequest {
        public String email;
        public String text;
    }

    @PostMapping
    public ResponseEntity<?> createComment(@RequestBody CommentRequest req) {
        try {
            Comment saved = commentService.create(req.email, req.text);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping
    public List<Comment> listComments() {
        return commentService.listAll();
    }
}
