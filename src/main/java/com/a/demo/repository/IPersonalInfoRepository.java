package com.a.demo.repository;

import com.a.demo.model.PersonalInfo;

import java.util.List;
import java.util.Optional;

/**
 * Contrato del repositorio de PersonalInfo.
 * Definir la interfaz aparte permite testear el service con mocks facilmente
 * y cambiar la implementacion sin tocar quien la usa.
 */
public interface IPersonalInfoRepository {

    PersonalInfo save(PersonalInfo personalInfo);

    /**
     * Busca un registro de PersonalInfo por su ID.
     * Devuelve un Optional que puede contener la PersonalInfo si se encuentra,
     * o estar vacio si no existe. Esto ayuda a manejar valores faltantes
     * de forma segura sin NullPointerExceptions.
     */
    Optional<PersonalInfo> findById(Long id);

    /** Busqueda exacta por email (util para login, evitar duplicados, etc). */
    Optional<PersonalInfo> findByEmail(String email);

    List<PersonalInfo> findAll();

    /** Chequea si existe sin traer la fila entera (mas eficiente que findById si solo te importa eso). */
    boolean existsById(Long id);

    /**
     * Elimina un registro de PersonalInfo por su ID.
     * El tipo de retorno void indica que este metodo no devuelve un valor;
     * su proposito es unicamente realizar una accion (la eliminacion).
     */
    void deleteById(Long id);
}
