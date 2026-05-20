package com.a.demo.repository;

import com.a.demo.model.Education;
import com.a.demo.model.PersonalInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import({EducationRepositoryImpl.class, PersonalInfoRepositoryImpl.class})
@ActiveProfiles("test")
class EducationRepositoryImplTest {

    @Autowired
    private EducationRepositoryImpl educationRepository;

    @Autowired
    private PersonalInfoRepositoryImpl personalInfoRepository;

    private Long personId;

    @BeforeEach
    void setUp() {
        PersonalInfo p = new PersonalInfo();
        p.setFirstName("Test");
        p.setLastName("User");
        p.setTitle("Dev");
        p.setProfileDescription("desc");
        personId = personalInfoRepository.save(p).getId();
    }

    private Education buildEducation(LocalDate start, LocalDate end) {
        Education e = new Education();
        e.setDegree("Ing. Sistemas");
        e.setInstitution("Universidad XYZ");
        e.setStartDate(start);
        e.setEndDate(end);
        e.setDescription("desc");
        return e;
    }

    @Test
    void save_assignsId() {
        Education saved = educationRepository.save(
                buildEducation(LocalDate.of(2015, 3, 1), LocalDate.of(2020, 12, 15)),
                personId);
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void save_withNullEndDate_persistsAsNull() {
        Education saved = educationRepository.save(
                buildEducation(LocalDate.of(2023, 1, 1), null),
                personId);
        // Releemos para asegurarnos que viaja ida-y-vuelta
        Education reloaded = educationRepository.findById(saved.getId()).orElseThrow();
        assertThat(reloaded.getEndDate()).isNull();
    }

    @Test
    void findByInstitution_caseInsensitiveContains() {
        educationRepository.save(
                buildEducation(LocalDate.of(2015, 3, 1), LocalDate.of(2020, 12, 15)),
                personId);
        assertThat(educationRepository.findByInstitution("xyz")).hasSize(1);
        assertThat(educationRepository.findByInstitution("XYZ")).hasSize(1);
        assertThat(educationRepository.findByInstitution("nope")).isEmpty();
    }

    @Test
    void update_modifies() {
        Education saved = educationRepository.save(
                buildEducation(LocalDate.of(2015, 3, 1), null), personId);
        saved.setDegree("Master Ing.");
        educationRepository.save(saved, personId);

        assertThat(educationRepository.findById(saved.getId()).orElseThrow().getDegree())
                .isEqualTo("Master Ing.");
    }
}
