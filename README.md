# Franchise API

API REST reactiva para administrar franquicias, sus sucursales y los productos de cada sucursal.

- Una **franquicia** tiene un nombre y una lista de sucursales.
- Una **sucursal** tiene un nombre y una lista de productos.
- Un **producto** tiene un nombre y una cantidad de stock.

## Tecnologías

- Java 21 y Spring Boot 4.1
- Spring WebFlux (programación reactiva con Reactor)
- Spring Data Reactive MongoDB
- MongoDB Atlas (persistencia en la nube)
- JUnit 5, Mockito y StepVerifier
- Docker y Docker Compose
- Gradle

## Arquitectura

El proyecto sigue Clean Architecture. Las dependencias siempre apuntan hacia adentro: `infrastructure → usecase → domain`.

```
com.danielgomez.franchise
├── domain
│   ├── model
│   │   ├── franchise      Franchise, Branch, Product, BranchTopProduct
│   │   └── exception      BusinessException, ResourceNotFoundException
│   └── port/out           FranchiseRepository (puerto de salida)
├── usecase
│   └── franchise          FranchiseUseCase
└── infrastructure
    ├── config             Crea los casos de uso como beans
    ├── drivenadapter
    │   └── mongo          Documentos, repositorio reactivo, mapper y adaptador
    └── entrypoint
        └── rest           Controlador, DTOs y manejo global de errores
```

- **domain**: reglas del negocio. No depende de Spring ni de MongoDB.
- **usecase**: orquesta el flujo (buscar, modificar, guardar) usando el puerto `FranchiseRepository`.
- **infrastructure**: detalles técnicos. El adaptador de Mongo implementa el puerto y el controlador REST expone los casos de uso.

## Decisiones de diseño

- **Un documento por franquicia.** Las sucursales y los productos van embebidos. Cada operación lee y guarda la franquicia completa, sin joins ni consistencia entre colecciones.
- **Dominio inmutable.** `Franchise`, `Branch` y `Product` son records. Cada cambio devuelve una instancia nueva y las validaciones (nombre obligatorio, stock no negativo) están en el constructor, así que no puede existir un objeto inválido.
- **Producto con más stock por sucursal.** Se calcula en el dominio (`Franchise.topStockProductPerBranch()`), no con una agregación de Mongo. Así la regla queda en el negocio y se prueba sin base de datos. Si hay empate, gana el primer producto agregado. Las sucursales sin productos no aparecen.
- **Casos de uso sin anotaciones de Spring.** Se registran como beans en `infrastructure/config`.
- **Errores con `ProblemDetail` (RFC 9457).** `ResourceNotFoundException` → 404, `BusinessException` y validaciones → 400.

## Cómo correr en local

### Requisitos

- Java 21
- Docker

### Opción 1: todo con Docker Compose (recomendada)

Levanta la API y un MongoDB:

```bash
docker compose up --build
```

La API queda en `http://localhost:8080`. Para detenerla:

```bash
docker compose down
```

### Opción 2: Gradle con MongoDB en Docker

```bash
docker run -d --name mongo-franquicias -p 27017:27017 mongo:7
./gradlew bootRun
```

Por defecto la API se conecta a `mongodb://localhost:27017/franchises`.

### Conectar a MongoDB Atlas

La conexión se toma de la variable de entorno `MONGODB_URI`:

```bash
MONGODB_URI="mongodb+srv://<usuario>:<clave>@<cluster>.mongodb.net/franchises?retryWrites=true&w=majority" ./gradlew bootRun
```

En PowerShell:

```powershell
$env:MONGODB_URI="mongodb+srv://<usuario>:<clave>@<cluster>.mongodb.net/franchises?retryWrites=true&w=majority"; ./gradlew bootRun
```

## Persistencia en la nube

La base de datos está en **MongoDB Atlas**, en un cluster M0 (gratuito) en AWS `us-east-1`. La aplicación no cambia entre local y nube: solo cambia el valor de `MONGODB_URI`. Las credenciales no están en el repositorio.

## Pruebas

```bash
./gradlew test
```

- `FranchiseTest`: reglas del dominio (producto con más stock, empates, sucursales vacías, inmutabilidad, stock negativo).
- `FranchiseUseCaseTest`: casos de uso con Mockito y StepVerifier, tanto los casos felices como los errores (franquicia, sucursal o producto inexistente, nombre vacío, stock negativo y error del repositorio).

## Endpoints

