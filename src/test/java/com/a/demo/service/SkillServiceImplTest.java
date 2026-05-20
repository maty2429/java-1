package com.a.demo.service;

import com.a.demo.exception.ResourceNotFoundException;
import com.a.demo.model.Skill;
import com.a.demo.repository.ISkillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillServiceImplTest {

    @Mock
    private ISkillRepository repository;

    @InjectMocks
    private SkillServiceImpl service;

    private Skill sample;

    @BeforeEach
    void setUp() {
        sample = new Skill();
        sample.setName("Java");
        sample.setLevelPercentage(90);
    }

    @Test
    void save_callsRepo() {
        when(repository.save(sample, 1L)).thenReturn(sample);
        Skill result = service.save(sample, 1L);
        assertThat(result).isSameAs(sample);
    }

    @Test
    void update_preservesPersonalInfoId() {
        // El service tiene que recuperar el personal_info_id existente y pasarlo al save.
        when(repository.findPersonalInfoIdBySkillId(10L)).thenReturn(Optional.of(99L));
        when(repository.save(any(Skill.class), eq(99L))).thenAnswer(inv -> inv.getArgument(0));

        Skill input = new Skill();
        input.setName("Spring");
        input.setLevelPercentage(80);

        Skill result = service.update(10L, input);

        assertThat(result.getId()).isEqualTo(10L);
        verify(repository).save(any(Skill.class), eq(99L));
    }

    @Test
    void update_whenSkillNotFound_throws() {
        when(repository.findPersonalInfoIdBySkillId(404L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.update(404L, sample))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findByIdOrThrow_whenMissing_throws() {
        when(repository.findById(404L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.findByIdOrThrow(404L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findByMinLevel_delegates() {
        when(repository.findByMinLevel(70)).thenReturn(List.of(sample));
        assertThat(service.findByMinLevel(70)).hasSize(1);
    }

    @Test
    void findTopSkills_invalidLimit_throwsIllegalArgument() {
        assertThatThrownBy(() -> service.findTopSkills(0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void findTopSkills_validLimit_delegates() {
        when(repository.findTopSkills(3)).thenReturn(List.of(sample));
        assertThat(service.findTopSkills(3)).hasSize(1);
    }

    @Test
    void deleteById_whenExists_callsRepo() {
        when(repository.findById(1L)).thenReturn(Optional.of(sample));
        service.deleteById(1L);
        verify(repository).deleteById(1L);
    }

    @Test
    void deleteById_whenMissing_throws() {
        when(repository.findById(404L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.deleteById(404L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
