package com.a.demo.repository;

import com.a.demo.model.Experience;
import java.util.List;
import java.util.Optional;

public interface IExperienceRepository {
    Experience save(Experience experience, Long personalInfoId);
    Optional<Experience> findById(Long id);
    List<Experience> findAll();
    List<Experience> findByPersonalInfoId(Long personalInfoId);
    void deleteById(Long id);
}
