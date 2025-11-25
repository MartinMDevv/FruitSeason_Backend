package com.example.FruitseasonBackend.service;

import com.example.FruitseasonBackend.model.entity.User;
import com.example.FruitseasonBackend.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;

/**
 * UserDetailsServiceImpl - Implementación de UserDetailsService para Spring Security
 * 
 * Responsabilidad:
 * - Cargar datos del usuario desde la BD para autenticación
 * - Convertir entidad User a UserDetails de Spring Security
 * - Asignar roles/autoridades al usuario
 */
@Service
@Transactional(readOnly = true)
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Carga un usuario por username para autenticación
     * 
     * @param username - Nombre de usuario
     * @return UserDetails con credenciales y autoridades
     * @throws UsernameNotFoundException si el usuario no existe
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
        
        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());
        
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singleton(authority)
        );
    }
}