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
 * DTO de entrada para crear o actualizar una Experience.
 * endDate puede ser null (significa "trabajo actual").
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExperienceRequest {

    @NotBlank(message = "Job title is mandatory")
    @Size(max = 255)
    private String jobTitle;

    @NotBlank(message = "Company name is mandatory")
    @Size(max = 255)
    private String companyName;

    @NotNull(message = "Start date is mandatory")
    @PastOrPresent(message = "Start date cannot be in the future")
    private LocalDate startDate;

    private LocalDate endDate;

    private String description;
}
