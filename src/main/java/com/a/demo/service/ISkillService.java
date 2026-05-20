package com.a.demo.service;

import com.a.demo.model.Skill;

import java.util.List;
import java.util.Optional;

public interface ISkillService {

    Skill save(Skill skill, Long personalInfoId);

    /** Actualiza una skill existente, preservando su personalInfoId original. */
    Skill update(Long id, Skill skill);

    Skill findByIdOrThrow(Long id);

    Optional<Skill> findById(Long id);

    List<Skill> findAll();

    List<Skill> findByPersonalInfoId(Long personalInfoId);

    /** Filtra habilidades por nivel minimo (ej: las que el usuario domina >= 70%). */
    List<Skill> findByMinLevel(int minLevel);

    /** Top N habilidades. */
    List<Skill> findTopSkills(int limit);

    void deleteById(Long id);
}
