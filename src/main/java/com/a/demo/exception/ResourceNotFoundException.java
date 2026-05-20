package com.a.demo.exception;

/**
 * Excepcion que se lanza cuando se busca un recurso por id y no existe.
 *
 * Por que existe esta clase:
 *   En vez de devolver Optional vacio al controller y que cada controller decida
 *   responder 404, centralizamos la decision: el service lanza esta excepcion y
 *   el GlobalExceptionHandler la convierte en una respuesta 404 con JSON.
 *
 * Hereda de RuntimeException (no comprobada) para no tener que poner try/catch
 * en cada metodo que pueda lanzarla. Spring atrapa la excepcion automaticamente
 * gracias al @RestControllerAdvice.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructor practico: arma un mensaje uniforme del estilo
     * "PersonalInfo not found with id: 42".
     */
    public ResourceNotFoundException(String resourceName, Long id) {
        super(resourceName + " not found with id: " + id);
    }
}
