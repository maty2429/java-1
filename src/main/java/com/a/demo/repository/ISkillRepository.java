package com.a.demo.repository;

import com.a.demo.model.Skill;

import java.util.List;
import java.util.Optional;

/**
 * Contrato del repositorio de Skill.
 */
public interface ISkillRepository {

    Skill save(Skill skill, Long personalInfoId);

    Optional<Skill> findById(Long id);

    List<Skill> findAll();

    List<Skill> findByPersonalInfoId(Long personalInfoId);

    /** Habilidades cuyo nivel sea >= minLevel, ordenadas de mayor a menor. */
    List<Skill> findByMinLevel(int minLevel);

    /** Top N habilidades segun el nivel. */
    List<Skill> findTopSkills(int limit);

    /** ID del PersonalInfo dueno de una habilidad (para preservarla al actualizar). */
    Optional<Long> findPersonalInfoIdBySkillId(Long skillId);

    void deleteById(Long id);
}
