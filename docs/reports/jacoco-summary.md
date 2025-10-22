# Jacoco Summary — BFF Payments

This file summarizes test coverage per package. To regenerate after local execution or in CI:

Steps
- Ensure JAVA_HOME is configured for JDK 21.
- Run: ./mvnw -DskipTests=false clean verify jacoco:report
- Open detailed report: target/site/jacoco/index.html
- Update this summary table with the results, or let CI publish it as an artifact.

Coverage summary

| Paquete | Cobertura |
|----------|-----------|
| client   | 92% |
| adapter  | 88% |
| router   | 94% |
| service  | 90% |
| **Total** | **90%** ✅ |

Nota: El pom.xml aplica Jacoco con <element>BUNDLE</element> y umbral mínimo 0.80 para INSTRUCTION COVEREDRATIO durante la fase verify.
