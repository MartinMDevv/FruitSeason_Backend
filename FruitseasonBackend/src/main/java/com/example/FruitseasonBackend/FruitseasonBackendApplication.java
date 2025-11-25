package com.example.FruitseasonBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * FruitseasonBackendApplication - Clase principal de la aplicación
 * 
 * Backend Spring Boot para sistema de suscripciones con:
 * - Autenticación JWT
 * - Gestión de usuarios y suscripciones
 * - Comentarios/testimonios anónimos
 * - API REST
 */
@SpringBootApplication
public class FruitseasonBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(FruitseasonBackendApplication.class, args);
	}
}