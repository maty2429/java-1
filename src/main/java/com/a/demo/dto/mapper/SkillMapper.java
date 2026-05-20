package com.a.demo.dto.mapper;

import com.a.demo.dto.request.SkillRequest;
import com.a.demo.dto.response.SkillResponse;
import com.a.demo.model.Skill;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper "a mano" entre Skill (entidad) y sus DTOs.
 *
 * Por que un Mapper?
 *   Mantiene el codigo de conversion en un solo lugar (DRY).
 *   Los services y controllers no se llenan de "new SkillResponse(...)" repetidos.
 *
 * Por que @Component?
 *   Para que Spring lo inyecte donde se necesite (services, controllers).
 *
 * En proyectos grandes se suele usar MapStruct (generacion automatica), pero
 * a mano queda mas claro mientras se aprende.
 */
@Component
public class SkillMapper {

    /** Convierte un request entrante en una entidad nueva (sin id). */
    public Skill toEntity(SkillRequest request) {
        if (request == null) return null;
        Skill skill = new Skill();
        skill.setName(request.getName());
        skill.setLevelPercentage(request.getLevelPercentage());
        skill.setIconClass(request.getIconClass());
        return skill;
    }

    /** Convierte una entidad de BD en el response que devolvemos al cliente. */
    public SkillResponse toResponse(Skill skill) {
        if (skill == null) return null;
        return SkillResponse.builder()
                .id(skill.getId())
                .name(skill.getName())
                .levelPercentage(skill.getLevelPercentage())
                .iconClass(skill.getIconClass())
                .build();
    }

    /** Conveniencia para listas. */
    public List<SkillResponse> toResponseList(List<Skill> skills) {
        return skills.stream().map(this::toResponse).toList();
    }
}
