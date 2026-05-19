package com.a.demo.rest;

import com.a.demo.model.Skill;
import com.a.demo.service.ISkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {

    private final ISkillService skillService;

    @GetMapping("/all")
    public ResponseEntity<List<Skill>> getAllSkills() {
        return ResponseEntity.ok(skillService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Skill> getSkillById(@PathVariable Long id) {
        return skillService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/person/{personId}")
    public ResponseEntity<List<Skill>> getSkillsByPerson(@PathVariable Long personId) {
        return ResponseEntity.ok(skillService.findByPersonalInfoId(personId));
    }

    @PostMapping("/person/{personId}")
    public ResponseEntity<Skill> createSkill(@PathVariable Long personId, @RequestBody Skill skill) {
        return ResponseEntity.ok(skillService.save(skill, personId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Skill> updateSkill(@PathVariable Long id, @RequestBody Skill skill) {
        return skillService.findById(id)
                .map(existing -> {
                    skill.setId(id);
                    // Note: In a real app, we might need the personId here too, 
                    // or keep it from the existing record. For simplicity:
                    return ResponseEntity.ok(skillService.save(skill, null)); 
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSkill(@PathVariable Long id) {
        skillService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
