package com.a.demo.repository;

import com.a.demo.model.PersonalInfo;

import java.util.List;
import java.util.Optional;

public interface IPersonalInfoRepository {
    PersonalInfo save(PersonalInfo personalInfo);
    /**
     * Busca una PersonalInfo por su ID.
     * Retorna un `Optional` que puede contener la PersonalInfo si se encuentra,
     * o estar vacío si no existe. Esto ayuda a manejar la ausencia de valores
     * sin riesgo de NullPointerExceptions.
     */
    Optional<PersonalInfo> findById(Long id);
    List<PersonalInfo> findAll();
    /**
     * Elimina una PersonalInfo por su ID.
     * El tipo `void` indica que este método no devuelve ningún valor;
     * su propósito es solo realizar una acción (la eliminación).
     */
    void deleteById(Long id);
}
