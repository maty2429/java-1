package com.a.demo.service;

import com.a.demo.model.Education;

import java.util.List;
import java.util.Optional;

public interface IEducationService {

    Education save(Education education, Long personalInfoId);

    Education update(Long id, Education education, Long personalInfoId);

    Education findByIdOrThrow(Long id);

    Optional<Education> findById(Long id);

    List<Education> findAll();

    List<Education> findByPersonalInfoId(Long personalInfoId);

    List<Education> findByInstitution(String institution);

    void deleteById(Long id);
}