Base: `http://localhost:8080/api/franchises`

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/franchises` | Crear franquicia |
| PATCH | `/api/franchises/{franchiseId}` | Cambiar el nombre de la franquicia |
| POST | `/api/franchises/{franchiseId}/branches` | Agregar sucursal |
| PATCH | `/api/franchises/{franchiseId}/branches/{branchId}` | Cambiar el nombre de la sucursal |
| POST | `/api/franchises/{franchiseId}/branches/{branchId}/products` | Agregar producto |
| DELETE | `/api/franchises/{franchiseId}/branches/{branchId}/products/{productId}` | Eliminar producto |
| PATCH | `/api/franchises/{franchiseId}/branches/{branchId}/products/{productId}/stock` | Modificar stock |
| PATCH | `/api/franchises/{franchiseId}/branches/{branchId}/products/{productId}` | Cambiar el nombre del producto |
| GET | `/api/franchises/{franchiseId}/top-stock-products` | Producto con más stock de cada sucursal |

### Ejemplos con curl

```bash
# Crear franquicia (guarda el "id" de la respuesta)
curl -X POST http://localhost:8080/api/franchises \
  -H "Content-Type: application/json" \
  -d '{"name": "Panadería"}'

# Agregar sucursal
curl -X POST http://localhost:8080/api/franchises/{franchiseId}/branches \
  -H "Content-Type: application/json" \
  -d '{"name": "Centro"}'

# Agregar producto
curl -X POST http://localhost:8080/api/franchises/{franchiseId}/branches/{branchId}/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Pan", "stock": 10}'

# Modificar stock
curl -X PATCH http://localhost:8080/api/franchises/{franchiseId}/branches/{branchId}/products/{productId}/stock \
  -H "Content-Type: application/json" \
  -d '{"stock": 25}'

# Eliminar producto
curl -X DELETE http://localhost:8080/api/franchises/{franchiseId}/branches/{branchId}/products/{productId}

# Producto con más stock por sucursal
curl http://localhost:8080/api/franchises/{franchiseId}/top-stock-products

# Cambiar nombres
curl -X PATCH http://localhost:8080/api/franchises/{franchiseId} \
  -H "Content-Type: application/json" -d '{"name": "Panadería Central"}'
curl -X PATCH http://localhost:8080/api/franchises/{franchiseId}/branches/{branchId} \
  -H "Content-Type: application/json" -d '{"name": "Norte"}'
curl -X PATCH http://localhost:8080/api/franchises/{franchiseId}/branches/{branchId}/products/{productId} \
  -H "Content-Type: application/json" -d '{"name": "Pan integral"}'
```

Respuesta de `top-stock-products`:

```json
[
  { "branchId": "…", "branchName": "Centro", "productId": "…", "productName": "Pan", "stock": 25 },
  { "branchId": "…", "branchName": "Norte", "productId": "…", "productName": "Queso", "stock": 12 }
]
```

Ejemplo de error (404):

```json
{
  "title": "Not Found",
  "status": 404,
  "detail": "Franchise not found with id 'abc'",
  "instance": "/api/franchises/abc/top-stock-products"
}
```

## Flujo de Git

Cada bloque de trabajo se hizo en una rama (`feature/domain`, `feature/api`, `feature/tests`, `feature/docker`, `docs/readme`) y se integró a `main` con un pull request. Los commits siguen Conventional Commits (`feat`, `test`, `build`, `docs`, `chore`).

## Pendientes y mejoras

- Aprovisionar el cluster de Atlas con **Terraform** (provider `mongodbatlas`) en lugar de crearlo desde la consola.
- Desplegar la API en la nube (por ejemplo Render o AWS) usando la imagen Docker.
- Restringir el acceso de red de Atlas. Hoy está abierto (`0.0.0.0/0`) para facilitar la revisión.
- Pruebas de integración de los endpoints con `WebTestClient` y Testcontainers.

## Uso de IA

Usé un asistente de IA (Claude) de forma puntual, como apoyo:

- Propuesta inicial del árbol de paquetes de Clean Architecture.
- Fragmento reactivo del caso "producto con más stock por sucursal", que después integré y ajusté (`flatMapIterable`).
- Revisión de mis casos de uso y pruebas, y lista de casos borde que faltaban.
- Patrón de manejo global de errores con `@RestControllerAdvice` y `ProblemDetail`.
- Guía para configurar MongoDB Atlas.
- Borrador de este README, que revisé y ajusté.

El modelo de dominio, los casos de uso, el adaptador de Mongo, los controladores, las pruebas y Docker los desarrollé yo.
