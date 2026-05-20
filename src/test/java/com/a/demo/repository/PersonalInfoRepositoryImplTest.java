package com.a.demo.repository;

import com.a.demo.model.PersonalInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test del repositorio con base de datos real (H2 in-memory).
 *
 * @JdbcTest -> levanta solo lo minimo de Spring para tests con JdbcTemplate.
 *              Crea un DataSource embebido (H2), aplica schema.sql, hace rollback
 *              automatico al final de cada test (no contamina otros tests).
 * @Import   -> @JdbcTest no escanea @Repository por defecto. Lo importamos a mano.
 * @ActiveProfiles("test") -> usa application-test.properties (H2 + MODE=PostgreSQL + schema-h2.sql).
 */
@JdbcTest
@Import(PersonalInfoRepositoryImpl.class)
@ActiveProfiles("test")
class PersonalInfoRepositoryImplTest {

    @Autowired
    private PersonalInfoRepositoryImpl repository;

    @Autowired
    private JdbcTemplate jdbc;

    private PersonalInfo buildSample() {
        PersonalInfo p = new PersonalInfo();
        p.setFirstName("Ana");
        p.setLastName("Garcia");
        p.setTitle("Backend Dev");
        p.setProfileDescription("Apasionada por backend");
        p.setYearsOfExperience(3);
        p.setEmail("ana@example.com");
        return p;
    }

    @Test
    void save_assignsGeneratedId() {
        PersonalInfo saved = repository.save(buildSample());
        assertThat(saved.getId()).isNotNull().isPositive();
    }

    @Test
    void findById_existing_returnsIt() {
        PersonalInfo saved = repository.save(buildSample());
        Optional<PersonalInfo> found = repository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("Ana");
    }

    @Test
    void findById_missing_returnsEmpty() {
        assertThat(repository.findById(99999L)).isEmpty();
    }

    @Test
    void findByEmail_existing_returnsIt() {
        repository.save(buildSample());
        Optional<PersonalInfo> found = repository.findByEmail("ana@example.com");
        assertThat(found).isPresent();
    }

    @Test
    void findAll_returnsAllRecords() {
        repository.save(buildSample());
        PersonalInfo other = buildSample();
        other.setEmail("otra@example.com");
        repository.save(other);

        List<PersonalInfo> all = repository.findAll();
        assertThat(all).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void existsById_trueWhenExists() {
        PersonalInfo saved = repository.save(buildSample());
        assertThat(repository.existsById(saved.getId())).isTrue();
    }

    @Test
    void existsById_falseWhenMissing() {
        assertThat(repository.existsById(99999L)).isFalse();
    }

    @Test
    void update_modifiesFields() {
        PersonalInfo saved = repository.save(buildSample());
        saved.setTitle("Senior Backend Dev");
        repository.save(saved);

        Optional<PersonalInfo> updated = repository.findById(saved.getId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getTitle()).isEqualTo("Senior Backend Dev");
    }

    @Test
    void deleteById_removesRecord() {
        PersonalInfo saved = repository.save(buildSample());
        repository.deleteById(saved.getId());
        assertThat(repository.findById(saved.getId())).isEmpty();
    }
}
