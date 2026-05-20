package com.a.demo.service;

import com.a.demo.exception.ResourceNotFoundException;
import com.a.demo.exception.ValidationException;
import com.a.demo.model.Experience;
import com.a.demo.repository.IExperienceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Logica de negocio para Experience.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExperienceServiceImpl implements IExperienceService {

    private final IExperienceRepository experienceRepository;

    @Override
    @Transactional
    public Experience save(Experience experience, Long personalInfoId) {
        validateDates(experience);
        log.info("Saving experience: {} at {} for PersonalInfo ID: {}",
                experience.getJobTitle(), experience.getCompanyName(), personalInfoId);
        return experienceRepository.save(experience, personalInfoId);
    }

    @Override
    @Transactional
    public Experience update(Long id, Experience experience, Long personalInfoId) {
        if (experienceRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Experience", id);
        }
        validateDates(experience);
        experience.setId(id);
        log.info("Updating experience ID: {}", id);
        return experienceRepository.save(experience, personalInfoId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Experience> findById(Long id) {
        log.info("Fetching experience with ID: {}", id);
        return experienceRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Experience findByIdOrThrow(Long id) {
        return experienceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Experience", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Experience> findAll() {
        log.info("Retrieving all experience records");
        return experienceRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Experience> findByPersonalInfoId(Long personalInfoId) {
        log.info("Fetching experience for PersonalInfo ID: {}", personalInfoId);
        return experienceRepository.findByPersonalInfoId(personalInfoId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Experience> findCurrentByPersonalInfoId(Long personalInfoId) {
        log.info("Fetching CURRENT experiences for PersonalInfo ID: {}", personalInfoId);
        return experienceRepository.findCurrentByPersonalInfoId(personalInfoId);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (experienceRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Experience", id);
        }
        log.warn("Deleting experience with ID: {}", id);
        experienceRepository.deleteById(id);
    }

    private void validateDates(Experience experience) {
        if (experience.getEndDate() != null
                && experience.getStartDate() != null
                && experience.getEndDate().isBefore(experience.getStartDate())) {
            throw new ValidationException(List.of("endDate must be on or after startDate"));
        }
    }
}
