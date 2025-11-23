package com.example.FruitseasonBackend.controller;

import com.example.FruitseasonBackend.model.entity.Comment;
import com.example.FruitseasonBackend.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    public static class CommentRequest {
        public String email;
        public String text;
    }

    @PostMapping
    public ResponseEntity<?> createComment(@RequestBody CommentRequest req) {
        if (req.email == null || req.text == null) {
            return ResponseEntity.badRequest().body("email and text are required");
        }
        Comment c = new Comment(req.email, req.text);
        Comment saved = commentRepository.save(c);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public List<Comment> listComments() {
        return commentRepository.findAll();
    }
}
