package com.a.demo.service;

import com.a.demo.model.PersonalInfo;

import java.util.List;
import java.util.Optional;

public interface IPersonalInfoService {

    PersonalInfo save(PersonalInfo personalInfo);

    /** Actualiza un PersonalInfo existente. Lanza ResourceNotFoundException si no existe. */
    PersonalInfo update(Long id, PersonalInfo personalInfo);

    /** Devuelve el PersonalInfo o lanza ResourceNotFoundException (no devuelve Optional). */
    PersonalInfo findByIdOrThrow(Long id);

    Optional<PersonalInfo> findById(Long id);

    Optional<PersonalInfo> findByEmail(String email);

    List<PersonalInfo> findAll();

    void deleteById(Long id);
}
