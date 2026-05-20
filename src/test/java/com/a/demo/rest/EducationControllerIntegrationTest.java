package com.a.demo.rest;

import com.a.demo.dto.request.EducationRequest;
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
class EducationControllerIntegrationTest {

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

    private EducationRequest validEducation() {
        EducationRequest e = new EducationRequest();
        e.setDegree("Ing. Sistemas");
        e.setInstitution("Universidad XYZ");
        e.setStartDate(LocalDate.of(2015, 3, 1));
        e.setEndDate(LocalDate.of(2020, 12, 15));
        e.setDescription("desc");
        return e;
    }

    @Test
    void post_validEducation_returns201() throws Exception {
        mockMvc.perform(post("/api/educations/person/" + personId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validEducation())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.degree").value("Ing. Sistemas"));
    }

    @Test
    void post_futureStartDate_returns400() throws Exception {
        EducationRequest e = validEducation();
        e.setStartDate(LocalDate.now().plusYears(1)); // futuro
        mockMvc.perform(post("/api/educations/person/" + personId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(e)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void post_endBeforeStart_returns400_crossFieldValidation() throws Exception {
        EducationRequest e = validEducation();
        e.setStartDate(LocalDate.of(2020, 1, 1));
        e.setEndDate(LocalDate.of(2015, 1, 1)); // antes
        mockMvc.perform(post("/api/educations/person/" + personId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(e)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void put_updatesEducation() throws Exception {
        String response = mockMvc.perform(post("/api/educations/person/" + personId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validEducation())))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long id = objectMapper.readTree(response).get("id").asLong();

        EducationRequest update = validEducation();
        update.setDegree("Master en Ing.");

        mockMvc.perform(put("/api/educations/person/" + personId + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.degree").value("Master en Ing."));
    }

    @Test
    void getById_missing_returns404() throws Exception {
        mockMvc.perform(get("/api/educations/99999"))
                .andExpect(status().isNotFound());
    }
}
