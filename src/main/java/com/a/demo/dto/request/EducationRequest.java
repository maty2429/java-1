package com.a.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO de entrada para crear o actualizar una Education.
 *
 * La validacion cruzada (endDate >= startDate) NO se puede expresar con anotaciones
 * estandar de Jakarta. Esa regla la valida el service a mano y lanza ValidationException.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EducationRequest {

    @NotBlank(message = "Degree is mandatory")
    @Size(max = 255)
    private String degree;

    @NotBlank(message = "Institution is mandatory")
    @Size(max = 255)
    private String institution;

    @NotNull(message = "Start date is mandatory")
    @PastOrPresent(message = "Start date cannot be in the future")
    private LocalDate startDate;

    /** Opcional: si es null significa "actualmente cursando". */
    private LocalDate endDate;

    private String description;
}
