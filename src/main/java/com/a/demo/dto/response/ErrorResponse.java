package com.a.demo.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Estructura estandar para errores en la API.
 * El GlobalExceptionHandler la usa en todas las respuestas de error.
 *
 * @JsonInclude(NON_NULL): si un campo es null, no aparece en el JSON
 * (asi el campo "errors" no aparece cuando no aplica, por ejemplo en un 404).
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    /** Momento en que se produjo el error (util para debug en logs). */
    private LocalDateTime timestamp;

    /** Codigo HTTP (400, 404, 500...). */
    private int status;

    /** Nombre corto del error ("Bad Request", "Not Found"...). */
    private String error;

    /** Mensaje legible para el cliente. */
    private String message;

    /** Path al que apunto el cliente, ej. "/api/personal-info/999". */
    private String path;

    /** Lista de detalles de validacion (solo se llena cuando hay multiples errores). */
    private List<String> errors;
}
