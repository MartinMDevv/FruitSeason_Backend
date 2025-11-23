package com.example.FruitseasonBackend.service;

import com.example.FruitseasonBackend.model.entity.User;
import com.example.FruitseasonBackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User register(String username, String email, String rawPassword) {
        if (username == null || username.isBlank()) throw new IllegalArgumentException("username required");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("email required");
        if (rawPassword == null || rawPassword.isBlank()) throw new IllegalArgumentException("password required");

        Optional<User> byUser = userRepository.findByUsername(username);
        if (byUser.isPresent()) throw new IllegalArgumentException("username already exists");

        Optional<User> byEmail = userRepository.findByEmail(email);
        if (byEmail.isPresent()) throw new IllegalArgumentException("email already exists");

        String encoded = passwordEncoder.encode(rawPassword);
        User u = new User(username, email, encoded);
        return userRepository.save(u);
    }

    public boolean login(String username, String rawPassword) {
        if (username == null || rawPassword == null) return false;
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) return false;
        User user = userOpt.get();
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }
}
