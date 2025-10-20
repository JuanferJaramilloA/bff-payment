
# Router en el BFF

Componente Router interno del BFF de Pagos

El BFF de Pagos incluye un router interno responsable de decidir, en tiempo de ejecución, a qué microservicio interno de pagos debe enviarse cada solicitud (por ejemplo, Bancolombia, Pagos Externos, etc).

Propósito

El router permite que el BFF delegue las peticiones al servicio interno correcto sin condicionar la lógica de negocio.
Esto lo hace más extensible y mantenible, ya que para agregar un nuevo servicio solo se debe implementar un nuevo client y declararlo en la configuración.

Claves:

El router no aplica reglas de negocio.

Todos los servicios que enruta son internos del ecosistema Flypass.

La selección del destino se hace por petición (en tiempo de ejecución).

Cómo decide el destino

El router resuelve un identificador (serviceId) que indica a qué servicio interno se debe dirigir la solicitud.
Aplica las siguientes reglas, en orden de prioridad:

Header HTTP

Formato oficial: X-Service: <id>

Valor por defecto

Si no se envía el header, se usa payments.defaultServiceId configurado en application.yml.

Normalización:

Se eliminan espacios y se convierte el valor a minúsculas.

Si el valor no coincide con ningún servicio configurado, se lanza una excepción UnknownServiceException.

Flujo interno simplificado
Frontend
↓
PaymentsController
↓
PaymentsService
↓
RouteResolver → determina el serviceId (header o default)
↓
ServiceClientRegistry → obtiene el client correspondiente
↓
ServiceClient (Bancolombia, Wompi, etc.)
↓
Microservicio interno

# Router en el BFF

Componente Router interno del BFF de Pagos

El BFF de Pagos incluye un router interno responsable de decidir, en tiempo de ejecución, a qué microservicio interno de pagos debe enviarse cada solicitud (por ejemplo, Bancolombia, Pagos Externos, etc).

Propósito

El router permite que el BFF delegue las peticiones al servicio interno correcto sin condicionar la lógica de negocio.
Esto lo hace más extensible y mantenible, ya que para agregar un nuevo servicio solo se debe implementar un nuevo client y declararlo en la configuración.

Claves:

El router no aplica reglas de negocio.

Todos los servicios que enruta son internos del ecosistema Flypass.

La selección del destino se hace por petición (en tiempo de ejecución).

Cómo decide el destino

El router resuelve un identificador (serviceId) que indica a qué servicio interno se debe dirigir la solicitud.
Aplica las siguientes reglas, en orden de prioridad:

Header HTTP

Formato oficial: X-Service: <id>

Valor por defecto

Si no se envía el header, se usa payments.defaultServiceId configurado en application.yml.

Normalización:

Se eliminan espacios y se convierte el valor a minúsculas.

Si el valor no coincide con ningún servicio configurado, se lanza una excepción UnknownServiceException.

Flujo interno simplificado
Frontend
↓
PaymentsController
↓
PaymentsService
↓
RouteResolver → determina el serviceId (header o default)
↓
ServiceClientRegistry → obtiene el client correspondiente
↓
ServiceClient (Bancolombia, Wompi, etc.)
↓
Microservicio interno

---

Observabilidad y Actuator

- Dependencias clave: spring-boot-starter-actuator, micrometer-core, micrometer-registry-prometheus.
- Common tags configuradas: app=bff-payments, env=${spring.profiles.active:local}.
- Endpoints expuestos:
  - GET /actuator/health (incluye detalle de circuit breakers)
  - GET /actuator/metrics
  - GET /actuator/metrics/payments.client.list.count
  - GET /actuator/metrics/payments.client.list.latency
  - GET /actuator/prometheus (si Prometheus registry está habilitado)

OpenAPI / Swagger

- Swagger UI: /swagger-ui/index.html
- Especificación OpenAPI: /v3/api-docs y archivo docs/api/openapi.yaml
- Endpoint documentado: GET /wallets/{walletId}/payment-methods (200/4xx/5xx)

Cómo ejecutar (local)

- Perfil por defecto: local
- Arrancar la app y comprobar:
  - http://localhost:8080/actuator/health
  - http://localhost:8080/swagger-ui/index.html
  - http://localhost:8080/actuator/metrics/payments.client.list.count
  - http://localhost:8080/actuator/metrics/payments.client.list.latency

Cobertura (Jacoco)

- Configurado jacoco-maven-plugin con umbral de 80% a nivel de paquete.
- Ejecutar: ./mvnw -DskipTests=false clean verify
- Reporte: target/site/jacoco/index.html
- Resumen exportado a: docs/reports/jacoco-summary.md (ver instrucciones dentro).
