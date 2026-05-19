package com.a.demo.service;

import com.a.demo.model.Experience;
import com.a.demo.repository.IExperienceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExperienceServiceImpl implements IExperienceService {

    private final IExperienceRepository experienceRepository;

    @Override
    public Experience save(Experience experience, Long personalInfoId) {
        log.info("Saving experience: {} at {} for PersonalInfo ID: {}", experience.getJobTitle(), experience.getCompanyName(), personalInfoId);
        return experienceRepository.save(experience, personalInfoId);
    }

    @Override
    public Optional<Experience> findById(Long id) {
        log.info("Fetching experience with ID: {}", id);
        return experienceRepository.findById(id);
    }

    @Override
    public List<Experience> findAll() {
        log.info("Retrieving all experience records");
        return experienceRepository.findAll();
    }

    @Override
    public List<Experience> findByPersonalInfoId(Long personalInfoId) {
        log.info("Fetching experience for PersonalInfo ID: {}", personalInfoId);
        return experienceRepository.findByPersonalInfoId(personalInfoId);
    }

    @Override
    public void deleteById(Long id) {
        log.warn("Deleting experience with ID: {}", id);
        experienceRepository.deleteById(id);
    }
}
