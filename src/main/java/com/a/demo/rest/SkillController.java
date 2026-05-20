package com.a.demo.rest;

import com.a.demo.dto.mapper.SkillMapper;
import com.a.demo.dto.request.SkillRequest;
import com.a.demo.dto.response.SkillResponse;
import com.a.demo.model.Skill;
import com.a.demo.service.ISkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * Controlador REST para Skill.
 *
 * Rutas:
 *   GET    /api/skills                      -> todas
 *   GET    /api/skills?minLevel=70          -> filtradas por nivel minimo
 *   GET    /api/skills/top?limit=5          -> top N habilidades
 *   GET    /api/skills/{id}                 -> una por id
 *   GET    /api/skills/person/{personId}    -> las de una persona
 *   POST   /api/skills/person/{personId}    -> crear bajo una persona
 *   PUT    /api/skills/{id}                 -> actualizar
 *   DELETE /api/skills/{id}                 -> borrar
 */
@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {

    private final ISkillService skillService;
    private final SkillMapper mapper;

    @GetMapping
    public ResponseEntity<List<SkillResponse>> getAllSkills(
            @RequestParam(name = "minLevel", required = false) Integer minLevel) {
        // Si pasan ?minLevel=N, filtramos; si no, devolvemos todas.
        List<Skill> skills = (minLevel != null)
                ? skillService.findByMinLevel(minLevel)
                : skillService.findAll();
        return ResponseEntity.ok(mapper.toResponseList(skills));
    }

    /** Alias antiguo, lo mantenemos para no romper clientes existentes. */
    @GetMapping("/all")
    public ResponseEntity<List<SkillResponse>> getAllAlias() {
        return getAllSkills(null);
    }

    @GetMapping("/top")
    public ResponseEntity<List<SkillResponse>> getTopSkills(
            @RequestParam(name = "limit", defaultValue = "5") int limit) {
        return ResponseEntity.ok(mapper.toResponseList(skillService.findTopSkills(limit)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SkillResponse> getSkillById(@PathVariable Long id) {
        // findByIdOrThrow lanza ResourceNotFoundException si no existe -> 404 automatico.
        return ResponseEntity.ok(mapper.toResponse(skillService.findByIdOrThrow(id)));
    }

    @GetMapping("/person/{personId}")
    public ResponseEntity<List<SkillResponse>> getSkillsByPerson(@PathVariable Long personId) {
        return ResponseEntity.ok(mapper.toResponseList(skillService.findByPersonalInfoId(personId)));
    }

    @PostMapping("/person/{personId}")
    public ResponseEntity<SkillResponse> createSkill(@PathVariable Long personId,
                                                     @Valid @RequestBody SkillRequest request) {
        Skill saved = skillService.save(mapper.toEntity(request), personId);
        URI location = URI.create("/api/skills/" + saved.getId());
        return ResponseEntity.created(location).body(mapper.toResponse(saved));
    }

    /**
     * Actualizar una skill. El service se encarga de preservar el personalInfoId original
     * (arregla el bug anterior donde se enviaba null).
     */
    @PutMapping("/{id}")
    public ResponseEntity<SkillResponse> updateSkill(@PathVariable Long id,
                                                     @Valid @RequestBody SkillRequest request) {
        Skill updated = skillService.update(id, mapper.toEntity(request));
        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSkill(@PathVariable Long id) {
        skillService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
