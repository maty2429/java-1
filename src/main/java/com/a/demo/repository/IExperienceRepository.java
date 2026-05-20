package com.a.demo.repository;

import com.a.demo.model.Experience;

import java.util.List;
import java.util.Optional;

/**
 * Contrato del repositorio de Experience.
 */
public interface IExperienceRepository {

    Experience save(Experience experience, Long personalInfoId);

    Optional<Experience> findById(Long id);

    List<Experience> findAll();

    List<Experience> findByPersonalInfoId(Long personalInfoId);

    /** Experiencias actuales (endDate IS NULL) de una persona. */
    List<Experience> findCurrentByPersonalInfoId(Long personalInfoId);

    void deleteById(Long id);
}
