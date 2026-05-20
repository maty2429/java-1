package com.a.demo.repository;

import com.a.demo.model.Education;

import java.util.List;
import java.util.Optional;

/**
 * Contrato (interfaz) para el repositorio de Education.
 * Definir la interfaz aparte permite cambiar la implementacion (por ejemplo
 * migrar de JdbcTemplate a Spring Data JPA) sin tocar el resto de la app.
 */
public interface IEducationRepository {

    /** Crea o actualiza una educacion (decide segun el id sea null o no). */
    Education save(Education education, Long personalInfoId);

    Optional<Education> findById(Long id);

    List<Education> findAll();

    List<Education> findByPersonalInfoId(Long personalInfoId);

    /** Busqueda flexible (case-insensitive, contiene texto) por nombre de institucion. */
    List<Education> findByInstitution(String institution);

    void deleteById(Long id);
}
