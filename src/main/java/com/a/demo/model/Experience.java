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
 * Modelo (entidad) que representa una Experiencia laboral del portafolio.
 * Se mapea a la tabla 'experiences' en la base de datos.
 *
 * Las fechas son LocalDate (igual que en Education) para poder validarlas
 * y para que Spring/Jackson convierta automaticamente desde JSON.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Experience {

    @Id
    private Long id;

    @NotBlank(message = "Job title is mandatory")
    @Size(max = 255)
    private String jobTitle;

    @NotBlank(message = "Company name is mandatory")
    @Size(max = 255)
    private String companyName;

    @NotNull(message = "Start date is mandatory")
    @PastOrPresent(message = "Start date cannot be in the future")
    private LocalDate startDate;

    // Opcional: si es null significa "trabajo actual"
    private LocalDate endDate;

    private String description;
}
