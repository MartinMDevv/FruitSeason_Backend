package com.example.FruitseasonBackend.repository;

import com.example.FruitseasonBackend.model.entity.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * CommentRepository - Acceso a datos de comentarios
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    // Obtener los N comentarios más recientes
    @Query("SELECT c FROM Comment c ORDER BY c.createdAt DESC")
    List<Comment> findRecent(Pageable pageable);
    
    // Buscar comentarios por email (opcional, para auditoría)
    List<Comment> findByEmail(String email);
}