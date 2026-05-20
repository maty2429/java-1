package com.a.demo.exception;

import com.a.demo.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manejador GLOBAL de excepciones para la API REST.
 *
 * @RestControllerAdvice = @ControllerAdvice + @ResponseBody.
 * Significa que TODAS las respuestas de error se serializan como JSON automaticamente,
 * sin necesidad de devolver vistas HTML (en este proyecto NO usamos Thymeleaf).
 *
 * Como funciona?
 *   1. Cualquier excepcion que escape de un @RestController llega aqui.
 *   2. Spring busca el metodo con @ExceptionHandler que coincide con el tipo de excepcion.
 *   3. Ese metodo arma un ErrorResponse (estructura JSON estandar) con el codigo HTTP apropiado.
 *   4. El cliente recibe siempre un JSON con la misma forma, sin importar el tipo de error.
 *
 * Codigos HTTP que devolvemos:
 *   400 Bad Request   -> validacion (campos invalidos, JSON mal formado, parametros con mal tipo)
 *   404 Not Found     -> recurso inexistente
 *   500 Internal Error-> cualquier excepcion no manejada (fallback)
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Se dispara cuando un @RequestBody con @Valid falla en alguna validacion.
     * Es la excepcion mas comun en APIs: "First name is mandatory", "Email should be valid", etc.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                          HttpServletRequest request) {
        // Convertimos los errores de cada campo en mensajes legibles: "fieldName: mensaje"
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.toList());

        ErrorResponse body = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Validation failed")
                .path(request.getRequestURI())
                .errors(errors)
                .build();

        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Validacion manual desde el service (por ejemplo, validacion cruzada de fechas).
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleCustomValidation(ValidationException ex,
                                                                HttpServletRequest request) {
        ErrorResponse body = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .errors(ex.getErrors())
                .build();
        return ResponseEntity.badRequest().body(body);
    }

    /** 404 cuando se busca un recurso inexistente. */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex,
                                                        HttpServletRequest request) {
        ErrorResponse body = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /** JSON malformado (cuerpo no se puede parsear, fecha en formato invalido, etc). */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException ex,
                                                          HttpServletRequest request) {
        ErrorResponse body = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Malformed JSON or invalid value in request body")
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.badRequest().body(body);
    }

    /** PathVariable con tipo incorrecto (ej. /personal-info/abc cuando se espera Long). */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                            HttpServletRequest request) {
        String message = "Parameter '" + ex.getName() + "' has invalid type";
        ErrorResponse body = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.badRequest().body(body);
    }

    /** Ruta inexistente. (Solo se dispara con spring.mvc.throw-exception-if-no-handler-found=true.) */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandler(NoHandlerFoundException ex,
                                                         HttpServletRequest request) {
        ErrorResponse body = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message("Endpoint not found")
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /** IllegalArgumentException -> 400. */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex,
                                                                HttpServletRequest request) {
        ErrorResponse body = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Fallback: cualquier excepcion no contemplada arriba.
     * Importante NO devolver el stack trace al cliente (es informacion sensible).
     * Lo logueamos para nuestro debug, pero al cliente solo le decimos "algo fallo".
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception", ex);
        ErrorResponse body = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("An unexpected error occurred")
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
