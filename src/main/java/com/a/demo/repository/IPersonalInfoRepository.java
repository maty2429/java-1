package com.a.demo.repository;

import com.a.demo.model.PersonalInfo;

import java.util.List;
import java.util.Optional;

public interface IPersonalInfoRepository {
    PersonalInfo save(PersonalInfo personalInfo);
    /**
     * Busca un registro de PersonalInfo por su ID.
     * Devuelve un `Optional` que puede contener la PersonalInfo si se encuentra,
     * o estar vacío si no existe. Esto ayuda a manejar valores faltantes
     * de forma segura sin NullPointerExceptions.
     */
    Optional<PersonalInfo> findById(Long id);
    List<PersonalInfo> findAll();
    /**
     * Elimina un registro de PersonalInfo por su ID.
     * El tipo de retorno `void` indica que este método no devuelve un valor;
     * su propósito es únicamente realizar una acción (la eliminación).
     */
    void deleteById(Long id);
}
