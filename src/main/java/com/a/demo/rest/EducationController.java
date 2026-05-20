package com.a.demo.rest;

import com.a.demo.dto.mapper.EducationMapper;
import com.a.demo.dto.request.EducationRequest;
import com.a.demo.dto.response.EducationResponse;
import com.a.demo.model.Education;
import com.a.demo.service.IEducationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * Controlador REST para Education.
 *
 * Rutas:
 *   GET    /api/educations                          -> todas
 *   GET    /api/educations/search?institution=XXX   -> busqueda por institucion (contains)
 *   GET    /api/educations/{id}                     -> una
 *   GET    /api/educations/person/{personId}        -> las de una persona
 *   POST   /api/educations/person/{personId}        -> crear
 *   PUT    /api/educations/person/{personId}/{id}   -> actualizar
 *   DELETE /api/educations/{id}                     -> borrar
 */
@RestController
@RequestMapping("/api/educations")
@RequiredArgsConstructor
public class EducationController {

    private final IEducationService educationService;
    private final EducationMapper mapper;

    @GetMapping
    public ResponseEntity<List<EducationResponse>> getAllEducations() {
        return ResponseEntity.ok(mapper.toResponseList(educationService.findAll()));
    }

    @GetMapping("/all")
    public ResponseEntity<List<EducationResponse>> getAllAlias() {
        return getAllEducations();
    }

    @GetMapping("/search")
    public ResponseEntity<List<EducationResponse>> searchByInstitution(@RequestParam String institution) {
        return ResponseEntity.ok(mapper.toResponseList(educationService.findByInstitution(institution)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EducationResponse> getEducationById(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toResponse(educationService.findByIdOrThrow(id)));
    }

    @GetMapping("/person/{personId}")
    public ResponseEntity<List<EducationResponse>> getEducationsByPerson(@PathVariable Long personId) {
        return ResponseEntity.ok(mapper.toResponseList(educationService.findByPersonalInfoId(personId)));
    }

    @PostMapping("/person/{personId}")
    public ResponseEntity<EducationResponse> createEducation(@PathVariable Long personId,
                                                              @Valid @RequestBody EducationRequest request) {
        Education saved = educationService.save(mapper.toEntity(request), personId);
        URI location = URI.create("/api/educations/" + saved.getId());
        return ResponseEntity.created(location).body(mapper.toResponse(saved));
    }

    /**
     * Actualizar una educacion. Incluimos personId en la ruta para mantener la relacion clara.
     */
    @PutMapping("/person/{personId}/{id}")
    public ResponseEntity<EducationResponse> updateEducation(@PathVariable Long personId,
                                                              @PathVariable Long id,
                                                              @Valid @RequestBody EducationRequest request) {
        Education updated = educationService.update(id, mapper.toEntity(request), personId);
        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEducation(@PathVariable Long id) {
        educationService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
