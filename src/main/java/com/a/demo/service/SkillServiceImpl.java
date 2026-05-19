package com.a.demo.service;

import com.a.demo.model.Skill;
import com.a.demo.repository.ISkillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementación de la Lógica de Negocio para las Habilidades (Skills).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SkillServiceImpl implements ISkillService {

    private final ISkillRepository skillRepository;

    @Override
    public Skill save(Skill skill, Long personalInfoId) {
        log.info("Saving skill: {} for PersonalInfo ID: {}", skill.getName(), personalInfoId);
        return skillRepository.save(skill, personalInfoId);
    }

    @Override
    public Optional<Skill> findById(Long id) {
        log.info("Fetching skill with ID: {}", id);
        return skillRepository.findById(id);
    }

    @Override
    public List<Skill> findAll() {
        log.info("Retrieving all skills");
        return skillRepository.findAll();
    }

    @Override
    public List<Skill> findByPersonalInfoId(Long personalInfoId) {
        log.info("Fetching skills for PersonalInfo ID: {}", personalInfoId);
        return skillRepository.findByPersonalInfoId(personalInfoId);
    }

    @Override
    public void deleteById(Long id) {
        log.warn("Deleting skill with ID: {}", id);
        skillRepository.deleteById(id);
    }
}
