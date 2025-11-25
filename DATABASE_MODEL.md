# Modelo de Datos - FruitSeason Backend

## Documentación Completa de Base de Datos para Integración con Frontend

Esta documentación detalla todas las tablas, campos, tipos de datos y relaciones del sistema FruitSeason Backend.

---

## 📊 Diagrama de Relaciones

```
┌─────────────┐         ┌──────────────────┐         ┌─────────────┐
│    users    │◄────────│ payment_methods  │         │  comments   │
│             │  1:N    │                  │         │             │
└──────┬──────┘         └──────────────────┘         └─────────────┘
       │                                              
       │ 1:1                                          
       ▼                                              
┌─────────────┐                                       
│    carts    │                                       
│             │                                       
└──────┬──────┘                                       
       │ 1:N                                          
       ▼                                              
┌─────────────┐                                       
│ cart_items  │                                       
│             │                                       
└─────────────┘                                       

┌─────────────┐
│   orders    │
│             │◄──── users (1:N)
└─────────────┘
```

---

## 1️⃣ Tabla: `users`

**Descripción**: Almacena usuarios registrados del sistema con autenticación.

### Campos

| Campo | Tipo de Dato (Java) | Tipo SQL | Nullable | Único | Descripción |
|-------|---------------------|----------|----------|-------|-------------|
| `id` | Long | BIGINT | NO | SÍ (PK) | ID autoincremental del usuario |
| `username` | String | VARCHAR(50) | NO | SÍ | Nombre de usuario único |
| `email` | String | VARCHAR(100) | NO | SÍ | Email único del usuario |
| `password` | String | VARCHAR(255) | NO | NO | Contraseña hasheada con BCrypt |
| `role` | String | VARCHAR(20) | NO | NO | Rol del usuario (default: "ROLE_USER") |
| `subscription` | SubscriptionPlan (Enum) | VARCHAR(20) | NO | NO | Plan de suscripción actual |
| `created_at` | LocalDateTime | DATETIME | NO | NO | Fecha de creación (auto-generada) |

### Valores del Enum `SubscriptionPlan`
- `NO_SUBSCRIBED` - Sin suscripción (default)
- `BASIC` - Plan básico (4 frutas)
- `FAMILY` - Plan familiar (8 frutas)
- `PREMIUM` - Plan premium (12 frutas)

### Relaciones
- **1:N con `payment_methods`** - Un usuario puede tener múltiples métodos de pago
- **1:1 con `carts`** - Un usuario tiene un carrito
- **1:N con `orders`** - Un usuario puede tener múltiples pedidos

### Ejemplo JSON (Response del Backend)
```json
{
  "id": 1,
  "username": "juanperez",
  "email": "juan@example.com",
  "role": "ROLE_USER",
  "subscription": "BASIC",
  "createdAt": "2025-11-25T15:00:00"
}
```

**NOTA**: El campo `password` NUNCA se envía en las respuestas JSON.

---

## 2️⃣ Tabla: `payment_methods`

**Descripción**: Métodos de pago guardados de los usuarios (solo datos enmascarados).

### Campos

| Campo | Tipo de Dato (Java) | Tipo SQL | Nullable | Único | Descripción |
|-------|---------------------|----------|----------|-------|-------------|
| `id` | Long | BIGINT | NO | SÍ (PK) | ID autoincremental |
| `card_holder_name` | String | VARCHAR(255) | SÍ | NO | Nombre del titular de la tarjeta |
| `masked_number` | String | VARCHAR(255) | SÍ | NO | Número enmascarado (ej: "**** **** **** 1234") |
| `last4` | String | VARCHAR(255) | SÍ | NO | Últimos 4 dígitos de la tarjeta |
| `created_at` | LocalDateTime | DATETIME | SÍ | NO | Fecha de registro del método de pago |
| `user_id` | Long | BIGINT | SÍ | NO | FK hacia `users.id` |

### Relaciones
- **N:1 con `users`** - Muchos métodos de pago pertenecen a un usuario

### Ejemplo JSON
```json
{
  "id": 1,
  "cardHolderName": "Juan Pérez",
  "maskedNumber": "**** **** **** 1234",
  "last4": "1234",
  "createdAt": "2025-11-25T15:30:00"
}
```

---

## 3️⃣ Tabla: `carts`

**Descripción**: Carrito de compras de cada usuario (uno por usuario).

### Campos

| Campo | Tipo de Dato (Java) | Tipo SQL | Nullable | Único | Descripción |
|-------|---------------------|----------|----------|-------|-------------|
| `id` | Long | BIGINT | NO | SÍ (PK) | ID autoincremental del carrito |
| `user_id` | Long | BIGINT | NO | SÍ | FK hacia `users.id` (relación 1:1) |
| `selected_plan` | SubscriptionPlan (Enum) | VARCHAR(20) | SÍ | NO | Plan seleccionado en el carrito |
| `created_at` | LocalDateTime | DATETIME | NO | NO | Fecha de creación del carrito |
| `updated_at` | LocalDateTime | DATETIME | NO | NO | Última actualización del carrito |

