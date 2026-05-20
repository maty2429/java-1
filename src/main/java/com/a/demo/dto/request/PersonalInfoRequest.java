package com.a.demo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada (Request) para crear o actualizar PersonalInfo.
 *
 * Que es un DTO?
 *   Data Transfer Object: un objeto plano para llevar datos entre capas.
 *   En APIs REST, separamos lo que el cliente envia (Request) de la entidad de BD (PersonalInfo)
 *   porque:
 *     - El cliente NO deberia poder mandar el id (lo genera la BD).
 *     - La entidad puede tener mas campos internos que no queremos exponer.
 *     - Las validaciones del request pueden ser distintas a las del modelo persistente.
 *
 * En este request NO incluimos las colecciones (skills/educations/experiences) porque
 * se manejan con sus propios endpoints.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonalInfoRequest {

    @NotBlank(message = "First name is mandatory")
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

    @Min(value = 0, message = "Years of experience cannot be negative")
    private Integer yearsOfExperience;

    @Email(message = "Email should be valid")
    private String email;

    @Pattern(regexp = "^[+0-9 ()-]{6,30}$|^$", message = "Phone has an invalid format")
    private String phone;

    @Pattern(regexp = "^(https?://).+|^$", message = "LinkedIn URL must start with http:// or https://")
    private String linkedinUrl;

    @Pattern(regexp = "^(https?://).+|^$", message = "GitHub URL must start with http:// or https://")
    private String githubUrl;
}
