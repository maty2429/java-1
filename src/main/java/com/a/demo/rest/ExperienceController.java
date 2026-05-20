package com.a.demo.rest;

import com.a.demo.dto.mapper.ExperienceMapper;
import com.a.demo.dto.request.ExperienceRequest;
import com.a.demo.dto.response.ExperienceResponse;
import com.a.demo.model.Experience;
import com.a.demo.service.IExperienceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * Controlador REST para Experience.
 *
 * Endpoint extra util: /person/{personId}/current devuelve solo las experiencias actuales
 * (trabajos en curso). Es la clase de filtro que aparece todo el tiempo en proyectos reales.
 */
@RestController
@RequestMapping("/api/experiences")
@RequiredArgsConstructor
public class ExperienceController {

    private final IExperienceService experienceService;
    private final ExperienceMapper mapper;

    @GetMapping
    public ResponseEntity<List<ExperienceResponse>> getAllExperiences() {
        return ResponseEntity.ok(mapper.toResponseList(experienceService.findAll()));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ExperienceResponse>> getAllAlias() {
        return getAllExperiences();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExperienceResponse> getExperienceById(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toResponse(experienceService.findByIdOrThrow(id)));
    }

    @GetMapping("/person/{personId}")
    public ResponseEntity<List<ExperienceResponse>> getExperiencesByPerson(@PathVariable Long personId) {
        return ResponseEntity.ok(mapper.toResponseList(experienceService.findByPersonalInfoId(personId)));
    }

    /** Experiencias actuales de la persona (donde endDate IS NULL). */
    @GetMapping("/person/{personId}/current")
    public ResponseEntity<List<ExperienceResponse>> getCurrentByPerson(@PathVariable Long personId) {
        return ResponseEntity.ok(mapper.toResponseList(experienceService.findCurrentByPersonalInfoId(personId)));
    }

    @PostMapping("/person/{personId}")
    public ResponseEntity<ExperienceResponse> createExperience(@PathVariable Long personId,
                                                                @Valid @RequestBody ExperienceRequest request) {
        Experience saved = experienceService.save(mapper.toEntity(request), personId);
        URI location = URI.create("/api/experiences/" + saved.getId());
        return ResponseEntity.created(location).body(mapper.toResponse(saved));
    }

    @PutMapping("/person/{personId}/{id}")
    public ResponseEntity<ExperienceResponse> updateExperience(@PathVariable Long personId,
                                                                @PathVariable Long id,
                                                                @Valid @RequestBody ExperienceRequest request) {
        Experience updated = experienceService.update(id, mapper.toEntity(request), personId);
        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExperience(@PathVariable Long id) {
        experienceService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
