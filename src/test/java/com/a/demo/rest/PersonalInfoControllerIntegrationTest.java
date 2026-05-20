package com.a.demo.rest;

import com.a.demo.dto.request.PersonalInfoRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test del controlador con MockMvc.
 *
 * @SpringBootTest -> levanta toda la app real (controllers + services + repos + BD).
 * @AutoConfigureMockMvc -> configura un cliente HTTP simulado (MockMvc).
 * @ActiveProfiles("test") -> usa H2 en memoria (no toca Postgres real).
 *
 * Para que esto funcione contra H2 necesitamos que en application-test.properties
 * spring.sql.init.platform=h2 (asi se ejecuta schema-h2.sql).
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PersonalInfoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private PersonalInfoRequest validRequest() {
        PersonalInfoRequest r = new PersonalInfoRequest();
        r.setFirstName("Ana");
        r.setLastName("Garcia");
        r.setTitle("Backend Dev");
        r.setProfileDescription("Apasionada por backend");
        r.setEmail("ana@example.com");
        return r;
    }

    @Test
    void post_validRequest_returns201() throws Exception {
        mockMvc.perform(post("/api/personal-info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.firstName").value("Ana"));
    }

    @Test
    void post_missingFirstName_returns400WithErrorJson() throws Exception {
        PersonalInfoRequest bad = validRequest();
        bad.setFirstName(""); // viola @NotBlank

        mockMvc.perform(post("/api/personal-info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bad)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void post_invalidEmail_returns400() throws Exception {
        PersonalInfoRequest bad = validRequest();
        bad.setEmail("no-es-email");

        mockMvc.perform(post("/api/personal-info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bad)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getById_nonExistent_returns404Json() throws Exception {
        mockMvc.perform(get("/api/personal-info/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("PersonalInfo not found with id: 99999"));
    }

    @Test
    void put_existingId_returnsUpdated() throws Exception {
        // Primero creamos
        String response = mockMvc.perform(post("/api/personal-info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Number id = objectMapper.readTree(response).get("id").numberValue();

        // Luego actualizamos
        PersonalInfoRequest updated = validRequest();
        updated.setTitle("Senior Backend Dev");

        mockMvc.perform(put("/api/personal-info/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Senior Backend Dev"));
    }

    @Test
    void delete_existingId_returns204() throws Exception {
        String response = mockMvc.perform(post("/api/personal-info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Number id = objectMapper.readTree(response).get("id").numberValue();

        mockMvc.perform(delete("/api/personal-info/" + id))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_nonExistent_returns404() throws Exception {
        mockMvc.perform(delete("/api/personal-info/99999"))
                .andExpect(status().isNotFound());
    }
}
