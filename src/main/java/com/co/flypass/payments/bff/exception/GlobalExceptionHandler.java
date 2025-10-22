package com.co.flypass.payments.bff.exception;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.OffsetDateTime;
import java.util.Map;

@RestControllerAdvice
public final class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String CORRELATION_ID_KEY = "correlationId";

    @ExceptionHandler(UpstreamUnavailableException.class)
    public ResponseEntity<Map<String, Object>> handleUpstream(UpstreamUnavailableException ex) {
        HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;
        log.warn("error={} status={} path={} correlationId={}",
                "SERVICE_UNAVAILABLE", status.value(), requestPath(), correlationId(), ex);

        return ResponseEntity.status(status)
                .header(HttpHeaders.RETRY_AFTER, String.valueOf(ex.getRetryAfterSeconds()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(baseBody(status, "SERVICE_UNAVAILABLE", "Payments provider unavailable",
                        Map.of("retryAfterSeconds", ex.getRetryAfterSeconds())));
    }

    @ExceptionHandler(BusinessValidationException.class)
    public ResponseEntity<Map<String, Object>> handleBusiness(BusinessValidationException ex) {
        HttpStatus status = ex.getHttpStatus();
        log.warn("error={} status={} path={} correlationId={}",
                ex.getCode(), status.value(), requestPath(), correlationId(), ex);

        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(baseBody(status, ex.getCode(), ex.getMessage(), Map.of()));
    }

    @ExceptionHandler(UnknownServiceIdException.class)
    public ResponseEntity<Map<String, Object>> handleUnknownServiceId(UnknownServiceIdException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        log.warn("error={} status={} path={} correlationId={} details=serviceId:{} allowed:{}",
                "INVALID_SERVICE_ID", status.value(), requestPath(), correlationId(), ex.getServiceId(), ex.getAllowed());

        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(baseBody(status, "INVALID_SERVICE_ID",
                        "Service '" + ex.getServiceId() + "' is not supported",
                        Map.of("allowedServiceIds", ex.getAllowed())));
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<Map<String, Object>> handleMissingHeader(MissingRequestHeaderException ex) {
        if ("Authorization".equalsIgnoreCase(ex.getHeaderName())) {
            HttpStatus status = HttpStatus.UNAUTHORIZED;
            log.warn("error={} status={} path={} correlationId={}",
                    "UNAUTHORIZED", status.value(), requestPath(), correlationId());

            return ResponseEntity.status(status)
                    .header(HttpHeaders.WWW_AUTHENTICATE, "Bearer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(baseBody(status, "UNAUTHORIZED", "Missing Authorization header",
                            Map.of("header", "Authorization")));
        }
        HttpStatus status = HttpStatus.BAD_REQUEST;
        log.warn("error={} status={} path={} correlationId={} header={}",
                "BAD_REQUEST", status.value(), requestPath(), correlationId(), ex.getHeaderName());

        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(baseBody(status, "BAD_REQUEST", ex.getMessage(),
                        Map.of("header", ex.getHeaderName())));
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<Map<String, Object>> handleBadRequest(Exception ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        log.warn("error={} status={} path={} correlationId={}",
                "BAD_REQUEST", status.value(), requestPath(), correlationId(), ex);

        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(baseBody(status, "BAD_REQUEST", ex.getMessage(), Map.of()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        log.warn("error={} status={} path={} correlationId={}",
                "BAD_REQUEST", status.value(), requestPath(), correlationId(), ex);

        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(baseBody(status, "BAD_REQUEST", "Validation failed",
                        Map.of("violations", ex.getConstraintViolations()
                                .stream()
                                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                                .toList())));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        log.warn("error={} status={} path={} correlationId={}",
                "BAD_REQUEST", status.value(), requestPath(), correlationId(), ex);

        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(baseBody(status, "BAD_REQUEST", "Validation failed",
                        Map.of("fields", ex.getBindingResult().getFieldErrors()
                                .stream()
                                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                                .toList())));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        log.error("error={} status={} path={} correlationId={}",
                "INTERNAL_ERROR", status.value(), requestPath(), correlationId(), ex);

        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(baseBody(status, "INTERNAL_ERROR", "Unexpected error",
                        Map.of("exception", ex.getClass().getSimpleName())));
    }

    private static Map<String, Object> baseBody(HttpStatus status, String code, String message, Map<String, Object> details) {
        return Map.of(
                "timestamp", OffsetDateTime.now().toString(),
                "status", status.value(),
                "error", code,
                "message", message,
                "path", requestPath(),
                "correlationId", correlationId(),
                "details", details
        );
    }

    private static String correlationId() {
        String id = MDC.get(CORRELATION_ID_KEY);
        return (id == null || id.isBlank()) ? "" : id;
    }

    private static String requestPath() {
        var attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes sra && sra.getRequest() != null) {
            return sra.getRequest().getRequestURI();
        }
        return "";
    }
}
