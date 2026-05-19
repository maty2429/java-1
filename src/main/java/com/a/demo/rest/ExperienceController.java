package com.a.demo.rest;

import com.a.demo.model.Experience;
import com.a.demo.service.IExperienceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/experiences")
@RequiredArgsConstructor
public class ExperienceController {

    private final IExperienceService experienceService;

    @GetMapping("/all")
    public ResponseEntity<List<Experience>> getAllExperiences() {
        return ResponseEntity.ok(experienceService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Experience> getExperienceById(@PathVariable Long id) {
        return experienceService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/person/{personId}")
    public ResponseEntity<List<Experience>> getExperiencesByPerson(@PathVariable Long personId) {
        return ResponseEntity.ok(experienceService.findByPersonalInfoId(personId));
    }

    @PostMapping("/person/{personId}")
    public ResponseEntity<Experience> createExperience(@PathVariable Long personId, @RequestBody Experience experience) {
        return ResponseEntity.ok(experienceService.save(experience, personId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExperience(@PathVariable Long id) {
        experienceService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
