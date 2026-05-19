package com.a.demo.repository;

import com.a.demo.model.Skill;
import java.util.List;
import java.util.Optional;

public interface ISkillRepository {
    Skill save(Skill skill, Long personalInfoId);
    Optional<Skill> findById(Long id);
    List<Skill> findAll();
    List<Skill> findByPersonalInfoId(Long personalInfoId);
    void deleteById(Long id);
}
