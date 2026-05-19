package com.a.demo.service;

import com.a.demo.model.Education;
import com.a.demo.repository.IEducationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EducationServiceImpl implements IEducationService {

    private final IEducationRepository educationRepository;

    @Override
    public Education save(Education education, Long personalInfoId) {
        log.info("Saving education: {} for PersonalInfo ID: {}", education.getDegree(), personalInfoId);
        return educationRepository.save(education, personalInfoId);
    }

    @Override
    public Optional<Education> findById(Long id) {
        log.info("Fetching education with ID: {}", id);
        return educationRepository.findById(id);
    }

    @Override
    public List<Education> findAll() {
        log.info("Retrieving all education records");
        return educationRepository.findAll();
    }

    @Override
    public List<Education> findByPersonalInfoId(Long personalInfoId) {
        log.info("Fetching education for PersonalInfo ID: {}", personalInfoId);
        return educationRepository.findByPersonalInfoId(personalInfoId);
    }

    @Override
    public void deleteById(Long id) {
        log.warn("Deleting education with ID: {}", id);
        educationRepository.deleteById(id);
    }
}