### Valores del Enum `SubscriptionPlan`
- `BASIC` - Requiere 4 frutas
- `FAMILY` - Requiere 8 frutas
- `PREMIUM` - Requiere 12 frutas
- `null` - No hay plan seleccionado

### Relaciones
- **1:1 con `users`** - Un carrito pertenece a un usuario
- **1:N con `cart_items`** - Un carrito tiene múltiples items (frutas)

### Ejemplo JSON (Response GET /cart)
```json
{
  "id": 1,
  "selectedPlan": "BASIC",
  "requiredFruits": 4,
  "selectedFruits": [
    {
      "type": "MANZANA",
      "name": "Manzana",
      "category": "FRUTA"
    },
    {
      "type": "PERA",
      "name": "Pera",
      "category": "FRUTA"
    }
  ],
  "selectedFruitsCount": 2,
  "isComplete": false
}
```

---

## 4️⃣ Tabla: `cart_items`

**Descripción**: Items individuales del carrito (frutas seleccionadas).

### Campos

| Campo | Tipo de Dato (Java) | Tipo SQL | Nullable | Único | Descripción |
|-------|---------------------|----------|----------|-------|-------------|
| `id` | Long | BIGINT | NO | SÍ (PK) | ID autoincremental del item |
| `cart_id` | Long | BIGINT | NO | NO | FK hacia `carts.id` |
| `fruit_type` | FruitType (Enum) | VARCHAR(50) | NO | NO | Tipo de fruta seleccionada |

### Constraint Único
- `UNIQUE(cart_id, fruit_type)` - No permite frutas duplicadas en el mismo carrito

### Valores del Enum `FruitType`

**Frutas (11 tipos):**
- `FRUTILLA`, `NISPERO`, `DURAZNO`, `MELON`, `SANDIA`, `MANZANA`, `PERA`, `UVAS`, `KIWI`, `MANDARINA`, `NARANJA`

**Verduras (9 tipos):**
- `ALCACHOFA`, `ESPARRAGO`, `LECHUGA`, `TOMATE`, `ZAPALLO_ITALIANO`, `BROCOLI`, `ZAPALLO`, `COLIFLOR`, `REPOLLO`

Cada enum tiene:
- `name()` - Nombre del enum (ej: "MANZANA")
- `getDisplayName()` - Nombre para mostrar (ej: "Manzana")
- `getCategory()` - Categoría ("FRUTA" o "VERDURA")

### Relaciones
- **N:1 con `carts`** - Muchos items pertenecen a un carrito

### Ejemplo JSON (dentro de cart)
```json
{
  "type": "MANZANA",
  "name": "Manzana",
  "category": "FRUTA"
}
```

---

## 5️⃣ Tabla: `orders`

**Descripción**: Pedidos completados por los usuarios.

### Campos

| Campo | Tipo de Dato (Java) | Tipo SQL | Nullable | Único | Descripción |
|-------|---------------------|----------|----------|-------|-------------|
| `id` | Long | BIGINT | NO | SÍ (PK) | ID autoincremental del pedido |
| `order_number` | String | VARCHAR(36) | NO | SÍ | Número UUID único del pedido |
| `user_id` | Long | BIGINT | NO | NO | FK hacia `users.id` |
| `plan` | SubscriptionPlan (Enum) | VARCHAR(20) | NO | NO | Plan adquirido |
| `selected_fruits` | String | VARCHAR(500) | NO | NO | Frutas separadas por coma (ej: "MANZANA,PERA,NARANJA,KIWI") |
| `card_holder_name` | String | VARCHAR(100) | NO | NO | Nombre del titular de la tarjeta |
| `card_last4` | String | VARCHAR(20) | NO | NO | Últimos 4 dígitos de la tarjeta |
| `order_date` | LocalDateTime | DATETIME | NO | NO | Fecha y hora del pedido (auto-generada) |
| `status` | String | VARCHAR(20) | NO | NO | Estado del pedido (default: "COMPLETED") |

### Valores del Campo `status`
- `COMPLETED` - Pedido completado (default)
- `PENDING` - Pedido pendiente
- `CANCELLED` - Pedido cancelado

### Relaciones
- **N:1 con `users`** - Muchos pedidos pertenecen a un usuario

