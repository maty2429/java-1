package com.a.demo.service;

import com.a.demo.exception.ResourceNotFoundException;
import com.a.demo.model.Skill;
import com.a.demo.repository.ISkillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementacion de la Logica de Negocio para las Habilidades (Skills).
 *
 * Ver PersonalInfoServiceImpl para la explicacion detallada de @Transactional
 * y @Transactional(readOnly = true).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SkillServiceImpl implements ISkillService {

    private final ISkillRepository skillRepository;

    @Override
    @Transactional
    public Skill save(Skill skill, Long personalInfoId) {
        log.info("Saving skill: {} for PersonalInfo ID: {}", skill.getName(), personalInfoId);
        return skillRepository.save(skill, personalInfoId);
    }

    @Override
    @Transactional
    public Skill update(Long id, Skill skill) {
        // Recuperamos el personal_info_id existente para preservar la relacion.
        // Esto resuelve el bug que tenia el controller (antes pasaba null).
        Long personalInfoId = skillRepository.findPersonalInfoIdBySkillId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill", id));
        skill.setId(id);
        log.info("Updating skill ID: {} for PersonalInfo ID: {}", id, personalInfoId);
        return skillRepository.save(skill, personalInfoId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Skill> findById(Long id) {
        log.info("Fetching skill with ID: {}", id);
        return skillRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Skill findByIdOrThrow(Long id) {
        return skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Skill> findAll() {
        log.info("Retrieving all skills");
        return skillRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Skill> findByPersonalInfoId(Long personalInfoId) {
        log.info("Fetching skills for PersonalInfo ID: {}", personalInfoId);
        return skillRepository.findByPersonalInfoId(personalInfoId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Skill> findByMinLevel(int minLevel) {
        log.info("Fetching skills with level >= {}", minLevel);
        return skillRepository.findByMinLevel(minLevel);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Skill> findTopSkills(int limit) {
        // Validacion sencilla de borde: limit no puede ser negativo ni cero.
        if (limit <= 0) {
            throw new IllegalArgumentException("limit must be positive");
        }
        log.info("Fetching top {} skills", limit);
        return skillRepository.findTopSkills(limit);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        // Si no existe, devolvemos 404 (consistente con el resto de la API)
        if (skillRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Skill", id);
        }
        log.warn("Deleting skill with ID: {}", id);
        skillRepository.deleteById(id);
    }
}
