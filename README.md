# 💳 BFF Payments - Flypass

## 🧭 Descripción General
El BFF de Pagos es un Backend For Frontend (BFF) encargado de orquestar las operaciones de pago dentro del ecosistema Flypass.

Su objetivo principal es actuar como una capa intermedia entre los frontends y los microservicios de pago internos (por ejemplo, Bancolombia, Pagos Externos, etc.), garantizando un enrutamiento dinámico, resiliencia y observabilidad.

---

## ⚙️ Arquitectura General
El BFF sigue un enfoque modular y extensible, permitiendo agregar nuevos proveedores de pago sin modificar el código existente.

La selección del proveedor se realiza dinámicamente en tiempo de ejecución mediante el Router interno.

### 🧩 Flujo Simplificado
```text
Frontend
   ↓
PaymentsController
   ↓
PaymentsService
   ↓
RouteResolver → determina el serviceId (por header o default)
   ↓
ServiceClientRegistry → obtiene el cliente correspondiente
   ↓
ServiceClient (Bancolombia, Externo, etc.)
   ↓
Microservicio interno
```

### 🔀 Router Interno
El componente Router es el encargado de decidir, en cada petición, a qué microservicio de pagos debe enviarse la solicitud.

#### 📖 Reglas de resolución
- Header HTTP
  - Formato oficial: `X-Service-Id: <id>`
- Valor por defecto
  - Si el header no está presente, se usa `payments.default-service-id` definido en `application.yml`.
- 🔧 Normalización
  - Se eliminan espacios y se convierte el valor a minúsculas.
- Validación
  - Si el valor no coincide con un servicio configurado, se lanza `UnknownServiceIdException`.

#### 🧠 Ejemplo
| Header                | Resultado del enrutamiento                     |
|----------------------|-----------------------------------------------|
| `X-Service-Id: Bancolombia` | Client: `BancolombiaServiceClient`          |
| (sin header)         | Client: `default-service-id` del YAML (p. ej. `bancolombia`) |

---

## 🧾 Endpoint Principal
`GET /wallet/{walletId}/payment-methods`

Obtiene los métodos de pago asociados a una wallet.

### Headers obligatorios
```http
Authorization: Bearer <token>
X-Service-Id: bancolombia
```

### Ejemplo de respuesta (200 OK)
```json
[
  {
    "brand": "Visa",
    "brandIconUrl": "https://example.com/visa.png",
    "productLabel": "Tarjeta débito",
    "isDefault": true,
    "holderName": "Juan Pérez",
    "maskedNumber": "**** 1234"
  }
]
```

### Códigos de respuesta
| Código | Descripción                          |
|-------|--------------------------------------|
| 200   | OK                                   |
| 400   | Parámetros inválidos                 |
| 401   | Falta de autenticación               |
| 503   | Servicio del proveedor no disponible |

---

## 📊 Observabilidad y Actuator
El BFF expone métricas y salud del sistema mediante Spring Boot Actuator y Micrometer.

### 🔍 Endpoints disponibles
| Endpoint                                           | Descripción                                  |
|---------------------------------------------------|----------------------------------------------|
| `/actuator/health`                                | Estado general y detalle de circuit breakers |
| `/actuator/metrics`                               | Métricas generales de la aplicación          |
| `/actuator/metrics/payments.client.list.count`    | Total de invocaciones a clientes de pago     |
| `/actuator/metrics/payments.client.list.latency`  | Latencia promedio por proveedor              |
| `/actuator/prometheus`                            | Export Prometheus (si está habilitado)       |

### 🧩 Etiquetas comunes
- `app=bff-payments`
- `env=${spring.profiles.active:local}`
- `provider`
- `result`

---

## 🧰 OpenAPI / Swagger
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- Especificación JSON: http://localhost:8080/v3/api-docs
- Archivo local: `docs/api/openapi.yaml`

---

## 🧱 Configuración Clave (application.yml)
```yaml
payments:
  default-service-id: bancolombia
  services-by-id:
    bancolombia:
      base-url: https://test.security.flypass.co/flypass/bancolombia-integration
      connect-timeout: 1s
      read-timeout: 2s
      max-attempts-get: 3
    external:
      base-url: https://example.com/external-payments
      connect-timeout: 1s
      read-timeout: 2s
      max-attempts-get: 2

resilience4j:
  circuitbreaker:
    instances:
      bancolombia:
        registerHealthIndicator: true
        failureRateThreshold: 50
        waitDurationInOpenState: 10s
      external:
        registerHealthIndicator: true
        failureRateThreshold: 50
        waitDurationInOpenState: 5s
  retry:
    instances:
      bancolombia:
        maxAttempts: 3
        waitDuration: 200ms
      external:
        maxAttempts: 2
        waitDuration: 100ms
```

---

## 🧪 Cómo ejecutar (modo local)
```bash
./mvnw clean spring-boot:run
```
Perfil: `local`

Verificación:
- http://localhost:8080/actuator/health
- http://localhost:8080/swagger-ui/index.html
- http://localhost:8080/actuator/metrics/payments.client.list.count

---

## 📈 Cobertura (Jacoco)
Configurado con umbral de 80% mínimo por paquete.

Generar reportes:
```bash
./mvnw clean verify
```
- Reporte HTML: `target/site/jacoco/index.html`
- Resumen Markdown: `docs/reports/jacoco-summary.md`

---

## 📦 Estructura de Paquetes
```text
com.co.flypass.payments.bff
 ├── adapter/               → Normalización y adaptación por proveedor
 ├── client/                → HTTP clients (extienden AbstractServiceClient)
 ├── controller/            → Entradas REST (Controllers)
 ├── exception/             → Manejo global de errores
 ├── mapper/                → Mappers (MapStruct)
 ├── model/                 → Modelos internos
 ├── router/                → Router dinámico y registro de clientes
 ├── service/               → Lógica principal y orquestación
 ├── observability/         → Métricas y Actuator
 └── config/                → Configuración (Resilience4j, beans, etc.)
```