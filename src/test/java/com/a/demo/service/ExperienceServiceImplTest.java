package com.a.demo.service;

import com.a.demo.exception.ResourceNotFoundException;
import com.a.demo.exception.ValidationException;
import com.a.demo.model.Experience;
import com.a.demo.repository.IExperienceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExperienceServiceImplTest {

    @Mock
    private IExperienceRepository repository;

    @InjectMocks
    private ExperienceServiceImpl service;

    private Experience sample;

    @BeforeEach
    void setUp() {
        sample = new Experience();
        sample.setJobTitle("Backend Dev");
        sample.setCompanyName("Tech Co");
        sample.setStartDate(LocalDate.of(2022, 1, 1));
    }

    @Test
    void save_validDates_callsRepo() {
        when(repository.save(any(Experience.class), eq(1L))).thenAnswer(inv -> inv.getArgument(0));
        service.save(sample, 1L);
        verify(repository).save(sample, 1L);
    }

    @Test
    void save_endDateBeforeStartDate_throws() {
        sample.setEndDate(LocalDate.of(2020, 1, 1));
        assertThatThrownBy(() -> service.save(sample, 1L))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void findCurrentByPersonalInfoId_delegates() {
        when(repository.findCurrentByPersonalInfoId(1L)).thenReturn(List.of(sample));
        assertThat(service.findCurrentByPersonalInfoId(1L)).hasSize(1);
    }

    @Test
    void update_whenNotFound_throws() {
        when(repository.findById(404L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.update(404L, sample, 1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteById_whenMissing_throws() {
        when(repository.findById(404L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.deleteById(404L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