### Ejemplo JSON (Response GET /orders)
```json
{
  "id": 1,
  "orderNumber": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "plan": "BASIC",
  "fruits": [
    {
      "type": "MANZANA",
      "name": "Manzana",
      "category": "FRUTA"
    },
    {
      "type": "PERA",
      "name": "Pera",
      "category": "FRUTA"
    },
    {
      "type": "NARANJA",
      "name": "Naranja",
      "category": "FRUTA"
    },
    {
      "type": "KIWI",
      "name": "Kiwi",
      "category": "FRUTA"
    }
  ],
  "fruitsCount": 4,
  "cardHolderName": "Juan Pérez",
  "cardLast4": "1234",
  "orderDate": "2025-11-25T15:45:00",
  "status": "COMPLETED",
  "username": "juanperez"
}
```

---

## 6️⃣ Tabla: `comments`

**Descripción**: Comentarios/testimonios de usuarios (independiente de autenticación).

### Campos

| Campo | Tipo de Dato (Java) | Tipo SQL | Nullable | Único | Descripción |
|-------|---------------------|----------|----------|-------|-------------|
| `id` | Long | BIGINT | NO | SÍ (PK) | ID autoincremental |
| `email` | String | VARCHAR(100) | NO | NO | Email del comentarista |
| `text` | String | VARCHAR(500) | NO | NO | Texto del comentario |
| `created_at` | LocalDateTime | DATETIME | NO | NO | Fecha del comentario |

### Relaciones
- Ninguna (tabla independiente)

### Ejemplo JSON
```json
{
  "id": 1,
  "email": "maria@example.com",
  "text": "Excelente servicio, las frutas son muy frescas!",
  "createdAt": "2025-11-25T14:20:00"
}
```

---

## 🔗 Resumen de Relaciones

| Tabla Padre | Relación | Tabla Hija | Tipo | Cascade |
|-------------|----------|------------|------|---------|
| `users` | 1:N | `payment_methods` | OneToMany | ALL, orphanRemoval |
| `users` | 1:1 | `carts` | OneToOne | - |
| `users` | 1:N | `orders` | OneToMany | - |
| `carts` | 1:N | `cart_items` | OneToMany | ALL, orphanRemoval |

---

## 📝 Notas Importantes para el Frontend

### Autenticación JWT
- Todos los endpoints de `/cart/*` y `/orders/*` requieren header: `Authorization: Bearer {token}`
- El token se obtiene después del login en `/auth/login`

### Tipos de Datos
- **LocalDateTime**: Formato ISO-8601 → `"2025-11-25T15:30:00"`
- **Enum**: Siempre en MAYÚSCULAS → `"BASIC"`, `"MANZANA"`, etc.
- **Long**: Números enteros → `1`, `123`, etc.

### Validaciones del Backend
1. **Carrito**: No se pueden agregar más frutas del límite del plan
2. **Carrito**: No se permiten frutas duplicadas (validación en BD)
3. **Checkout**: Se requiere plan seleccionado y cantidad mínima de frutas
4. **Checkout**: Validación de tarjeta con algoritmo de Luhn

### Planes y Frutas Requeridas

| Plan | Frutas Mínimas | Frutas Máximas |
|------|----------------|----------------|
| BASIC | 4 | 4 |
| FAMILY | 8 | 8 |
| PREMIUM | 12 | 12 |

---

## 🎯 Flujo de Datos Típico

### 1. Registro y Login
```
POST /auth/register → Crea user en DB
POST /auth/login → Retorna JWT token
```

### 2. Selección de Plan y Frutas
```
GET /cart/available-fruits → Lista 20 frutas
POST /cart/select-plan → Selecciona plan en cart
POST /cart/add-fruit (x N) → Agrega items a cart_items
GET /cart → Verifica estado del carrito
```

### 3. Checkout
```
POST /orders/checkout → Crea order, actualiza user.subscription, limpia cart
GET /orders → Lista orders del usuario
```

---

## 📋 Lista Completa de Frutas (20 tipos)

### Frutas (11)
1. FRUTILLA - "Frutilla"
2. NISPERO - "Níspero"
3. DURAZNO - "Durazno"
4. MELON - "Melón"
5. SANDIA - "Sandía"
6. MANZANA - "Manzana"
7. PERA - "Pera"
8. UVAS - "Uvas"
9. KIWI - "Kiwi"
10. MANDARINA - "Mandarina"
11. NARANJA - "Naranja"

### Verduras (9)
1. ALCACHOFA - "Alcachofa"
2. ESPARRAGO - "Espárrago"
3. LECHUGA - "Lechuga"
4. TOMATE - "Tomate"
5. ZAPALLO_ITALIANO - "Zapallo italiano"
6. BROCOLI - "Brócoli"
7. ZAPALLO - "Zapallo"
8. COLIFLOR - "Coliflor"
9. REPOLLO - "Repollo"
