package com.a.demo.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.Set;

/**
 * Clase de Modelo para Informacion Personal.
 * Aqui aplicamos Validaciones para asegurar que los datos que entran sean correctos.
 *
 * Nota importante: este proyecto usa JdbcTemplate puro (sin Spring Data JPA),
 * asi que las colecciones (skills, educations, experiences) NO se persisten
 * automaticamente con la entidad. El service tiene que cargarlas/guardarlas a mano
 * llamando a sus respectivos repositorios.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonalInfo {

    @Id
    private Long id;

    // @NotBlank: el texto no puede estar vacio ni ser solo espacios.
    // @Size(max=100): no puede superar 100 caracteres.
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

    // @Min(0): no permite numeros negativos (no podes tener -3 anios de experiencia)
    @Min(value = 0, message = "Years of experience cannot be negative")
    private Integer yearsOfExperience;

    // @Email: valida que tenga formato de correo (ej. usuario@dominio.com)
    @Email(message = "Email should be valid")
    private String email;

    // @Pattern: validacion por expresion regular. Si el campo viene null no se valida,
    // pero si viene con valor tiene que cumplir el patron.
    // El "?" al final del regex permite que sea opcional/null.
    @Pattern(regexp = "^[+0-9 ()-]{6,30}$|^$", message = "Phone has an invalid format")
    private String phone;

    @Pattern(regexp = "^(https?://).+|^$", message = "LinkedIn URL must start with http:// or https://")
    private String linkedinUrl;

    @Pattern(regexp = "^(https?://).+|^$", message = "GitHub URL must start with http:// or https://")
    private String githubUrl;

    // Estas son las colecciones de habilidades, educacion y experiencia.
    // Se cargan/guardan manualmente en el service (no automaticamente porque no usamos JPA).
    private Set<Skill> skills;
    private Set<Education> educations;
    private Set<Experience> experiences;
}
