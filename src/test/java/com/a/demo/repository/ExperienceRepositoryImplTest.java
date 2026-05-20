package com.a.demo.repository;

import com.a.demo.model.Experience;
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
@Import({ExperienceRepositoryImpl.class, PersonalInfoRepositoryImpl.class})
@ActiveProfiles("test")
class ExperienceRepositoryImplTest {

    @Autowired
    private ExperienceRepositoryImpl experienceRepository;

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

    private Experience buildExp(String jobTitle, LocalDate start, LocalDate end) {
        Experience ex = new Experience();
        ex.setJobTitle(jobTitle);
        ex.setCompanyName("Test Co");
        ex.setStartDate(start);
        ex.setEndDate(end);
        return ex;
    }

    @Test
    void findCurrentByPersonalInfoId_returnsOnlyOpenEnded() {
        experienceRepository.save(
                buildExp("Current Job", LocalDate.of(2024, 1, 1), null), personId);
        experienceRepository.save(
                buildExp("Old Job", LocalDate.of(2020, 1, 1), LocalDate.of(2023, 12, 31)), personId);

        assertThat(experienceRepository.findCurrentByPersonalInfoId(personId))
                .extracting(Experience::getJobTitle)
                .containsExactly("Current Job");
    }

    @Test
    void save_andFindById_preservesDates() {
        Experience saved = experienceRepository.save(
                buildExp("Backend", LocalDate.of(2022, 5, 10), LocalDate.of(2024, 3, 31)),
                personId);
        Experience reloaded = experienceRepository.findById(saved.getId()).orElseThrow();
        assertThat(reloaded.getStartDate()).isEqualTo(LocalDate.of(2022, 5, 10));
        assertThat(reloaded.getEndDate()).isEqualTo(LocalDate.of(2024, 3, 31));
    }
}
