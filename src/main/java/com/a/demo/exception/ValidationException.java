package com.a.demo.exception;

import lombok.Getter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;

/**
 * Excepcion personalizada para errores de validacion en la capa de Servicio.
 *
 * Por que existe?
 *   Cuando el controller usa @Valid, Spring lanza MethodArgumentNotValidException por su cuenta
 *   y nosotros la atrapamos en GlobalExceptionHandler. Pero si el service hace una validacion
 *   manual (por ejemplo, reglas de negocio cruzadas), no hay una excepcion natural para eso.
 *   Esta clase llena ese hueco: el service la lanza y el handler la convierte en respuesta JSON 400.
 *
 * Por que heredamos de RuntimeException?
 *   En Java hay excepciones comprobadas (te obligan a poner try-catch) y no comprobadas (RuntimeException).
 *   Al ser RuntimeException podemos lanzarla desde cualquier metodo sin ensuciar el codigo
 *   con try-catch por todos lados. Simplemente detiene el flujo y viaja hasta el manejador global.
 *
 * @Getter (Lombok): genera getErrors() y getBindingResult() automaticamente.
 */
@Getter
public class ValidationException extends RuntimeException {

    /** Lista plana de mensajes de error (lo que devolvemos al cliente en el JSON). */
    private final List<String> errors;

    /**
     * Constructor a partir de una lista de mensajes (uso simple).
     * Ejemplo: new ValidationException(List.of("endDate must be after startDate"));
     */
    public ValidationException(List<String> errors) {
        super("Validation failed: " + errors.size() + " error(s)");
        this.errors = errors;
    }

    /**
     * Constructor a partir de un BindingResult de Spring (compatibilidad con validacion manual
     * con BeanPropertyBindingResult + Validator). Extraemos los mensajes en una lista plana.
     */
    public ValidationException(BindingResult bindingResult) {
        super("Validation failed: " + bindingResult.getErrorCount() + " error(s)");
        List<String> messages = new ArrayList<>();
        for (FieldError fe : bindingResult.getFieldErrors()) {
            messages.add(fe.getField() + ": " + fe.getDefaultMessage());
        }
        // Errores globales (no atados a un campo en particular)
        bindingResult.getGlobalErrors().forEach(ge -> messages.add(ge.getDefaultMessage()));
        this.errors = messages;
    }
}
