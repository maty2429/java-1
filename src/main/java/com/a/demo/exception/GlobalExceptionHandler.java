package com.a.demo.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @ControllerAdvice: 
 * Esta anotación marca la clase como un "Asesor de Controladores". 
 * Su función es capturar excepciones que ocurran en CUALQUIER controlador de la aplicación. 
 * Es como un embudo donde caen todos los errores para ser procesados en un solo lugar.
 */
@ControllerAdvice 
public class GlobalExceptionHandler {

    /**
     * @ExceptionHandler(ValidationException.class):
     * Indica que este método solo se activará cuando se lance una 'ValidationException'.
     * Si ocurre otro tipo de error, este método lo ignorará.
     * 
     * ValidationException ex: 
     * Es el objeto de la excepción que fue lanzada. Contiene los errores de validación.
     * 
     * Model model: 
     * Es un objeto de Spring que actúa como un contenedor de datos. 
     * Lo que guardes aquí (usando addAttribute) estará disponible en tu página HTML (ej. Thymeleaf).
     */
    @ExceptionHandler(ValidationException.class)
    public String handleValidationException(ValidationException ex, Model model) {
        
        // ex.getBindingResult().getAllErrors(): 
        // Extrae la lista completa de errores del "bolsito" (BindingResult) que explicamos antes.
        // Los guarda en el modelo bajo el nombre "errors" para que el HTML pueda mostrarlos.
        model.addAttribute("errors", ex.getBindingResult().getAllErrors());

        // ex.getMessage(): 
        // Obtiene el mensaje de texto descriptivo del error (ej. "Error de validación: se encontraron 3 errores").
        // Lo guarda en el modelo bajo el nombre "message".
        model.addAttribute("message", ex.getMessage());

        // return "error": 
        // Le dice a Spring que busque un archivo de vista llamado "error" (ej. error.html) 
        // y se lo muestre al usuario en el navegador.
        return "error";
    }
}
