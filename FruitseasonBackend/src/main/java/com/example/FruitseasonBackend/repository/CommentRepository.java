package com.example.FruitseasonBackend.repository;

import com.example.FruitseasonBackend.model.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
