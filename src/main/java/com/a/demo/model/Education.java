package com.a.demo.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;

/**
 * Modelo (entidad) que representa un registro de Educacion / estudio.
 * Se mapea a la tabla 'educations' en la base de datos.
 *
 * Importante: las fechas son de tipo LocalDate (no String).
 * Asi @PastOrPresent SI funciona, y Spring puede convertir automaticamente
 * el JSON "2020-05-15" en un LocalDate al recibir el request.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Education {

    @Id
    private Long id;

    @NotBlank(message = "Degree is mandatory")
    @Size(max = 255)
    private String degree;

    @NotBlank(message = "Institution is mandatory")
    @Size(max = 255)
    private String institution;

    // @NotNull: la fecha es obligatoria.
    // @PastOrPresent: la fecha no puede estar en el futuro (no podes haber empezado a estudiar manana).
    @NotNull(message = "la fecha de inicio es obligatoria")
    @PastOrPresent(message = "la fecha de inicio no puede ser futura")
    private LocalDate startDate;

    // endDate es OPCIONAL: si es null significa "actualmente cursando".
    // Por eso NO lleva @NotNull.
    private LocalDate endDate;

    private String description;
}
