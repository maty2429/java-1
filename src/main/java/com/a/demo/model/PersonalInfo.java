package com.a.demo.model;

import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.Set;

/**
 * Clase de Modelo para Información Personal.
 * Aquí aplicamos Validaciones para asegurar que los datos que entran sean correctos.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonalInfo {

    @Id
    private Long id;

    @NotBlank(message = "First name is mandatory") // No puede estar vacío ni ser solo espacios
    @Size(max = 100, message = "First name must be under 100 characters")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    @Size(max = 100, message = "Last name must be under 100 characters")
    private String lastName;

    @NotBlank(message = "Title is mandatory")
    @Size(max = 255)
    private String title;

    @NotBlank(message = "Description is mandatory")
    private String profileDescription;

    private String profileImageUrl;

    @Min(value = 0, message = "Years of experience cannot be negative") // Mínimo valor permitido
    private Integer yearsOfExperience;

    @Email(message = "Email should be valid") // Valida que tenga formato de correo (ej. usuario@dominio.com)
    private String email;

    private String phone;
    private String linkedinUrl;
    private String githubUrl;

    // Estas son las colecciones de habilidades, educación y experiencia
    private Set<Skill> skills;
    private Set<Education> educations;
    private Set<Experience> experiences;
}

