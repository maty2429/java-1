package com.a.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de salida (Response) para Skill.
 * Lo que devolvemos al cliente cuando consulta o crea una Skill.
 *
 * NO lleva validaciones porque es respuesta (no entrada).
 * Lleva el id porque el cliente necesita conocerlo para futuras operaciones.
 *
 * @Builder (Lombok): nos da un patron builder fluido para construir el objeto:
 *   SkillResponse.builder().id(1L).name("Java").build();
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SkillResponse {
    private Long id;
    private String name;
    private Integer levelPercentage;
    private String iconClass;
}
