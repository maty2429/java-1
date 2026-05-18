package com.a.demo.repository;

import com.a.demo.model.PersonalInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @Repository: Le dice a Spring que esta clase es un componente de acceso a datos.
 * Maneja cómo se guardan y recuperan los datos de la base de datos.
 */
@Repository
/**
 * @RequiredArgsConstructor: Genera automáticamente el constructor para inyectar 
 * dependencias (como JdbcTemplate) que sean 'final'.
 */
@RequiredArgsConstructor
public class PersonalInfoRepositoryImpl implements IPersonalInfoRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * RowMapper: Es el "traductor" que convierte cada fila de la base de datos
     * (ResultSet) en un objeto Java (PersonalInfo).
     */
    private final RowMapper<PersonalInfo> personalInfoRowMapper = (rs, rowNum) -> {
        PersonalInfo p = new PersonalInfo();
        p.setId(rs.getLong("id"));
        p.setFirstName(rs.getString("first_name"));
        p.setLastName(rs.getString("last_name"));
        p.setTitle(rs.getString("title"));
        p.setProfileDescription(rs.getString("profile_description"));
        p.setProfileImageUrl(rs.getString("profile_image_url"));
        p.setYearsOfExperience(rs.getInt("years_of_experience"));
        p.setEmail(rs.getString("email"));
        p.setPhone(rs.getString("phone"));
        p.setLinkedinUrl(rs.getString("linkedin_url"));
        p.setGithubUrl(rs.getString("github_url"));
        return p;
    };

    /**
     * @Override: Indica que estamos cumpliendo con una tarea definida en la interfaz.
     * Este método decide si INSERTAR (si el ID es nuevo) o ACTUALIZAR (si ya tiene ID).
     */
    @Override
    public PersonalInfo save(PersonalInfo personalInfo) {
        if (personalInfo.getId() == null) {
            // OPERACIÓN: INSERTAR nuevo registro
            String sql = "INSERT INTO personal_info (first_name, last_name, title, profile_description, profile_image_url, years_of_experience, email, phone, linkedin_url, github_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            // KeyHolder: Se usa para capturar el ID que la base de datos genera automáticamente.
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                var ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, personalInfo.getFirstName());
                ps.setString(2, personalInfo.getLastName());
                ps.setString(3, personalInfo.getTitle());
                ps.setString(4, personalInfo.getProfileDescription());
                ps.setString(5, personalInfo.getProfileImageUrl());
                ps.setInt(6, personalInfo.getYearsOfExperience());
                ps.setString(7, personalInfo.getEmail());
                ps.setString(8, personalInfo.getPhone());
                ps.setString(9, personalInfo.getLinkedinUrl());
                ps.setString(10, personalInfo.getGithubUrl());
                return ps;
            }, keyHolder);

            // Recuperamos el ID generado y lo asignamos al objeto.
            if (keyHolder.getKey() != null) {
                personalInfo.setId(keyHolder.getKey().longValue());
            }
        } else {
            // OPERACIÓN: ACTUALIZAR registro existente
            String sql = "UPDATE personal_info SET first_name = ?, last_name = ?, title = ?, profile_description = ?, profile_image_url = ?, years_of_experience = ?, email = ?, phone = ?, linkedin_url = ?, github_url = ? WHERE id = ?";

            jdbcTemplate.update(sql,
                    personalInfo.getFirstName(), personalInfo.getLastName(), personalInfo.getTitle(),
                    personalInfo.getProfileDescription(), personalInfo.getProfileImageUrl(),
                    personalInfo.getYearsOfExperience(), personalInfo.getEmail(), personalInfo.getPhone(),
                    personalInfo.getLinkedinUrl(), personalInfo.getGithubUrl(), personalInfo.getId());
        }
        return personalInfo;
    }

    @Override
    public Optional<PersonalInfo> findById(Long id) {
        String sql = "SELECT * FROM personal_info WHERE id = ?";
        /**
         * ¿Por qué no usamos queryForObject? Porque queryForObject lanza un error (excepción) 
         * si el ID no existe. 
         * 
         * En cambio, .query() devuelve una lista vacía si no hay resultados, y 
         * .stream().findFirst() la convierte en un Optional vacío de forma segura sin romper el programa.
         */
        return jdbcTemplate.query(sql, personalInfoRowMapper, id)
                .stream()
                .findFirst();
    }

    @Override
    public List<PersonalInfo> findAll() {
        String sql = "SELECT * FROM personal_info";
        // Devuelve una lista de todos los registros mapeados por personalInfoRowMapper.
        return jdbcTemplate.query(sql, personalInfoRowMapper);
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM personal_info WHERE id = ?";
        // update se usa para ejecutar sentencias que modifican datos (INSERT, UPDATE, DELETE).
        jdbcTemplate.update(sql, id);
    }
}
