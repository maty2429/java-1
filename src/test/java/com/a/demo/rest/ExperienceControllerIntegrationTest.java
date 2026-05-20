package com.a.demo.rest;

import com.a.demo.dto.request.ExperienceRequest;
import com.a.demo.dto.request.PersonalInfoRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ExperienceControllerIntegrationTest {

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

    private ExperienceRequest validExp() {
        ExperienceRequest e = new ExperienceRequest();
        e.setJobTitle("Backend Dev");
        e.setCompanyName("Tech Co");
        e.setStartDate(LocalDate.of(2022, 1, 1));
        return e;
    }

    @Test
    void post_validExperience_returns201() throws Exception {
        mockMvc.perform(post("/api/experiences/person/" + personId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validExp())))
                .andExpect(status().isCreated());
    }

    @Test
    void getCurrent_returnsOnlyOpenEnded() throws Exception {
        // Una sin endDate (actual)
        mockMvc.perform(post("/api/experiences/person/" + personId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validExp())))
                .andExpect(status().isCreated());

        // Una con endDate (vieja)
        ExperienceRequest old = validExp();
        old.setJobTitle("Old Job");
        old.setStartDate(LocalDate.of(2018, 1, 1));
        old.setEndDate(LocalDate.of(2020, 12, 31));
        mockMvc.perform(post("/api/experiences/person/" + personId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(old)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/experiences/person/" + personId + "/current"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].jobTitle").value("Backend Dev"));
    }

    @Test
    void post_missingJobTitle_returns400() throws Exception {
        ExperienceRequest e = validExp();
        e.setJobTitle(""); // viola @NotBlank

        mockMvc.perform(post("/api/experiences/person/" + personId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(e)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void delete_nonExistent_returns404() throws Exception {
        mockMvc.perform(delete("/api/experiences/99999"))
                .andExpect(status().isNotFound());
    }
}
