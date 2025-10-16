# 📡 DOCUMENTACIÓN DE ENDPOINTS

## Sistema de Logística de Transporte de Contenedores

**Versión**: 1.0  
**Fecha**: Octubre 2025  
**Entrega**: Inicial - Diseño de API

---

## 📋 Índice

1. [Arquitectura General](#arquitectura-general)
2. [Microservicio: Servicio-Flota](#microservicio-servicio-flota)
3. [Microservicio: Servicio-Operaciones](#microservicio-servicio-operaciones)
4. [API Gateway](#api-gateway)
5. [Códigos HTTP Utilizados](#códigos-http-utilizados)
6. [Mapeo de Requerimientos Funcionales](#mapeo-de-requerimientos-funcionales)

---

## 🏗️ Arquitectura General

```
Cliente/Transportista/Operador
         ↓
    API Gateway (Puerto 8080) ← [Keycloak - Autenticación JWT]
         ↓
    ┌────────────────────────────────┐
    ↓                                ↓
Servicio-Flota                 Servicio-Operaciones
(Puerto 8081)                  (Puerto 8082)
    ↓                                ↓
DB Flota (PostgreSQL)          DB Operaciones (PostgreSQL)
                                     ↓
                            Google Maps API
```

---

## 🚛 Microservicio: Servicio-Flota

**Puerto**: 8081  
**Base Path**: `/api`  
**Responsabilidad**: Gestión de recursos físicos (Camiones, Depósitos, Tarifas)

### 📦 Recurso: Camiones

| Método | Endpoint | Rol | Descripción | Entrada | Salida | HTTP | Estado |
|--------|----------|-----|-------------|---------|--------|------|--------|
| **GET** | `/camiones` | Operador | Listar todos los camiones | - | `List<CamionDTO>` | 200 | ✅ Implementado |
| **GET** | `/camiones/{id}` | Operador | Obtener camión por ID | `id: Long` (PathVariable) | `CamionDTO` | 200, 404 | ✅ Implementado |
| **GET** | `/camiones/disponibles` | Operador | Listar camiones disponibles con filtros | Query: `pesoMinimo: double`, `volumenMinimo: double` | `List<CamionDTO>` | 200 | ✅ Implementado |
| **POST** | `/camiones` | Operador | Registrar nuevo camión | `CamionCreateDTO` (body) | `CamionDTO` | 201, 400 | ✅ Implementado |
| **PUT** | `/camiones/{id}` | Operador | Actualizar datos de camión | `id: Long`, `CamionUpdateDTO` (body) | `CamionDTO` | 200, 404, 400 | ✅ Implementado |
| **PATCH** | `/camiones/{id}/disponibilidad` | Sistema | Cambiar disponibilidad del camión | `id: Long`, `DisponibilidadDTO` (body) | `CamionDTO` | 200, 404 | 🟡 Pendiente (Lógica) |
| **DELETE** | `/camiones/{id}` | Operador | Eliminar camión | `id: Long` | - | 204, 404 | ✅ Implementado |

#### DTOs - Camiones *(✅ Implementado)*

**Estado de DTOs**: 🟡 Pendiente (Lógica) - Requiere implementación de capa de transferencia de datos

```java
// Entrada
CamionCreateDTO {
    String dominio;                    // REQUERIDO - Patente única
    String nombreTransportista;        // REQUERIDO
    String telefono;                   // REQUERIDO
    double capacidadPeso;              // REQUERIDO - en kg
    double capacidadVolumen;           // REQUERIDO - en m³
    double consumoCombustiblePorKm;    // REQUERIDO - litros/km
    double costoPorKm;                 // REQUERIDO - $/km
}

CamionUpdateDTO {
    String nombreTransportista;        // OPCIONAL
    String telefono;                   // OPCIONAL
    double consumoCombustiblePorKm;    // OPCIONAL
    double costoPorKm;                 // OPCIONAL
}

DisponibilidadDTO {
    boolean disponible;                // true = libre, false = ocupado
}

// Salida
CamionDTO {
    Long id;
    String dominio;
    String nombreTransportista;
    String telefono;
    double capacidadPeso;
    double capacidadVolumen;
    boolean disponible;
    double consumoCombustiblePorKm;
    double costoPorKm;
}
```

---

### 🏢 Recurso: Depósitos

| Método | Endpoint | Rol | Descripción | Entrada | Salida | HTTP | Estado |
|--------|----------|-----|-------------|---------|--------|------|--------|
| **GET** | `/depositos` | Operador | Listar todos los depósitos | - | `List<DepositoDTO>` | 200 | ✅ Implementado |
| **GET** | `/depositos/{id}` | Operador | Obtener depósito por ID | `id: Long` | `DepositoDTO` | 200, 404 | ✅ Implementado |
| **POST** | `/depositos` | Operador | Registrar nuevo depósito | `DepositoCreateDTO` (body) | `DepositoDTO` | 201, 400 | ✅ Implementado |
| **PUT** | `/depositos/{id}` | Operador | Actualizar datos de depósito | `id: Long`, `DepositoUpdateDTO` (body) | `DepositoDTO` | 200, 404, 400 | ✅ Implementado |
| **DELETE** | `/depositos/{id}` | Operador | Eliminar depósito | `id: Long` | - | 204, 404 | ✅ Implementado |

#### DTOs - Depósitos *(✅ Implementado)*

**Estado de DTOs**: 🟡 Pendiente (Lógica) - Requiere implementación de capa de transferencia de datos

```java
// Entrada
DepositoCreateDTO {
    String nombre;            // REQUERIDO
    String direccion;         // REQUERIDO
    double latitud;           // REQUERIDO - formato: -90 a 90
    double longitud;          // REQUERIDO - formato: -180 a 180
    double costoEstadiaDiaria; // REQUERIDO - $/día
}

DepositoUpdateDTO {
    String nombre;            // OPCIONAL
    String direccion;         // OPCIONAL
    double costoEstadiaDiaria; // OPCIONAL
}

// Salida
DepositoDTO {
    Long id;
    String nombre;
    String direccion;
    double latitud;
    double longitud;
    double costoEstadiaDiaria;
}
```

---

### 💰 Recurso: Tarifas

| Método | Endpoint | Rol | Descripción | Entrada | Salida | HTTP | Estado |
|--------|----------|-----|-------------|---------|--------|------|--------|
| **GET** | `/tarifas/actual` | Todos | Obtener tarifa activa vigente | - | `TarifaDTO` | 200, 404 | ✅ Implementado |
| **GET** | `/tarifas` | Operador | Listar todas las tarifas (históricas) | - | `List<TarifaDTO>` | 200 | ✅ Implementado |
| **GET** | `/tarifas/{id}` | Operador | Obtener tarifa por ID | `id: Long` | `TarifaDTO` | 200, 404 | ✅ Implementado |
| **POST** | `/tarifas` | Operador | Crear nueva tarifa | `TarifaCreateDTO` (body) | `TarifaDTO` | 201, 400 | ✅ Implementado |
| **PUT** | `/tarifas/{id}` | Operador | Actualizar tarifa | `id: Long`, `TarifaUpdateDTO` (body) | `TarifaDTO` | 200, 404, 400 | ✅ Implementado |
| **DELETE** | `/tarifas/{id}` | Operador | Eliminar tarifa (solo si no está activa) | `id: Long` | - | 204, 404, 400 | ✅ Implementado |

**✅ IMPLEMENTADO**: Recurso completo con arquitectura de 4 capas, DTOs, validaciones y lógica de negocio para gestión de tarifas activas.

#### DTOs - Tarifas *(✅ Implementado)*

**Estado de DTOs**: ✅ Implementado - DTOs completos con validaciones Jakarta Validation

```java
// Entrada
TarifaCreateDTO {
    double costoKmBase;             // REQUERIDO - Costo base por km
    double precioLitroCombustible;  // REQUERIDO - Precio actual del litro
    double cargoGestionPorTramo;    // REQUERIDO - Cargo fijo por tramo
    LocalDateTime vigenciaDesde;    // REQUERIDO - Fecha inicio vigencia
}

TarifaUpdateDTO {
    double precioLitroCombustible;  // OPCIONAL
    double cargoGestionPorTramo;    // OPCIONAL
    LocalDateTime vigenciaHasta;    // OPCIONAL - Para cerrar vigencia
    boolean activa;                 // OPCIONAL
}

// Salida
TarifaDTO {
    Long id;
    double costoKmBase;
    double precioLitroCombustible;
    double cargoGestionPorTramo;
    LocalDateTime vigenciaDesde;
    LocalDateTime vigenciaHasta;
    boolean activa;
}
```

---

## 🚀 Microservicio: Servicio-Operaciones

**Puerto**: 8082  
**Base Path**: `/api`  
**Responsabilidad**: Gestión de operaciones de negocio (Clientes, Solicitudes, Rutas, Tramos)

### 👤 Recurso: Clientes

| Método | Endpoint | Rol | Descripción | Entrada | Salida | HTTP | Estado |
|--------|----------|-----|-------------|---------|--------|------|--------|
| **GET** | `/clientes` | Operador | Listar todos los clientes | - | `List<ClienteDTO>` | 200 | ✅ Implementado |
| **GET** | `/clientes/{id}` | Operador | Obtener cliente por ID | `id: Long` | `ClienteDTO` | 200, 404 | ✅ Implementado |
| **POST** | `/clientes` | Cliente, Operador | Registrar nuevo cliente | `ClienteCreateDTO` (body) | `ClienteDTO` | 201, 400, 409 | ✅ Implementado |
| **PUT** | `/clientes/{id}` | Cliente, Operador | Actualizar datos de cliente | `id: Long`, `ClienteUpdateDTO` (body) | `ClienteDTO` | 200, 404, 400 | ✅ Implementado |

#### DTOs - Clientes *(✅ Implementado)*

**Estado de DTOs**: 🟡 Pendiente (Lógica) - Requiere implementación de capa de transferencia de datos

```java
// Entrada
ClienteCreateDTO {
    String nombre;     // REQUERIDO
    String email;      // REQUERIDO - único
    String telefono;   // REQUERIDO
    String direccion;  // REQUERIDO
    String cuit;       // REQUERIDO - único
}

ClienteUpdateDTO {
    String nombre;     // OPCIONAL
    String telefono;   // OPCIONAL
    String direccion;  // OPCIONAL
}

// Salida
ClienteDTO {
    Long id;
    String nombre;
    String email;
    String telefono;
    String direccion;
    String cuit;
}
```

---

### 📦 Recurso: Contenedores

| Método | Endpoint | Rol | Descripción | Entrada | Salida | HTTP | Estado |
|--------|----------|-----|-------------|---------|--------|------|--------|
| **GET** | `/contenedores` | Operador | Listar contenedores con filtros | Query: `estado`, `clienteId` | `List<ContenedorDTO>` | 200 | ✅ Implementado |
| **GET** | `/contenedores/{id}` | Cliente, Operador | Obtener contenedor por ID | `id: Long` | `ContenedorDTO` | 200, 404 | ✅ Implementado |
| **GET** | `/contenedores/{id}/estado` | Cliente | **[RF#2]** Consultar estado de contenedor (seguimiento) | `id: Long` | `ContenedorEstadoDTO` | 200, 404 | 🟡 Pendiente (Lógica) |
| **GET** | `/contenedores/pendientes` | Operador | **[RF#6]** Consultar contenedores pendientes de entrega | Query: `depositoId`, `estado` | `List<ContenedorPendienteDTO>` | 200 | 🟡 Pendiente (Lógica) |

#### DTOs - Contenedores *(✅ Implementado)*

**Estado de DTOs**: 🟡 Pendiente (Lógica) - Requiere implementación de DTOs específicos para seguimiento y estado

```java
// Salida
ContenedorDTO {
    Long id;
    String numero;
    String tipo;
    double peso;
    double volumen;
    String estado;
    String direccionOrigen;
    double latitudOrigen;
    double longitudOrigen;
    String direccionDestino;
    double latitudDestino;
    double longitudDestino;
    ClienteDTO cliente;
}

ContenedorEstadoDTO {
    Long id;
    String numero;
    String estado;                    // EN_ORIGEN, EN_VIAJE, EN_DEPOSITO, ENTREGADO
    String ubicacionActual;           // Descripción textual
    TramoDTO tramoActual;             // Tramo en curso (si aplica)
    DepositoDTO depositoActual;       // Depósito actual (si aplica)
}

ContenedorPendienteDTO {
    Long id;
    String numero;
    String estado;
    String ubicacionActual;
    ClienteDTO cliente;
    SolicitudDTO solicitud;
}
```

---

### 📋 Recurso: Solicitudes

| Método | Endpoint | Rol | Descripción | Entrada | Salida | HTTP | Estado |
|--------|----------|-----|-------------|---------|--------|------|--------|
| **POST** | `/solicitudes` | Cliente | **[RF#1]** Registrar nueva solicitud de transporte | `SolicitudCreateDTO` (body) | `SolicitudDTO` | 201, 400 | ✅ Implementado |
| **GET** | `/solicitudes` | Operador | Listar todas las solicitudes | Query: `estado`, `clienteId` | `List<SolicitudDTO>` | 200 | ✅ Implementado |
| **GET** | `/solicitudes/{id}` | Cliente, Operador | Obtener solicitud por ID | `id: Long` | `SolicitudDTO` | 200, 404 | ✅ Implementado |
| **GET** | `/solicitudes/{id}/estado` | Cliente | **[RF#2]** Consultar estado del transporte | `id: Long` | `SolicitudEstadoDTO` | 200, 404 | 🟡 Pendiente (Lógica) |
| **PUT** | `/solicitudes/{id}/estado` | Operador, Sistema | Actualizar estado de solicitud | `id: Long`, `EstadoUpdateDTO` (body) | `SolicitudDTO` | 200, 404, 400 | 🟡 Pendiente (Lógica) |
| **PATCH** | `/solicitudes/{id}/finalizar` | Sistema | **[RF#10]** Registrar costos y tiempos finales | `id: Long`, `FinalizacionDTO` (body) | `SolicitudDTO` | 200, 404 | 🟡 Pendiente (Lógica) |

#### DTOs - Solicitudes *(🟡 Pendiente - Lógica)*

**Estado de DTOs**: 🟡 Pendiente (Lógica) - Requiere implementación de DTOs complejos con relaciones y seguimiento

```java
// Entrada
SolicitudCreateDTO {
    ContenedorCreateDTO contenedor;   // REQUERIDO - Se crea el contenedor
    ClienteCreateDTO cliente;         // OPCIONAL - Si es cliente nuevo
    Long clienteId;                   // OPCIONAL - Si cliente ya existe
    String observaciones;             // OPCIONAL
}

ContenedorCreateDTO {
    String numero;                    // REQUERIDO - único
    String tipo;                      // REQUERIDO - STANDARD, REFRIGERADO, etc.
    double peso;                      // REQUERIDO - en kg
    double volumen;                   // REQUERIDO - en m³
    String direccionOrigen;           // REQUERIDO
    double latitudOrigen;             // REQUERIDO
    double longitudOrigen;            // REQUERIDO
    String direccionDestino;          // REQUERIDO
    double latitudDestino;            // REQUERIDO
    double longitudDestino;           // REQUERIDO
}

EstadoUpdateDTO {
    String estado;                    // BORRADOR, PROGRAMADA, EN_TRANSITO, ENTREGADA
}

FinalizacionDTO {
    double costoFinal;                // REQUERIDO
    double tiempoReal;                // REQUERIDO - en horas
}

// Salida
SolicitudDTO {
    Long id;
    String numero;
    LocalDateTime fechaSolicitud;
    String estado;
    String observaciones;
    ClienteDTO cliente;
    ContenedorDTO contenedor;
    RutaDTO ruta;
    double costoEstimado;
    double tiempoEstimado;
    double costoFinal;
    double tiempoReal;
}

SolicitudEstadoDTO {
    Long id;
    String numero;
    String estado;
    ContenedorEstadoDTO contenedor;
    RutaDTO rutaActual;
    List<TramoHistorialDTO> historialTramos;  // Estados cronológicos
    double progreso;                           // Porcentaje 0-100
}

TramoHistorialDTO {
    int orden;
    String tipo;
    String estado;
    String puntoInicio;
    String puntoFin;
    LocalDateTime fechaHoraInicio;
    LocalDateTime fechaHoraFin;
    String camion;
}
```

---

### 🛣️ Recurso: Rutas

| Método | Endpoint | Rol | Descripción | Entrada | Salida | HTTP | Estado |
|--------|----------|-----|-------------|---------|--------|------|--------|
| **GET** | `/solicitudes/{solicitudId}/rutas/tentativas` | Operador | **[RF#3]** Consultar rutas tentativas con cálculos | `solicitudId: Long` | `List<RutaTentativaDTO>` | 200, 404 | 🟡 Pendiente (Lógica) |
| **POST** | `/solicitudes/{solicitudId}/rutas` | Operador | **[RF#4]** Asignar ruta con tramos a solicitud | `solicitudId: Long`, `RutaCreateDTO` (body) | `RutaDTO` | 201, 400, 404 | 🟡 Pendiente (Lógica) |
| **GET** | `/rutas/{id}` | Operador | Obtener ruta por ID | `id: Long` | `RutaDTO` | 200, 404 | ✅ Implementado |

#### DTOs - Rutas *(🟡 Pendiente - Lógica)*

```java
// Entrada
RutaCreateDTO {
    List<TramoCreateDTO> tramos;      // REQUERIDO - Lista ordenada de tramos
}

TramoCreateDTO {
    int orden;                        // REQUERIDO - 1, 2, 3...
    String tipo;                      // REQUERIDO
    double latitudInicio;             // REQUERIDO
    double longitudInicio;            // REQUERIDO
    double latitudFin;                // REQUERIDO
    double longitudFin;               // REQUERIDO
    Long depositoOrigenId;            // OPCIONAL
    Long depositoDestinoId;           // OPCIONAL
    LocalDateTime fechaEstimadaInicio; // REQUERIDO
    LocalDateTime fechaEstimadaFin;    // REQUERIDO
}

// Salida
RutaTentativaDTO {
    List<TramoTentativoDTO> tramos;
    double costoEstimado;
    double tiempoEstimado;             // en horas
    double distanciaTotal;             // en km
    int cantidadTramos;
    int cantidadDepositos;
}

TramoTentativoDTO {
    int orden;
    String tipo;
    String puntoInicio;
    String puntoFin;
    double distanciaKm;
    double tiempoEstimadoHoras;
    double costoAproximado;
    DepositoDTO depositoOrigen;
    DepositoDTO depositoDestino;
}

RutaDTO {
    Long id;
    Long solicitudId;
    int cantidadTramos;
    int cantidadDepositos;
    double distanciaTotal;
    double tiempoEstimadoTotal;
    double costoEstimado;
    List<TramoDTO> tramos;
}
```

---

### 🛤️ Recurso: Tramos

| Método | Endpoint | Rol | Descripción | Entrada | Salida | HTTP | Estado |
|--------|----------|-----|-------------|---------|--------|------|--------|
| **GET** | `/tramos` | Transportista, Operador | Listar tramos con filtros | Query: `rutaId`, `camionId`, `estado` | `List<TramoDTO>` | 200 | ✅ Implementado |
| **GET** | `/tramos/{id}` | Transportista, Operador | Obtener tramo por ID | `id: Long` | `TramoDTO` | 200, 404 | ✅ Implementado |
| **POST** | `/tramos/{id}/asignar-camion` | Operador | **[RF#7]** Asignar camión a tramo | `id: Long`, `AsignacionCamionDTO` (body) | `TramoDTO` | 200, 400, 404 | 🟡 Pendiente (Lógica) |
| **POST** | `/tramos/{id}/iniciar` | Transportista | **[RF#8]** Registrar inicio de tramo | `id: Long`, `InicioTramoDTO` (body) | `TramoDTO` | 200, 400, 404 | 🟡 Pendiente (Lógica) |
| **POST** | `/tramos/{id}/finalizar` | Transportista | **[RF#8]** Registrar fin de tramo | `id: Long`, `FinTramoDTO` (body) | `TramoDTO` | 200, 400, 404 | 🟡 Pendiente (Lógica) |
| **GET** | `/transportistas/{id}/tramos` | Transportista | Ver tramos asignados a transportista | `id: Long` | `List<TramoDTO>` | 200 | 🟡 Pendiente (Lógica) |

#### DTOs - Tramos *(🟡 Pendiente - Lógica)*

```java
// Entrada
AsignacionCamionDTO {
    Long camionId;                    // REQUERIDO - ID del camión en servicio-flota
}

InicioTramoDTO {
    LocalDateTime fechaHoraInicio;    // OPCIONAL - Si no se envía, usa fecha actual
}

FinTramoDTO {
    LocalDateTime fechaHoraFin;       // OPCIONAL - Si no se envía, usa fecha actual
    String observaciones;             // OPCIONAL
}

// Salida
TramoDTO {
    Long id;
    Long rutaId;
    int orden;
    String tipo;
    String estado;
    String puntoInicio;
    double latitudInicio;
    double longitudInicio;
    String puntoFin;
    double latitudFin;
    double longitudFin;
    double distanciaKm;
    double tiempoEstimadoHoras;
    double costoAproximado;
    double costoReal;
    LocalDateTime fechaEstimadaInicio;
    LocalDateTime fechaEstimadaFin;
    LocalDateTime fechaHoraInicio;
    LocalDateTime fechaHoraFin;
    CamionReferenceDTO camion;
    DepositoReferenceDTO depositoOrigen;
    DepositoReferenceDTO depositoDestino;
}
```

---

### 🔄 Recurso: Referencias (Sincronización entre servicios)

| Método | Endpoint | Rol | Descripción | Entrada | Salida | HTTP | Estado |
|--------|----------|-----|-------------|---------|--------|------|--------|
| **POST** | `/camiones-reference/sync` | Sistema | Sincronizar camión desde servicio-flota | `CamionSyncDTO` (body) | `CamionReferenceDTO` | 201, 400 | 🟡 Pendiente (Lógica) |
| **GET** | `/camiones-reference` | Sistema | Listar referencias de camiones | - | `List<CamionReferenceDTO>` | 200 | ✅ Implementado |
| **POST** | `/depositos-reference/sync` | Sistema | Sincronizar depósito desde servicio-flota | `DepositoSyncDTO` (body) | `DepositoReferenceDTO` | 201, 400 | 🟡 Pendiente (Lógica) |
| **GET** | `/depositos-reference` | Sistema | Listar referencias de depósitos | - | `List<DepositoReferenceDTO>` | 200 | ✅ Implementado |

---

### 🧮 Recurso: Cálculos

| Método | Endpoint | Rol | Descripción | Entrada | Salida | HTTP | Estado |
|--------|----------|-----|-------------|---------|--------|------|--------|
| **POST** | `/calculos/costo` | Sistema, Operador | **[RF#9]** Calcular costo de ruta/tramo | `CalculoCostoRequestDTO` (body) | `CalculoCostoResponseDTO` | 200, 400 | 🟡 Pendiente (Lógica) |
| **POST** | `/calculos/distancia` | Sistema | Consultar distancia entre puntos (Google Maps) | `DistanciaRequestDTO` (body) | `DistanciaResponseDTO` | 200, 400, 503 | 🟡 Pendiente (Lógica) |

#### DTOs - Cálculos *(🟡 Pendiente - Lógica)*

```java
// Entrada
CalculoCostoRequestDTO {
    Long contenedorId;                // REQUERIDO
    List<Long> tramoIds;              // REQUERIDO
}

DistanciaRequestDTO {
    CoordenadaDTO origen;             // REQUERIDO
    CoordenadaDTO destino;            // REQUERIDO
}

CoordenadaDTO {
    double latitud;
    double longitud;
}

// Salida
CalculoCostoResponseDTO {
    double costoTotal;
    DesgloseDTO desglose;
}

DesgloseDTO {
    double costoCombustible;
    double costoKilometraje;
    double costoEstadia;
    double cargoGestion;
    List<CostoTramoDTO> costosPorTramo;
}

CostoTramoDTO {
    int orden;
    double distanciaKm;
    double costoCombustible;
    double costoKilometraje;
    double costoEstadia;
}

DistanciaResponseDTO {
    double distanciaKm;
    double tiempoEstimadoHoras;
    String proveedor;                 // "Google Maps API"
}
```

---

## 🌐 API Gateway

**Puerto**: 8080 (Pendiente implementación - Entrega Final)  
**Tecnología**: Spring Cloud Gateway  
**Responsabilidades**:
- Punto de entrada único
- Enrutamiento a microservicios
- Validación de tokens JWT (Keycloak)
- Rate limiting
- CORS

### Rutas de Enrutamiento

| Ruta Externa | Microservicio Destino | Puerto | Función |
|--------------|----------------------|--------|---------|
| `/api/camiones/**` | servicio-flota | 8081 | Gestión de camiones |
| `/api/depositos/**` | servicio-flota | 8081 | Gestión de depósitos |
| `/api/tarifas/**` | servicio-flota | 8081 | Gestión de tarifas |
| `/api/clientes/**` | servicio-operaciones | 8082 | Gestión de clientes |
| `/api/contenedores/**` | servicio-operaciones | 8082 | Gestión de contenedores |
| `/api/solicitudes/**` | servicio-operaciones | 8082 | Gestión de solicitudes |
| `/api/rutas/**` | servicio-operaciones | 8082 | Gestión de rutas |
| `/api/tramos/**` | servicio-operaciones | 8082 | Gestión de tramos |
| `/api/transportistas/**` | servicio-operaciones | 8082 | Consultas de transportistas |
| `/api/calculos/**` | servicio-operaciones | 8082 | Servicios de cálculo |

---

## 📊 Códigos HTTP Utilizados

| Código | Descripción | Uso |
|--------|-------------|-----|
| **200** | OK | Operación exitosa (GET, PUT, PATCH) |
| **201** | Created | Recurso creado exitosamente (POST) |
| **204** | No Content | Recurso eliminado exitosamente (DELETE) |
| **400** | Bad Request | Datos de entrada inválidos o validación fallida |
| **401** | Unauthorized | Token JWT inválido o ausente |
| **403** | Forbidden | Usuario sin permisos para el recurso |
| **404** | Not Found | Recurso no encontrado |
| **409** | Conflict | Conflicto (ej: email duplicado, CUIT existente) |
| **500** | Internal Server Error | Error interno del servidor |
| **503** | Service Unavailable | Servicio externo no disponible (ej: Google Maps) |

---

## 🎯 Mapeo de Requerimientos Funcionales

Según **Enunciado - Requerimientos Funcionales Mínimos**:

| RF# | Requerimiento | Endpoint(s) | Microservicio |
|-----|--------------|------------|---------------|
| **RF#1** | Registrar nueva solicitud de transporte | `POST /solicitudes` | Operaciones |
| **RF#2** | Consultar estado del transporte | `GET /solicitudes/{id}/estado`<br>`GET /contenedores/{id}/estado` | Operaciones |
| **RF#3** | Consultar rutas tentativas | `GET /solicitudes/{id}/rutas/tentativas` | Operaciones |
| **RF#4** | Asignar ruta con tramos | `POST /solicitudes/{id}/rutas` | Operaciones |
| **RF#5** | Consultar contenedores pendientes | `GET /contenedores/pendientes` | Operaciones |
| **RF#6** | Asignar camión a tramo | `POST /tramos/{id}/asignar-camion` | Operaciones |
| **RF#7** | Determinar inicio/fin de tramo | `POST /tramos/{id}/iniciar`<br>`POST /tramos/{id}/finalizar` | Operaciones |
| **RF#8** | Calcular costo total | `POST /calculos/costo` | Operaciones |
| **RF#9** | Registrar costo/tiempo final | `PATCH /solicitudes/{id}/finalizar` | Operaciones |
| **RF#10** | Registrar/actualizar depósitos, camiones, tarifas | `POST/PUT/DELETE /camiones`<br>`POST/PUT/DELETE /depositos`<br>`POST/PUT /tarifas` | Flota |
| **RF#11** | Validar capacidad de camión | Lógica interna en asignación | Operaciones |

---

## 🔐 Seguridad y Roles (Entrega Final)

### Roles definidos:

1. **Cliente**
   - Puede crear solicitudes
   - Puede consultar estado de sus contenedores/solicitudes
   - Puede registrar/actualizar sus datos

2. **Transportista**
   - Puede ver sus tramos asignados
   - Puede registrar inicio/fin de tramos
   - Solo acceso a sus propios datos

3. **Operador/Administrador**
   - Acceso completo a gestión de recursos
   - Puede asignar rutas y camiones
   - Puede consultar todos los datos del sistema

4. **Sistema** (Internal)
   - Comunicación entre microservicios
   - Sincronización de referencias
   - Cálculos automáticos

---

## 📝 Notas Adicionales

### Validaciones Importantes:

1. **Al crear solicitud**:
   - Validar que cliente existe o crear uno nuevo
   - Validar coordenadas (formato lat/lng válido)
   - Validar peso y volumen > 0

2. **Al asignar camión a tramo**:
   - Validar que camión esté disponible
   - Validar que capacidad del camión sea suficiente (peso y volumen)
   - Cambiar estado del tramo a "ASIGNADO"
   - Marcar camión como no disponible

3. **Al iniciar tramo**:
   - Validar que tramo tenga camión asignado
   - Validar que estado sea "ASIGNADO"
   - Cambiar estado a "INICIADO"
   - Actualizar estado del contenedor

4. **Al finalizar tramo**:
   - Validar que estado sea "INICIADO"
   - Calcular costo real del tramo
   - Cambiar estado a "FINALIZADO"
   - Marcar camión como disponible
   - Si es el último tramo, finalizar solicitud

### Comunicación entre Microservicios:

- **Servicio-Operaciones → Servicio-Flota**:
  - Consultar camiones disponibles
  - Consultar depósitos
  - Consultar tarifa activa
  - Actualizar disponibilidad de camiones

- **Sincronización**:
  - Al crear/actualizar camión en Flota → Sincronizar en Operaciones
  - Al crear/actualizar depósito en Flota → Sincronizar en Operaciones

### Integración con Google Maps:

- Endpoint interno: `POST /calculos/distancia`
- Parámetros: coordenadas origen y destino
- Respuesta: distancia en km y tiempo estimado
- Manejo de errores: timeout, límite de API, servicio caído

---

**Fin del Documento**

