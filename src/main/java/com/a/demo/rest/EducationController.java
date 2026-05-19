package com.a.demo.rest;

import com.a.demo.model.Education;
import com.a.demo.service.IEducationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/educations")
@RequiredArgsConstructor
public class EducationController {

    private final IEducationService educationService;

    @GetMapping("/all")
    public ResponseEntity<List<Education>> getAllEducations() {
        return ResponseEntity.ok(educationService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Education> getEducationById(@PathVariable Long id) {
        return educationService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/person/{personId}")
    public ResponseEntity<List<Education>> getEducationsByPerson(@PathVariable Long personId) {
        return ResponseEntity.ok(educationService.findByPersonalInfoId(personId));
    }

    @PostMapping("/person/{personId}")
    public ResponseEntity<Education> createEducation(@PathVariable Long personId, @RequestBody Education education) {
        return ResponseEntity.ok(educationService.save(education, personId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEducation(@PathVariable Long id) {
        educationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
