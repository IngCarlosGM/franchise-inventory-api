# Franchise Inventory API

API RESTFUL reactiva para administrar franquicias, sus sucursales y el inventario de productos de cada sucursal.

Construida con Spring Boot Webflux teniendo como base una arquitectura limpia por módulos, con persistencia reactiva en PostgreSQL.

## Tabla de contenido

- [Stack](#stack)
- [Arquitectura](#arquitectura)
- [Requisitos previos](#requisitos-previos)
- [Cómo levantar el proyecto en local](#cómo-levantar-el-proyecto-en-local)
- [Variables de entorno](#variables-de-entorno)
- [Endpoints](#endpoints)
- [Modelo de datos](#modelo-de-datos)
- [Manejo de errores](#manejo-de-errores)
- [Pruebas y cobertura](#pruebas-y-cobertura)
- [Flujo de trabajo con Git](#flujo-de-trabajo-con-git)
- [Consideraciones de diseño](#consideraciones-de-diseño)

## Stack

## Arquitectura

## Requisitos previos

## Cómo levantar el proyecto en local

## Variables de entorno

| Variable | Descripción | Por defecto |
|---|---|---|
| `SERVER_PORT` | Puerto HTTP de la aplicación | `8080` |
| `DB_HOST` | Host de PostgreSQL | `localhost` |
| `DB_PORT` | Puerto de PostgreSQL | `5432` |
| `DB_NAME` | Nombre de la base de datos | `franchise_inventory` |
| `DB_USER` | Usuario de la base de datos | `postgres` |
| `DB_PASSWORD` | Contraseña de la base de datos | `postgres` |
| `DB_SSL_MODE` | Modo TLS de la conexión R2DBC | `disable` |

Los valores por defecto apuntan al PostgreSQL local de `docker-compose.yml`. Para una
instancia gestionada en nube, `DB_SSL_MODE` debe ser `require`.

El archivo `.env.example` sirve de plantilla. **Spring no lee `.env` automáticamente**:
las variables deben estar en el entorno del proceso.

## Endpoints

### Crear franquicia

`POST /franchises`

```json
{ "name": "Mi Franquicia" }
```

Campos opcionales: `contactEmail`, `website`.

**Respuestas**

| Código | Cuándo                                              |
|---|-----------------------------------------------------|
| `201` | Franquicia creada; la devuelve con su identificador |
| `400` | El nombre está vacío o el cuerpo es inválido        |
| `409` | Ya existe una franquicia con ese nombre             |

### Agregar sucursal a una franquicia

`POST /franchises/{franchiseId}/branches`

```json
{ "name": "Centro" }
```

Campos opcionales: `city`, `phone`.

**Respuestas**

| Código | Cuándo |
|---|---|
| `201` | Sucursal creada; devuelve el recurso con su identificador |
| `400` | El nombre está vacío o el cuerpo es inválido |
| `404` | La franquicia indicada no existe |
| `409` | Ya existe una sucursal con ese nombre en esa franquicia |

La unicidad del nombre está acotada a la franquicia: dos franquicias distintas
pueden tener una sucursal llamada "Centro".

### Agregar producto a una sucursal

`POST /branches/{branchId}/products`

```json
{ "name": "Café", "stock": 40 }
```

Campos opcionales: `price`, `unit`.

**Respuestas**

| Código | Cuándo                                                                     |
|---|----------------------------------------------------------------------------|
| `201` | Producto creado; devuelve el producto con su identificador                 |
| `400` | El nombre está vacío, el stock es nulo o negativo, o el precio es negativo |
| `404` | La sucursal indicada no existe                                             |
| `409` | Ya existe un producto con ese nombre en esa sucursal                       |

Un stock de cero es válido: significa agotado. La unicidad del nombre está acotada a la sucursal.

### Eliminar producto

`DELETE /products/{productId}`

Sin body.

**Respuestas**

| Código | Cuándo                         |
|---|--------------------------------|
| `204` | Producto eliminado; sin body   |
| `404` | El producto indicado no existe |

Eliminar un producto inexistente devuelve `404` en lugar de `204`: informa al
cliente de que no había nada que borrar.

### Modificar el stock de un producto

`PATCH /products/{productId}`

```json
{ "stock": 42 }
```

**Respuestas**

| Código | Cuándo |
|---|---|
| `200` | Stock actualizado; devuelve el producto completo |
| `400` | El stock es nulo o negativo |
| `404` | El producto indicado no existe |

El valor recibido **reemplaza** el stock, no lo incrementa. La operación es
idempotente: aplicar la misma petición dos veces deja el mismo estado.

### Productos con más stock por sucursal

`GET /franchises/{franchiseId}/top-products`

Sin body.

**Respuesta**

```json
[
  { "branchId": "…", "branchName": "Centro",
    "productId": "…", "productName": "Café", "stock": 40 }
]
```

| Código | Cuándo |
|---|---|
| `200` | Un elemento por sucursal; lista vacía si no hay resultados |
| `404` | La franquicia indicada no existe |

Los empates se devuelven todos: si dos productos comparten el stock máximo de una
sucursal, ambos aparecen. Las sucursales sin productos se omiten. Una franquicia
sin resultados devuelve lista vacía con `200`.

## Modelo de datos

### `franchise`

| Columna | Tipo | Restricción |
|---|---|---|
| `id` | `VARCHAR(36)` | Clave primaria, UUID asignado por la aplicación |
| `name` | `VARCHAR(120)` | Obligatorio, único |
| `contact_email` | `VARCHAR(120)` | Opcional |
| `website` | `VARCHAR(200)` | Opcional |
| `created_at` | `TIMESTAMPTZ` | Obligatorio, asignado por el adaptador |
| `updated_at` | `TIMESTAMPTZ` | Obligatorio, asignado por el adaptador |

### `branch`

| Columna | Tipo | Restricción |
|---|---|---|
| `id` | `VARCHAR(36)` | Clave primaria, UUID asignado por la aplicación |
| `franchise_id` | `VARCHAR(36)` | Obligatorio, llave foránea a `franchise` con borrado en cascada |
| `name` | `VARCHAR(120)` | Obligatorio, único dentro de la franquicia |
| `city` | `VARCHAR(80)` | Opcional |
| `phone` | `VARCHAR(20)` | Opcional |
| `created_at` | `TIMESTAMPTZ` | Obligatorio, asignado por el adaptador |
| `updated_at` | `TIMESTAMPTZ` | Obligatorio, asignado por el adaptador |

### `product`

| Columna | Tipo | Restricción |
|---|---|---|
| `id` | `VARCHAR(36)` | Clave primaria, UUID asignado por la aplicación |
| `branch_id` | `VARCHAR(36)` | Obligatorio, llave foránea a `branch` con borrado en cascada |
| `name` | `VARCHAR(120)` | Obligatorio, único dentro de la sucursal |
| `stock` | `INTEGER` | Obligatorio, no negativo |
| `price` | `NUMERIC(12,2)` | Opcional, no negativo |
| `unit` | `VARCHAR(20)` | Opcional |
| `created_at` | `TIMESTAMPTZ` | Obligatorio, asignado por el adaptador |
| `updated_at` | `TIMESTAMPTZ` | Obligatorio, asignado por el adaptador |

## Manejo de errores

## Pruebas y cobertura

## Flujo de trabajo con Git

## Consideraciones de diseño