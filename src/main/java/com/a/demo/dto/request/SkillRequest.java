package com.a.demo.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para crear o actualizar una Skill.
 * No incluye id (la BD lo genera) ni personalInfoId (viene como PathVariable en la URL).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkillRequest {

    @NotBlank(message = "Skill name is mandatory")
    @Size(max = 100)
    private String name;

    @NotNull(message = "Level percentage is mandatory")
    @Min(value = 0, message = "Level percentage cannot be negative")
    @Max(value = 100, message = "Level percentage cannot exceed 100")
    private Integer levelPercentage;

    private String iconClass;
}
