package com.a.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO de salida para PersonalInfo, incluyendo las colecciones relacionadas
 * (skills, educations, experiences) en formato Response.
 *
 * Usar listas (no sets) en la respuesta da un orden estable al cliente
 * y un JSON mas predecible.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonalInfoResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String title;
    private String profileDescription;
    private String profileImageUrl;
    private Integer yearsOfExperience;
    private String email;
    private String phone;
    private String linkedinUrl;
    private String githubUrl;

    private List<SkillResponse> skills;
    private List<EducationResponse> educations;
    private List<ExperienceResponse> experiences;
}
