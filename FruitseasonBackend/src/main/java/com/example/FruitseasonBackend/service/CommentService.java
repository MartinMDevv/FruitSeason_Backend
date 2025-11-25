package com.example.FruitseasonBackend.service;

import com.example.FruitseasonBackend.model.entity.Comment;
import com.example.FruitseasonBackend.repository.CommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * CommentService - Lógica de negocio para comentarios
 * 
 * Responsabilidades:
 * - Crear comentarios anónimos con validación
 * - Listar comentarios (todos o recientes)
 * - Obtener comentario por ID
 */
@Service
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    /**
     * Crea un comentario anónimo
     * 
     * @param email - Email del usuario
     * @param text - Texto del comentario
     * @return Comentario guardado
     */
    public Comment create(String email, String text) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("El texto es obligatorio");
        }
        if (text.length() < 10) {
            throw new IllegalArgumentException("El comentario debe tener al menos 10 caracteres");
        }
        if (text.length() > 500) {
            throw new IllegalArgumentException("El comentario no puede exceder 500 caracteres");
        }

        Comment comment = new Comment(email, text);
        return commentRepository.save(comment);
    }

    /**
     * Lista todos los comentarios ordenados por fecha (más recientes primero)
     */
    @Transactional(readOnly = true)
    public List<Comment> listAll() {
        return commentRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    /**
     * Obtiene los N comentarios más recientes para el footer
     * 
     * @param limit - Cantidad de comentarios a obtener
     * @return Lista de comentarios recientes
     */
    @Transactional(readOnly = true)
    public List<Comment> findRecent(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        return commentRepository.findRecent(pageable);
    }

    /**
     * Busca un comentario por ID
     */
    @Transactional(readOnly = true)
    public Optional<Comment> findById(Long id) {
        return commentRepository.findById(id);
    }

    /**
     * Lista comentarios con paginación
     */
    @Transactional(readOnly = true)
    public Page<Comment> listAll(Pageable pageable) {
        return commentRepository.findAll(pageable);
    }
}