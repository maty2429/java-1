package com.a.demo.dto.mapper;

import com.a.demo.dto.request.PersonalInfoRequest;
import com.a.demo.dto.response.PersonalInfoResponse;
import com.a.demo.model.Education;
import com.a.demo.model.Experience;
import com.a.demo.model.PersonalInfo;
import com.a.demo.model.Skill;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Mapper PersonalInfo <-> DTOs.
 *
 * Este es mas complejo que los otros porque la respuesta incluye listas anidadas
 * (skills, educations, experiences). Por eso recibe los mappers especificos por
 * inyeccion de dependencias.
 */
@Component
@RequiredArgsConstructor
public class PersonalInfoMapper {

    private final SkillMapper skillMapper;
    private final EducationMapper educationMapper;
    private final ExperienceMapper experienceMapper;

    public PersonalInfo toEntity(PersonalInfoRequest request) {
        if (request == null) return null;
        PersonalInfo p = new PersonalInfo();
        p.setFirstName(request.getFirstName());
        p.setLastName(request.getLastName());
        p.setTitle(request.getTitle());
        p.setProfileDescription(request.getProfileDescription());
        p.setProfileImageUrl(request.getProfileImageUrl());
        p.setYearsOfExperience(request.getYearsOfExperience());
        p.setEmail(request.getEmail());
        p.setPhone(request.getPhone());
        p.setLinkedinUrl(request.getLinkedinUrl());
        p.setGithubUrl(request.getGithubUrl());
        return p;
    }

    /**
     * Variante "ligera": no incluye colecciones (skills/educations/experiences).
     * Util cuando devolvemos un listado y no queremos cargar todas las relaciones de cada uno.
     */
    public PersonalInfoResponse toResponseWithoutCollections(PersonalInfo p) {
        if (p == null) return null;
        return PersonalInfoResponse.builder()
                .id(p.getId())
                .firstName(p.getFirstName())
                .lastName(p.getLastName())
                .title(p.getTitle())
                .profileDescription(p.getProfileDescription())
                .profileImageUrl(p.getProfileImageUrl())
                .yearsOfExperience(p.getYearsOfExperience())
                .email(p.getEmail())
                .phone(p.getPhone())
                .linkedinUrl(p.getLinkedinUrl())
                .githubUrl(p.getGithubUrl())
                .skills(Collections.emptyList())
                .educations(Collections.emptyList())
                .experiences(Collections.emptyList())
                .build();
    }

    /**
     * Variante "completa": incluye las listas embebidas.
     * Las recibe el caller porque las trae con sus respectivos repositorios.
     */
    public PersonalInfoResponse toResponse(PersonalInfo p,
                                            List<Skill> skills,
                                            List<Education> educations,
                                            List<Experience> experiences) {
        if (p == null) return null;
        return PersonalInfoResponse.builder()
                .id(p.getId())
                .firstName(p.getFirstName())
                .lastName(p.getLastName())
                .title(p.getTitle())
                .profileDescription(p.getProfileDescription())
                .profileImageUrl(p.getProfileImageUrl())
                .yearsOfExperience(p.getYearsOfExperience())
                .email(p.getEmail())
                .phone(p.getPhone())
                .linkedinUrl(p.getLinkedinUrl())
                .githubUrl(p.getGithubUrl())
                .skills(skillMapper.toResponseList(skills))
                .educations(educationMapper.toResponseList(educations))
                .experiences(experienceMapper.toResponseList(experiences))
                .build();
    }

    public List<PersonalInfoResponse> toResponseListWithoutCollections(List<PersonalInfo> list) {
        return list.stream().map(this::toResponseWithoutCollections).toList();
    }
}
