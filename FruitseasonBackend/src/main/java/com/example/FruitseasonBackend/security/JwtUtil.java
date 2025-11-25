package com.example.FruitseasonBackend.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;

/**
 * JwtUtil - Utilidad para generar y validar tokens JWT
 * 
 * Responsabilidades:
 * - Generar tokens JWT con username y expiración
 * - Validar tokens y extraer información
 * - Usar HMAC256 para firmar tokens
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration-ms}")
    private long jwtExpirationMs;

    /**
     * Genera un token JWT para un usuario
     * 
     * @param username - Nombre de usuario
     * @return Token JWT firmado
     */
    public String generateToken(String username) {
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret.getBytes());
        return JWT.create()
                .withSubject(username)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .sign(algorithm);
    }

    /**
     * Extrae el username del token
     * 
     * @param token - Token JWT
     * @return Username
     */
    public String extractUsername(String token) {
        DecodedJWT jwt = verifier().verify(token);
        return jwt.getSubject();
    }

    /**
     * Valida que el token sea válido y pertenezca al usuario
     * 
     * @param token - Token JWT
     * @param username - Username esperado
     * @return true si es válido
     */
    public boolean validateToken(String token, String username) {
        try {
            String extractedUsername = extractUsername(token);
            return extractedUsername.equals(username);
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Crea el verificador JWT con la clave secreta
     */
    private JWTVerifier verifier() {
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret.getBytes());
        return JWT.require(algorithm).build();
    }
}