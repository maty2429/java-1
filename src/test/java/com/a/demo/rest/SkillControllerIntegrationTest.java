package com.a.demo.rest;

import com.a.demo.dto.request.PersonalInfoRequest;
import com.a.demo.dto.request.SkillRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SkillControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long personId;

    @BeforeEach
    void setUp() throws Exception {
        PersonalInfoRequest p = new PersonalInfoRequest();
        p.setFirstName("Owner");
        p.setLastName("Test");
        p.setTitle("Dev");
        p.setProfileDescription("desc");

        String response = mockMvc.perform(post("/api/personal-info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(p)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        personId = objectMapper.readTree(response).get("id").asLong();
    }

    private SkillRequest validSkill() {
        SkillRequest s = new SkillRequest();
        s.setName("Java");
        s.setLevelPercentage(90);
        s.setIconClass("fab fa-java");
        return s;
    }

    @Test
    void post_validSkill_returns201() throws Exception {
        mockMvc.perform(post("/api/skills/person/" + personId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validSkill())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Java"));
    }

    @Test
    void post_invalidLevel_returns400() throws Exception {
        SkillRequest bad = validSkill();
        bad.setLevelPercentage(150); // > 100

        mockMvc.perform(post("/api/skills/person/" + personId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bad)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void put_existingSkill_preservesOwnerAndUpdates() throws Exception {
        // Crear
        String response = mockMvc.perform(post("/api/skills/person/" + personId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validSkill())))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long skillId = objectMapper.readTree(response).get("id").asLong();

        // Actualizar
        SkillRequest update = validSkill();
        update.setName("Spring Boot");
        update.setLevelPercentage(85);

        mockMvc.perform(put("/api/skills/" + skillId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Spring Boot"));

        // Confirmar que sigue asociada al mismo person
        mockMvc.perform(get("/api/skills/person/" + personId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Spring Boot"));
    }

    @Test
    void getByMinLevel_filters() throws Exception {
        SkillRequest high = validSkill();
        high.setName("High");
        high.setLevelPercentage(90);
        mockMvc.perform(post("/api/skills/person/" + personId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(high)))
                .andExpect(status().isCreated());

        SkillRequest low = validSkill();
        low.setName("Low");
        low.setLevelPercentage(40);
        mockMvc.perform(post("/api/skills/person/" + personId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(low)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/skills").param("minLevel", "70"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("High"));
    }

    @Test
    void getById_nonExistent_returns404() throws Exception {
        mockMvc.perform(get("/api/skills/99999"))
                .andExpect(status().isNotFound());
    }
}
