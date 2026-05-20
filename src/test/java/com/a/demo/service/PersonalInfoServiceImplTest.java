package com.a.demo.service;

import com.a.demo.exception.ResourceNotFoundException;
import com.a.demo.exception.ValidationException;
import com.a.demo.model.PersonalInfo;
import com.a.demo.repository.IPersonalInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit test del service usando Mockito.
 *
 * @ExtendWith(MockitoExtension.class) -> habilita Mockito en JUnit 5.
 * @Mock -> crea un objeto simulado (no se va a la BD de verdad).
 * @InjectMocks -> crea la instancia del service inyectando los @Mock como dependencias.
 *
 * Filosofia del unit test:
 *   No tocamos la BD. No levantamos Spring. Solo probamos la logica del service
 *   en aislamiento, fingiendo que el repositorio responde lo que nosotros decimos.
 */
@ExtendWith(MockitoExtension.class)
class PersonalInfoServiceImplTest {

    @Mock
    private IPersonalInfoRepository repository;

    @Mock
    private Validator validator;

    @InjectMocks
    private PersonalInfoServiceImpl service;

    private PersonalInfo sample;

    @BeforeEach
    void setUp() {
        sample = new PersonalInfo();
        sample.setFirstName("Juan");
        sample.setLastName("Perez");
        sample.setTitle("Dev");
        sample.setProfileDescription("desc");
        sample.setEmail("juan@example.com");
    }

    @Test
    void save_validInput_returnsSaved() {
        // El validator no agrega errores -> la validacion pasa
        doNothing().when(validator).validate(any(PersonalInfo.class), any(BindingResult.class));
        // El repo devuelve la entidad con id asignado
        sample.setId(1L);
        when(repository.save(any(PersonalInfo.class))).thenReturn(sample);

        PersonalInfo result = service.save(sample);

        assertThat(result.getId()).isEqualTo(1L);
        verify(repository).save(sample);
    }

    @Test
    void save_invalidInput_throwsValidationException() {
        // Simulamos que el validator detecta un error: rechazamos campo "firstName"
        doAnswer(inv -> {
            BindingResult r = inv.getArgument(1);
            r.rejectValue("firstName", "NotBlank", "First name is mandatory");
            return null;
        }).when(validator).validate(any(PersonalInfo.class), any(BeanPropertyBindingResult.class));

        assertThatThrownBy(() -> service.save(sample))
                .isInstanceOf(ValidationException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void update_whenExists_updatesAndReturns() {
        when(repository.existsById(7L)).thenReturn(true);
        when(repository.save(any(PersonalInfo.class))).thenAnswer(inv -> inv.getArgument(0));

        PersonalInfo updated = service.update(7L, sample);

        assertThat(updated.getId()).isEqualTo(7L);
        verify(repository).save(any(PersonalInfo.class));
    }

    @Test
    void update_whenNotExists_throwsResourceNotFound() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.update(99L, sample))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void findByIdOrThrow_whenExists_returnsIt() {
        sample.setId(5L);
        when(repository.findById(5L)).thenReturn(Optional.of(sample));

        PersonalInfo result = service.findByIdOrThrow(5L);

        assertThat(result.getId()).isEqualTo(5L);
    }

    @Test
    void findByIdOrThrow_whenNotExists_throws() {
        when(repository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findByIdOrThrow(404L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findAll_returnsList() {
        when(repository.findAll()).thenReturn(List.of(sample));
        assertThat(service.findAll()).hasSize(1);
    }

    @Test
    void deleteById_whenExists_callsRepo() {
        when(repository.existsById(3L)).thenReturn(true);
        service.deleteById(3L);
        verify(repository).deleteById(3L);
    }

    @Test
    void deleteById_whenNotExists_throws() {
        when(repository.existsById(404L)).thenReturn(false);
        assertThatThrownBy(() -> service.deleteById(404L))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(repository, never()).deleteById(any());
    }
}
