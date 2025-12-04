# FruitSeason_Backend

Backend del proyecto Fruit Season, encargado de manejar toda la lógica y datos necesarios para la página web Fruit Season.

## Descripción del Proyecto

Este proyecto proporciona el backend para Fruit Season, permitiendo la compra y gestión de suscripciones a través de la web. Incluye autenticación de usuarios mediante JWT y expone sus funcionalidades mediante una API REST documentada en Swagger para facilitar las pruebas y desarrollo.

## Tecnologías y Lenguajes Usados

- **Java 21**: Lenguaje principal de desarrollo.
- **Spring Boot 4**: Framework para construcción de APIs web robustas y seguras.
- **Laragon**: Entorno de desarrollo local utilizado para la base de datos.
- **Swagger**: Herramienta para documentación y pruebas de endpoints de la API.
- **JWT (JSON Web Tokens)**: Autenticación y seguridad en las solicitudes REST.

## Requisitos para Ejecutar el Proyecto

- Java JDK 21 instalado.
- Spring Boot 4 como dependencia principal.
- Laragon instalado y configurado, con una base de datos creada para el proyecto.
- Acceso al frontend por vía local para integración y pruebas.

## Guía de uso y pruebas

1. Clona el repositorio:
    ```sh
    git clone https://github.com/MartinMDevv/FruitSeason_Backend.git
    cd FruitSeason_Backend
    ```
2. Instala JDK 21 y configura tu IDE (IntelliJ, Eclipse, VS Code, etc.).
3. Levanta Laragon y crea una base de datos para el proyecto.
4. Configura la conexión en el archivo `application.properties` con los datos de tu base local.
5. Ejecuta el backend con:
    ```sh
    ./mvnw spring-boot:run
    ```
    o
    ```sh
    ./gradlew bootRun
    ```
6. Accede a la documentación y pruebas de endpoints en Swagger en la ruta:
    ```
    http://localhost:<puerto>/swagger-ui/
    ```
    *(Reemplaza `<puerto>` por el puerto configurado, normalmente 8080)*

## Features principales

- Compra y gestión de suscripciones.
- Registro y autenticación de usuarios con JWT.
- API REST para interacción con el frontend.
- Documentación y pruebas de endpoints mediante Swagger.
- Seguridad y manejo de sesiones.

## Qué aprendí de este proyecto

- Profundicé mis conocimientos en Java y Spring Boot creando API REST orientadas a modelos de suscripción.
- Aprendí a implementar JWT para la autenticación segura de usuarios y manejo de sesiones.
- Configuré e integré Swagger para documentar y probar endpoints fácilmente.
- Mejoré la integración entre backend y frontend en entorno local.
- Desarrollé mejores prácticas en el diseño de sistemas de autenticación y gestión de usuarios.

---

¡Gracias por visitar el repositorio! Si tienes dudas, sugerencias o quieres contribuir, abre un Issue o Pull Request.
