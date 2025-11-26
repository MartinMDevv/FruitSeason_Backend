# 📘 Guía Completa de Uso de Swagger UI - FruitseasonBackend

## 🚀 Acceder a Swagger UI

### 1. Iniciar la Aplicación
```bash
cd c:\Users\venta\Desktop\FruitseasonBackend\FruitseasonBackend
mvnw.cmd spring-boot:run
```

### 2. Abrir Swagger UI en el Navegador
```
http://localhost:8080/swagger-ui.html
```

**URLs Importantes:**
- **Swagger UI (Interfaz Visual):** `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON (Especificación):** `http://localhost:8080/v3/api-docs`

---

## 📋 Interfaz de Swagger

### Secciones Principales

1. **Autenticación** - Endpoints públicos (register, login)
2. **Carrito** - Gestión del carrito de compras
3. **Pedidos** - Creación y consulta de pedidos
4. **Comentarios** - Sistema de comentarios (si aplica)

Cada endpoint muestra:
- ✅ Método HTTP (GET, POST, DELETE)
- ✅ URL del endpoint
- ✅ Descripción breve
- ✅ Parámetros requeridos
- ✅ Ejemplos de respuesta

---

## 🔐 Cómo Autenticarse en Swagger

### Paso 1: Registrar un Usuario

1. Click en **Autenticación** para expandir
2. Click en **POST /api/auth/register**
3. Click en **"Try it out"**
4. Edita el JSON de ejemplo:

```json
{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123"
}
```

5. Click en **"Execute"**
6. Verifica respuesta **201 Created**

---

### Paso 2: Hacer Login y Obtener Token

1. Click en **POST /api/auth/login**
2. Click en **"Try it out"**
3. Ingresa credenciales:

```json
{
  "username": "testuser",
  "password": "password123"
}
```

4. Click en **"Execute"**
5. **COPIA EL TOKEN** de la respuesta:

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "testuser",
  "message": "Login exitoso"
}
```

---

### Paso 3: Autenticar en Swagger UI

1. **Busca el botón "Authorize"** (candado verde) en la parte superior derecha
2. Click en **"Authorize"**
3. Aparecerá un modal con campo "Value:"
4. **Pega el token** (puedes pegar solo el token O escribir "Bearer <token>")
   - ✅ Correcto: `eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...`
   - ✅ También correcto: `Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...`
5. Click en **"Authorize"**
6. Click en **"Close"**

**✅ LISTO!** Ahora todos los endpoints protegidos funcionarán automáticamente.

---

## 🛒 Flujo Completo de Compra en Swagger

### Ejemplo: Comprar Suscripción BASIC

#### 1. Seleccionar Plan
- **Endpoint:** `POST /api/cart/select-plan`
- Click en **"Try it out"**
- Request body:
```json
{
  "plan": "BASIC"
}
```
- Click en **"Execute"**
- Respuesta esperada: ✅ 200 OK

---

#### 2. Agregar Frutas (4 mínimo para BASIC)

**Fruta 1:**
- **Endpoint:** `POST /api/cart/add-fruit`
- Request body:
```json
{
  "fruit": "APPLE"
}
```
- Execute

**Fruta 2:**
```json
{
  "fruit": "BANANA"
}
```

**Fruta 3:**
```json
{
  "fruit": "ORANGE"
}
```

**Fruta 4:**
```json
{
  "fruit": "GRAPE"
}
```

---

#### 3. Ver Carrito Actual
- **Endpoint:** `GET /api/cart`
- Click en **"Try it out"**
- Click en **"Execute"**
- Verifica que tengas 4 frutas y plan BASIC

---

#### 4. Realizar Checkout
- **Endpoint:** `POST /api/orders/checkout`
- Request body:
```json
{
  "cardHolderName": "Juan Pérez",
  "cardNumber": "4532015112830366"
}
```
- Click en **"Execute"**
- Respuesta esperada: ✅ 201 Created con detalles del pedido

---

#### 5. Ver Mis Pedidos
- **Endpoint:** `GET /api/orders`
- Click en **"Try it out"**
- Click en **"Execute"**
- Verás lista de todos tus pedidos

---

## 📌 Endpoints Importantes

### Endpoints Públicos (No requieren autenticación)
- ✅ `POST /api/auth/register` - Registrar usuario
- ✅ `POST /api/auth/login` - Iniciar sesión
- ✅ `GET /api/cart/available-fruits` - Ver frutas disponibles (opcional)

### Endpoints Protegidos (Requieren JWT)
Todos los demás endpoints requieren que presiones "Authorize" primero.

### Endpoint Solo para ADMIN
- ⚠️ `GET /api/orders/all` - Ver TODOS los pedidos del sistema
  - Requiere rol `ROLE_ADMIN` en la base de datos

---

## 🎯 Tips y Trucos de Swagger

### 1. Ver Frutas Disponibles
```
GET /api/cart/available-fruits
```
Retorna todas las frutas que puedes agregar al carrito.

### 2. Limpiar Carrito
```
DELETE /api/cart/clear
```
Útil para empezar de nuevo.

### 3. Remover Fruta Específica
```
DELETE /api/cart/remove-fruit
```
Con request body: `{"fruit": "APPLE"}`

### 4. Ver Detalle de un Pedido
```
GET /api/orders/{orderNumber}
```
Usa el `orderNumber` (UUID) de un pedido existente.

---

## 🔍 Interpretando Respuestas

### Códigos de Estado HTTP

**✅ 200 OK** - Operación exitosa
**✅ 201 Created** - Recurso creado (registro, pedido)
**⚠️ 400 Bad Request** - Datos inválidos
**⚠️ 401 Unauthorized** - Token faltante o inválido
**⚠️ 403 Forbidden** - Sin permisos (ej: no eres ADMIN)
**⚠️ 404 Not Found** - Recurso no encontrado
**❌ 500 Internal Server Error** - Error del servidor

---

## 🆚 Swagger vs Postman

| Característica | Swagger UI | Postman |
|----------------|------------|---------|
| **Instalación** | No requiere (navegador) | Requiere app |
| **Documentación automática** | ✅ Sí | ❌ No |
| **Pruebas rápidas** | ✅ Muy rápido | ⚠️ Requiere configurar |
| **Guardar colecciones** | ❌ No | ✅ Sí |
| **Autenticación JWT** | ✅ Integrada | ⚠️ Manual |
| **Compartir con equipo** | ✅ URL pública | ⚠️ Exportar JSON |
| **Testing automatizado** | ❌ No | ✅ Sí |
| **Mejor para...** | Desarrollo y docs | Testing complejo |

**Recomendación:** Usa Swagger para desarrollo rápido y Postman para testing completo.

---

## 🚫 Problemas Comunes

### Problema: "Failed to fetch"
**Solución:** 
- Verifica que la aplicación esté corriendo en `localhost:8080`
- Revisa la consola de Spring Boot por errores

### Problema: "401 Unauthorized"
**Solución:**
- Presiona "Authorize" e ingresa el token JWT
- Verifica que el token no haya expirado (válido por 1 hora)
- Haz login nuevamente si es necesario

### Problema: El botón "Authorize" no aparece
**Solución:**
- Refresca la página
- Verifica que `@SecurityRequirement(name = "bearer-jwt")` esté en los controladores

### Problema: "403 Forbidden" en /orders/all
**Solución:**
- Este endpoint SOLO funciona para usuarios ADMIN
- Ejecuta en MySQL:
```sql
UPDATE users SET role = 'ROLE_ADMIN' WHERE username = 'testuser';
```
- Haz login nuevamente para obtener nuevo token con rol ADMIN

---

## 📊 Esquemas de Datos

Swagger muestra automáticamente los esquemas de datos (DTOs) al final de la página:

- **RegisterRequest** - username, email, password
- **LoginRequest** - username, password
- **AuthResponse** - token, username, message
- **SelectPlanRequest** - plan (BASIC/FAMILY/PREMIUM)
- **AddFruitRequest** - fruit (APPLE, BANANA, etc.)
- **CheckoutRequest** - cardHolderName, cardNumber
- **OrderResponseDTO** - Detalles completos del pedido
- **FruitDTO** - type, name, category

---

## ⚙️ Configuración Avanzada

### Ver JSON de OpenAPI
```
http://localhost:8080/v3/api-docs
```

### Cambiar Puerto de Swagger
En `application.properties`:
```properties
server.port=9090
```
Luego accede a: `http://localhost:9090/swagger-ui.html`

