package com.a.demo.service;

import com.a.demo.exception.ResourceNotFoundException;
import com.a.demo.exception.ValidationException;
import com.a.demo.model.Education;
import com.a.demo.repository.IEducationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EducationServiceImplTest {

    @Mock
    private IEducationRepository repository;

    @InjectMocks
    private EducationServiceImpl service;

    private Education sample;

    @BeforeEach
    void setUp() {
        sample = new Education();
        sample.setDegree("Ing. Sistemas");
        sample.setInstitution("Universidad XYZ");
        sample.setStartDate(LocalDate.of(2015, 3, 1));
        sample.setEndDate(LocalDate.of(2020, 12, 15));
    }

    @Test
    void save_validDates_callsRepo() {
        when(repository.save(any(Education.class), eq(1L))).thenAnswer(inv -> inv.getArgument(0));
        Education result = service.save(sample, 1L);
        assertThat(result).isNotNull();
        verify(repository).save(sample, 1L);
    }

    @Test
    void save_endDateBeforeStartDate_throwsValidationException() {
        sample.setEndDate(LocalDate.of(2010, 1, 1)); // antes de startDate
        assertThatThrownBy(() -> service.save(sample, 1L))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Validation failed");
        verify(repository, never()).save(any(), any());
    }

    @Test
    void save_nullEndDate_isAllowed() {
        sample.setEndDate(null); // estudio en curso, valido
        when(repository.save(any(Education.class), eq(1L))).thenAnswer(inv -> inv.getArgument(0));
        service.save(sample, 1L);
        verify(repository).save(sample, 1L);
    }

    @Test
    void update_whenNotFound_throwsResourceNotFound() {
        when(repository.findById(404L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.update(404L, sample, 1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_whenFound_updates() {
        when(repository.findById(7L)).thenReturn(Optional.of(sample));
        when(repository.save(any(Education.class), eq(1L))).thenAnswer(inv -> inv.getArgument(0));
        Education result = service.update(7L, sample, 1L);
        assertThat(result.getId()).isEqualTo(7L);
    }

    @Test
    void deleteById_whenMissing_throws() {
        when(repository.findById(404L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.deleteById(404L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
