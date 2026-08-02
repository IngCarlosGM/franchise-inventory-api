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

## Manejo de errores

## Pruebas y cobertura

## Flujo de trabajo con Git

## Consideraciones de diseño