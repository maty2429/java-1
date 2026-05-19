package com.a.demo.exception;

import lombok.Getter;
import org.springframework.validation.BindingResult;

/**
 * ¿Para qué sirve esta clase?
 * Sirve para crear tu propia excepción personalizada. En lugar de lanzar un error genérico, 
 * lanzas una `ValidationException` que lleva dentro toda la lista de errores detallados.
 * 
 * @Getter: Anotación de Lombok que genera automáticamente el método 'getBindingResult()'.
 * Esto permite que el manejador de errores global pueda sacar la lista de errores de aquí adentro.
 */
@Getter
public class ValidationException extends RuntimeException {

    /**
     * ¿Qué es BindingResult?
     * Es como el "bolsito de los errores" de Spring. Cuando mandas datos a un controlador con @Valid, 
     * Spring revisa el modelo. Si encuentra que el email está mal o el nombre está vacío, 
     * mete esos errores detallados (qué campo falló y por qué) dentro de este BindingResult.
     */
    private final BindingResult bindingResult;

    /**
     * Constructor de la excepción.
     * 
     * ¿Por qué heredamos de RuntimeException?
     * En Java hay excepciones comprobadas (te obligan a poner try-catch) y no comprobadas (RuntimeException).
     * Al ser RuntimeException, podemos lanzarla desde cualquier controlador sin ensuciar el código
     * con try-catch por todos lados. Simplemente detiene el flujo y viaja hasta el manejador de errores.
     */
    public ValidationException(BindingResult bindingResult) {
        /**
         * super(...): Llama al constructor de la clase padre (RuntimeException).
         * Esto guarda el mensaje descriptivo en el sistema para cuando mires la consola del servidor.
         */
        super("Error de validación: se encontraron " + bindingResult.getErrorCount() + " errores");
        
        // Guardamos el bolsito de errores en nuestra excepción para usarlo después.
        this.bindingResult = bindingResult;
    }
}
