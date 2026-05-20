package com.a.demo.service;

import com.a.demo.model.Experience;

import java.util.List;
import java.util.Optional;

public interface IExperienceService {

    Experience save(Experience experience, Long personalInfoId);

    Experience update(Long id, Experience experience, Long personalInfoId);

    Experience findByIdOrThrow(Long id);

    Optional<Experience> findById(Long id);

    List<Experience> findAll();

    List<Experience> findByPersonalInfoId(Long personalInfoId);

    /** Experiencias "actuales" (sin endDate) de una persona. */
    List<Experience> findCurrentByPersonalInfoId(Long personalInfoId);

    void deleteById(Long id);
}
