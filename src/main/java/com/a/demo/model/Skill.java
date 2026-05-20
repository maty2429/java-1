package com.a.demo.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

/**
 * Modelo (entidad) que representa una Habilidad tecnica del portafolio.
 * Se mapea a la tabla 'skills' en la base de datos.
 *
 * @Data (Lombok): genera getters, setters, equals, hashCode y toString automaticamente.
 * @AllArgsConstructor / @NoArgsConstructor: generan constructores con y sin parametros.
 * @Id: marca el campo como clave primaria (lo usa Spring Data al hacer mapeos).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Skill {

    @Id
    private Long id;

    // @NotBlank: el texto no puede ser null, ni vacio (""), ni puro espacios en blanco.
    // @Size(max=100): no puede superar 100 caracteres (tiene que entrar en la columna de la BD).
    @NotBlank(message = "Skill name is mandatory")
    @Size(max = 100, message = "Skill name must be under 100 characters")
    private String name;

    // @Min/@Max: validan que el numero este en el rango [0, 100].
    // Estas anotaciones van sobre NUMEROS (Integer/Long), no sobre String.
    // Antes estaban mal puestas sobre 'name' (que es String) y por eso no validaban nada.
    @Min(value = 0, message = "Level percentage cannot be negative")
    @Max(value = 100, message = "Level percentage cannot exceed 100")
    private Integer levelPercentage;

    // iconClass es opcional (puede ser null), por eso no lleva validacion
    private String iconClass;
}
