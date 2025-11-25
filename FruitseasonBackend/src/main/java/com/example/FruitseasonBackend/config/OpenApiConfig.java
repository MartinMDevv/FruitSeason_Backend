package com.example.FruitseasonBackend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de OpenAPI/Swagger
 * 
 * Proporciona documentación interactiva de la API en:
 * - Swagger UI: http://localhost:8080/swagger-ui.html
 * - OpenAPI JSON: http://localhost:8080/v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI fruitSeasonOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FruitSeason Backend API")
                        .description("API REST para sistema de suscripciones de cajas de frutas mensuales. " +
                                "Incluye autenticación JWT, gestión de carritos y pedidos.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Martin M Dev")
                                .url("https://github.com/MartinMDevv/FruitSeason_Backend"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de Desarrollo Local")))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Ingresa el token JWT obtenido del endpoint /auth/login\n\n" +
                                        "Ejemplo: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }
}
