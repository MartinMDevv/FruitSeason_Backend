package com.example.FruitseasonBackend.service;

import com.example.FruitseasonBackend.model.entity.Comment;
import com.example.FruitseasonBackend.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * CommentService
 *
 * Servicio que centraliza la lógica de acceso y manipulación de comentarios.
 * - create: valida y persiste un comentario
 * - listAll: devuelve todos los comentarios
 */
@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public Comment create(String email, String text) {
        if (email == null || email.isBlank()) throw new IllegalArgumentException("email required");
        if (text == null || text.isBlank()) throw new IllegalArgumentException("text required");

        Comment c = new Comment(email, text);
        return commentRepository.save(c);
    }

    public List<Comment> listAll() {
        return commentRepository.findAll();
    }
}
