package com.a.demo.repository;

import com.a.demo.model.PersonalInfo;
import com.a.demo.model.Skill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests del repositorio Skill contra H2 in-memory.
 * Importamos tambien PersonalInfoRepositoryImpl porque cada Skill necesita
 * una PersonalInfo "duenia" (FK).
 */
@JdbcTest
@Import({SkillRepositoryImpl.class, PersonalInfoRepositoryImpl.class})
@ActiveProfiles("test")
class SkillRepositoryImplTest {

    @Autowired
    private SkillRepositoryImpl skillRepository;

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

    private Skill buildSkill(String name, int level) {
        Skill s = new Skill();
        s.setName(name);
        s.setLevelPercentage(level);
        s.setIconClass("fab fa-java");
        return s;
    }

    @Test
    void save_assignsId() {
        Skill saved = skillRepository.save(buildSkill("Java", 90), personId);
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void findByMinLevel_returnsOnlyHighLevel() {
        skillRepository.save(buildSkill("Java", 90), personId);
        skillRepository.save(buildSkill("Css", 60), personId);

        assertThat(skillRepository.findByMinLevel(70))
                .extracting(Skill::getName)
                .containsExactly("Java");
    }

    @Test
    void findTopSkills_respectsLimit() {
        skillRepository.save(buildSkill("Java", 90), personId);
        skillRepository.save(buildSkill("Spring", 85), personId);
        skillRepository.save(buildSkill("Css", 60), personId);

        assertThat(skillRepository.findTopSkills(2)).hasSize(2);
    }

    @Test
    void findPersonalInfoIdBySkillId_returnsOwner() {
        Skill saved = skillRepository.save(buildSkill("Java", 90), personId);
        assertThat(skillRepository.findPersonalInfoIdBySkillId(saved.getId()))
                .hasValue(personId);
    }

    @Test
    void findByPersonalInfoId_returnsOwnSkills() {
        skillRepository.save(buildSkill("Java", 90), personId);
        assertThat(skillRepository.findByPersonalInfoId(personId)).hasSize(1);
    }

    @Test
    void deleteById_removes() {
        Skill saved = skillRepository.save(buildSkill("Java", 90), personId);
        skillRepository.deleteById(saved.getId());
        assertThat(skillRepository.findById(saved.getId())).isEmpty();
    }
}