### Deshabilitar Swagger en Producción
En `application.properties`:
```properties
springdoc.swagger-ui.enabled=false
```

---

## ✅ Checklist de Verificación

Antes de entregar tu proyecto, verifica:

- [ ] Swagger UI accesible en `http://localhost:8080/swagger-ui.html`
- [ ] Todos los endpoints están documentados
- [ ] Descripción clara de cada endpoint
- [ ] El botón "Authorize" funciona con JWT
- [ ] Puedes registrar un usuario desde Swagger
- [ ] Puedes hacer login y obtener token
- [ ] Puedes agregar frutas al carrito
- [ ] Puedes crear un pedido completo
- [ ] Las respuestas muestran datos correctos
- [ ] Los códigos de error son apropiados

---

## 🎓 Para tu Proyecto Académico

**Lo que el profesor verá:**

1. ✅ Documentación automática profesional
2. ✅ API probable directamente desde el navegador
3. ✅ Autenticación JWT funcionando
4. ✅ Endpoints bien organizados por categorías
5. ✅ Descripciones claras de cada operación
6. ✅ Ejemplos de request/response
7. ✅ Validaciones y manejo de errores

**Puntos extra:**
- Menciona que usas OpenAPI 3.0 (estándar de la industria)
- Destaca la integración de seguridad JWT en Swagger
- Muestra cómo facilita testing sin necesitar Postman

---

## 📚 Recursos Adicionales

- **Documentación de Springdoc:** https://springdoc.org/
- **Especificación OpenAPI 3.0:** https://swagger.io/specification/
- **Guía de Swagger UI:** https://swagger.io/tools/swagger-ui/

---

## 🎉 ¡Listo para Probar!

Ya puedes abrir `http://localhost:8080/swagger-ui.html` y empezar a probar todos los endpoints de tu API directamente desde el navegador.

**Recuerda:**
1. Registrar usuario
2. Hacer login
3. Copiar token
4. Presionar "Authorize"
5. ¡Probar todos los endpoints!
