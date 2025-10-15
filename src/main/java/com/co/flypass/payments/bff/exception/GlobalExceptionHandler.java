package com.co.flypass.payments.bff.exception;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(UpstreamUnavailableException.class)
  public ResponseEntity<Map<String, Object>> handle(UpstreamUnavailableException ex) {
    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
      .header(HttpHeaders.RETRY_AFTER, String.valueOf(ex.getRetryAfterSeconds()))
      .body(Map.of("error","SERVICE_UNAVAILABLE","message","Payments provider unavailable"));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, Object>> badRequest(IllegalArgumentException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
      .body(Map.of("error","BAD_REQUEST","message", ex.getMessage()));
  }

  @ExceptionHandler(UnknownConnectorException.class)
  public ResponseEntity<Map<String, Object>> handleUnknownConnector(UnknownConnectorException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
      Map.of(
        "error", "INVALID_CONNECTOR",
        "message", "Connector '" + ex.getConnectorId() + "' is not supported",
        "allowedConnectors", ex.getAllowed()
      )
    );
  }
}
