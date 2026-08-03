# Franchise Inventory API

API RESTful reactiva para administrar franquicias, sus sucursales y el inventario de productos de cada sucursal.

Construida con Spring Boot WebFlux teniendo como base una arquitectura limpia por módulos, con persistencia reactiva en PostgreSQL.

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

| Componente | Elección                                                      |
|---|---------------------------------------------------------------|
| Lenguaje | Java 21 (LTS)                                                 |
| Framework | Spring Boot 4 con WebFlux                                     |
| Persistencia | PostgreSQL 17 vía R2DBC - Neon en la nube, Docker en local    |
| Build | Gradle multimódulo                                            |
| Pruebas | JUnit 5, Mockito, StepVerifier, WebTestClient, Testcontainers |
| Cobertura | JaCoCo                                                        |

El proyecto parte del scaffold [Clean Architecture](https://github.com/bancolombia/scaffold-clean-architecture),
que genera la estructura de módulos y aporta reglas de ArchUnit que fallan el build
si no se siguen adecuadamente.

## Arquitectura

**Arquitectura hexagonal (puertos y adaptadores), organizada por módulos de Gradle.**
El negocio queda en el centro y declara como interfaces, los puertos, todo lo que
necesita del exterior. Lo de fuera (HTTP, base de datos) son adaptadores que
implementan esos puertos. La arquitectura limpia es la misma idea con las capas
nombradas de otra forma; esta api se trabajó bajo lo estipulado en el scaffold.

Cinco módulos, con las dependencias apuntando siempre hacia adentro:

```
                    app-service
              (arranca y ensambla todo)
                         |
        +----------------+----------------+
        |                                 |
  reactive-web                     r2dbc-postgresql
  (entry point)                    (driven adapter)
        |                                 |
        |                                 |
        +----------> usecase <------------+
                        |
                      model
           (entidades, errores, puertos)
```
La estructura sigue el modelo del scaffold: un dominio central que no conoce
tecnología, entry points que reciben señales del exterior e inician los flujos, y
gateways implementados por adaptadores intercambiables que encapsulan los efectos
secundarios.

| Módulo | Contiene | Depende de |
|---|---|---|
| `domain/model` | Entidades, proyección de lectura, errores de negocio y puertos | nada |
| `domain/usecase` | Los seis casos de uso | `model` |
| `infrastructure/entry-points/reactive-web` | Router, handlers, modelos HTTP y traducción de errores | `model`, `usecase` |
| `infrastructure/driven-adapters/r2dbc-postgresql` | Entidades de persistencia, mappers y adaptadores | `model` |
| `applications/app-service` | Arranque y ensamblado de beans | todos |

`domain/model` no tiene una sola dependencia externa: sus clases se compilan y se
prueban sin Spring, sin driver y sin servidor. Los casos de uso son Java puro, sin
anotaciones; `app-service` los registra como beans y les inyecta las
implementaciones concretas de los puertos que declaran en su constructor.

**Qué beneficios tiene esta separación.** El dominio declara lo que necesita como interfaces
`FranchiseRepository`, `IdentityGenerator` y la infraestructura las implementa. Por ende, los casos de uso se prueban sustituyendo los puertos por implementaciones falsas,
sin base de datos ni servidor, y que cambiar PostgreSQL por otro motor toca un módulo y ninguno
más. Los tests de `domain/usecase` corren en milisegundos porque no levantan nada.

**Lógica de negocio.** Solo en `usecase` y en las entidades. Los handlers leen el
cuerpo y arman la respuesta; los adaptadores traducen entre tipos de dominio y filas.

**Request e2e.** `RouterRest` reconoce la ruta y se la pasa al
handler. El handler lee el cuerpo, lo convierte en un objeto del dominio y llama al
caso de uso. El caso de uso valida, decide y pide datos a través de sus puertos, sin
saber si detrás hay PostgreSQL u otro motor. El adaptador recibe esa petición,
la traduce a una consulta y devuelve tipos de dominio. Si algo falla por el camino, la
excepción sube hasta un único manejador que la convierte en el código HTTP que
corresponda.

### Comandos del scaffold

El plugin no es solo generación inicial: valida la estructura en cada build.

| Comando | Qué hace |
|---|---|
| `./gradlew vs` | Valida que la estructura de módulos siga siendo correcta |
| `./gradlew gm --name=X` | Genera una entidad de dominio y su gateway |
| `./gradlew guc --name=X` | Genera un caso de uso |
| `./gradlew gda --type=r2dbc` | Genera un driven adapter |
| `./gradlew gep --type=webflux` | Genera un entry point |

## Requisitos previos

- **JDK 21** (probado con Temurin 21.0.10)
- **Docker**, para el PostgreSQL local y para las pruebas de integración

No hace falta instalar Gradle: el proyecto incluye el wrapper.

## Cómo levantar el proyecto en local

**1. Levantar la base de datos**

```bash
docker compose up -d
```

Arranca un PostgreSQL 17 en el puerto 5432 con la base `franchise_inventory`. El
esquema se crea solo al arrancar la aplicación.

**2. Configurar las variables de entorno**

```bash
cp .env.example .env
```

Los valores por defecto ya apuntan al contenedor local, así que no hay que editarlos
para desarrollo.

**3. Cargar las variables y arrancar**

```bash
set -a && source .env && set +a
./gradlew bootRun
```

**Spring no lee el archivo `.env` automáticamente**, a diferencia de otros
ecosistemas: las variables deben estar en el entorno del proceso. El `set -a` marca
para exportación todo lo que se defina a continuación, `source` lee el archivo y
`set +a` deshace el marcado.

Desde IntelliJ, la alternativa es declararlas en *Run Configuration → Environment
variables*.

La API queda en `http://localhost:8080`. Para comprobar que responde:

```bash
curl http://localhost:8080/actuator/health
```

**Apuntar a una base de datos gestionada**

Basta cambiar los valores del `.env` y poner `DB_SSL_MODE=require`; la mayoría de
proveedores gestionados exigen TLS. No hace falta tocar código ni recompilar.

**Detener**

```bash
docker compose down      # detiene la base de datos
docker compose down -v   # además borra los datos
```

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

| Código | Cuándo |
|---|---|
| `201` | Franquicia creada; la devuelve con su identificador |
| `400` | El nombre está vacío o el cuerpo es inválido |
| `409` | Ya existe una franquicia con ese nombre |

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

| Código | Cuándo |
|---|---|
| `201` | Producto creado; devuelve el producto con su identificador |
| `400` | El nombre está vacío, el stock es nulo o negativo, o el precio es negativo |
| `404` | La sucursal indicada no existe |
| `409` | Ya existe un producto con ese nombre en esa sucursal |

Un stock de cero es válido: significa agotado. La unicidad del nombre está acotada a la sucursal.

### Eliminar producto

`DELETE /products/{productId}`

Sin body.

**Respuestas**

| Código | Cuándo |
|---|---|
| `204` | Producto eliminado; sin body |
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

### Probar la API de punta a punta

Con la aplicación corriendo:

```bash
FID=$(curl -s -X POST http://localhost:8080/franchises \
  -H "Content-Type: application/json" \
  -d '{"name":"Mi Franquicia"}' | sed -n 's/.*"id":"\([^"]*\)".*/\1/p')

CENTRO=$(curl -s -X POST http://localhost:8080/franchises/$FID/branches \
  -H "Content-Type: application/json" \
  -d '{"name":"Centro","city":"Bogotá"}' | sed -n 's/.*"id":"\([^"]*\)".*/\1/p')

NORTE=$(curl -s -X POST http://localhost:8080/franchises/$FID/branches \
  -H "Content-Type: application/json" \
  -d '{"name":"Norte","city":"Medellín"}' | sed -n 's/.*"id":"\([^"]*\)".*/\1/p')

curl -s -o /dev/null -X POST http://localhost:8080/branches/$CENTRO/products \
  -H "Content-Type: application/json" -d '{"name":"Café","stock":40}'
curl -s -o /dev/null -X POST http://localhost:8080/branches/$CENTRO/products \
  -H "Content-Type: application/json" -d '{"name":"Té","stock":10}'
curl -s -o /dev/null -X POST http://localhost:8080/branches/$NORTE/products \
  -H "Content-Type: application/json" -d '{"name":"Pan","stock":25}'
curl -s -o /dev/null -X POST http://localhost:8080/branches/$NORTE/products \
  -H "Content-Type: application/json" -d '{"name":"Leche","stock":25}'

curl -s http://localhost:8080/franchises/$FID/top-products
```

La respuesta trae tres elementos: el Café por Centro, y el Pan y la Leche por Norte,
porque empatan en el máximo de esa sucursal.

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

Todos los errores salen con el mismo formato:

```json
{
  "error": "RESOURCE_NOT_FOUND",
  "message": "No se encontró la franquicia con id abc-123"
}
```

| HTTP | `error` | Cuándo |
|---|---|---|
| `400` | `INVALID_DATA` | Un dato no cumple las reglas, o el cuerpo está mal formado |
| `404` | `RESOURCE_NOT_FOUND` | El recurso de la ruta no existe |
| `409` | `DUPLICATE_NAME` | El nombre ya está en uso en su ámbito |
| `500` | `INTERNAL_ERROR` | Fallo no previsto |

El campo `error` permite al cliente reaccionar sin leer el mensaje. En los `400` se
añade además la lista de campos con problema:

```json
{
  "error": "INVALID_DATA",
  "message": "Datos inválidos",
  "errors": [
    { "field": "stock", "message": "El stock no puede ser negativo" }
  ]
}
```

Las reglas de negocio viven en el dominio y viajan como excepciones propias
(`ResourceNotFoundException`, `InvalidDataException`, `DuplicateNameException`), sin
saber nada de HTTP. Un único manejador las traduce a códigos de estado.

## Pruebas y cobertura

```bash
./gradlew test                # todos los módulos
./gradlew jacocoMergedReport  # informe
./gradlew build               # lo anterior + verificación del umbral
```

El informe queda en `build/reports/jacocoMergedReport/html/index.html`.

Cada módulo se prueba con lo que le corresponde:

| Módulo | Qué se prueba | Con qué |
|---|---|---|
| `model` | Que una entidad inválida no se puede construir | JUnit |
| `usecase` | Orquestación, errores y señales del flujo | Mockito + StepVerifier |
| `reactive-web` | Rutas, códigos de estado y formato de error | WebTestClient |
| `r2dbc-postgresql` | Que las escrituras escriben y la consulta consulta | Testcontainers |

Los tests de dominio y casos de uso corren en milisegundos porque no levantan nada:
los puertos se sustituyen por implementaciones falsas. Los del adaptador levantan un
PostgreSQL real en Docker, y por eso son los únicos que tardan.

**Por qué el adaptador se prueba contra una base de datos real.** La consulta de
productos destacados es SQL en una cadena de texto: ningún compilador ni mock detecta
si se rompe. Solo un motor real dice si los empates siguen saliendo. Lo mismo con dos
fallos silenciosos de R2DBC que el diseño evita pero conviene verificar: el `UPDATE`
que no encuentra filas y no falla, y las marcas de tiempo viajando en nulo.

## Flujo de trabajo con Git

**Trunk-based con ramas cortas.** Una rama por bloque de trabajo, que vive horas y se
integra a `main` por Pull Request. Nada de commits directos.

```
main
 |-- feat/project-setup
 |-- feat/domain-foundation
 |-- feat/create-franchise
 |-- feat/add-branch
 |-- ...
```

Los mensajes siguen la convención de commits, en inglés como el código: `feat: add branch endpoint`, `fix: remove leftover pitest configuration`.

La rama `main` está protegida: exige Pull Request y bloquea el `push --force`. Cada PR describe qué hace, por
qué y qué queda fuera.

## Consideraciones de diseño

### Identidad y unicidad
El identificador es un UUID, lo que permite cambiar el nombre sin romper referencias.
El nombre es único entre hermanos del mismo padre, evitando duplicados en un mismo
nivel.

### Consulta de productos destacados
Se resuelve con una sola consulta usando la función de ventana `RANK()`. Consultar los
productos sucursal por sucursal habría multiplicado las conexiones a la base de datos
con cada petición. `RANK` devuelve los empates de forma natural.

### Modificar el stock
El valor recibido reemplaza el stock, no lo incrementa. La operación es idempotente.
Con clientes concurrentes ajustando el mismo producto, una actualización puede pisar a
otra; evitarlo requeriría bloqueo optimista, fuera del alcance actual.

### Borrado
El borrado es físico. Eliminar un producto inexistente devuelve `404` en lugar de
`204`, para informar al cliente de que no había nada que borrar. Si se requiere saber
que existió, el borrado debería ser lógico.

### Persistencia
Se usa R2DBC porque JDBC bloquea el hilo, y en WebFlux pocos hilos atienden muchas
peticiones. Los puertos separan `create` y `update` porque R2DBC no distingue entre
insertar y actualizar cuando la aplicación asigna los identificadores. Las marcas de
tiempo las pone el adaptador, por la misma razón. El esquema se versiona con el
historial de git en lugar de una herramienta de migraciones, que exigiría una conexión
bloqueante. Las consultas usan parámetros enlazados, nunca concatenación de cadenas, lo que
descarta la inyección SQL.

### API
Las rutas anidan al crear, porque el padre indica dónde va el hijo, y son planas al
operar sobre un recurso existente, que su identificador ya localiza. Las respuestas van
sin envoltura: el código y el estado viajan en el protocolo.

### Flujo de trabajo con Git
Trunk-based con ramas cortas, adecuado para un solo desarrollador sin versiones en
soporte. Con un equipo manteniendo varias versiones a la vez, GitFlow sería la
recomendación.

### Alcance
El modelo es mínimo por decisión: cada entidad lleva dos atributos opcionales, y un
sistema en producción añadiría categoría, código de barras o dirección. La API expone
seis operaciones y no incluye endpoints de consulta individual ni de listado.