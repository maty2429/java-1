package com.a.demo.service;

import com.a.demo.exception.ResourceNotFoundException;
import com.a.demo.exception.ValidationException;
import com.a.demo.model.Education;
import com.a.demo.repository.IEducationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Logica de negocio para Education.
 *
 * Validacion cruzada: endDate (si existe) debe ser >= startDate.
 * Esa regla NO se puede expresar con anotaciones estandar de Jakarta sobre el DTO,
 * por eso la chequeamos aca y lanzamos ValidationException si no se cumple.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EducationServiceImpl implements IEducationService {

    private final IEducationRepository educationRepository;

    @Override
    @Transactional
    public Education save(Education education, Long personalInfoId) {
        validateDates(education);
        log.info("Saving education: {} for PersonalInfo ID: {}", education.getDegree(), personalInfoId);
        return educationRepository.save(education, personalInfoId);
    }

    @Override
    @Transactional
    public Education update(Long id, Education education, Long personalInfoId) {
        if (educationRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Education", id);
        }
        validateDates(education);
        education.setId(id);
        log.info("Updating education ID: {}", id);
        return educationRepository.save(education, personalInfoId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Education> findById(Long id) {
        log.info("Fetching education with ID: {}", id);
        return educationRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Education findByIdOrThrow(Long id) {
        return educationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Education", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Education> findAll() {
        log.info("Retrieving all education records");
        return educationRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Education> findByPersonalInfoId(Long personalInfoId) {
        log.info("Fetching education for PersonalInfo ID: {}", personalInfoId);
        return educationRepository.findByPersonalInfoId(personalInfoId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Education> findByInstitution(String institution) {
        log.info("Searching educations by institution: {}", institution);
        return educationRepository.findByInstitution(institution);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (educationRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Education", id);
        }
        log.warn("Deleting education with ID: {}", id);
        educationRepository.deleteById(id);
    }

    /**
     * Validacion cruzada: endDate (si existe) debe ser >= startDate.
     * Si la regla se rompe, lanzamos ValidationException -> 400 en la respuesta.
     */
    private void validateDates(Education education) {
        if (education.getEndDate() != null
                && education.getStartDate() != null
                && education.getEndDate().isBefore(education.getStartDate())) {
            throw new ValidationException(List.of("endDate must be on or after startDate"));
        }
    }
}
