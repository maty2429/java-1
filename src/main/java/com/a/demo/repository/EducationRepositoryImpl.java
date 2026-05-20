package com.a.demo.repository;

import com.a.demo.model.Education;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio de Education. Usa JdbcTemplate puro (sin Spring Data JPA).
 *
 * Detalle clave sobre fechas:
 * - En el modelo usamos java.time.LocalDate (Java moderno).
 * - JDBC clasico usa java.sql.Date. Convertimos con Date.valueOf(localDate) al escribir
 *   y rs.getDate(...).toLocalDate() al leer.
 * - Si endDate es null, lo guardamos como NULL en la BD usando setNull con Types.DATE.
 */
@Repository
@RequiredArgsConstructor
public class EducationRepositoryImpl implements IEducationRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Education> educationRowMapper = (rs, rowNum) -> {
        Education e = new Education();
        e.setId(rs.getLong("id"));
        e.setDegree(rs.getString("degree"));
        e.setInstitution(rs.getString("institution"));
        // rs.getDate() puede devolver null. Por eso chequeamos antes de convertir.
        Date start = rs.getDate("start_date");
        e.setStartDate(start != null ? start.toLocalDate() : null);
        Date end = rs.getDate("end_date");
        e.setEndDate(end != null ? end.toLocalDate() : null);
        e.setDescription(rs.getString("description"));
        return e;
    };

    @Override
    public Education save(Education education, Long personalInfoId) {
        if (education.getId() == null) {
            // INSERT: nuevo registro. La BD genera el id automaticamente.
            String sql = "INSERT INTO educations (degree, institution, start_date, end_date, description, personal_info_id) VALUES (?, ?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                var ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, education.getDegree());
                ps.setString(2, education.getInstitution());
                ps.setDate(3, Date.valueOf(education.getStartDate()));
                // Manejo seguro de null en endDate
                if (education.getEndDate() != null) {
                    ps.setDate(4, Date.valueOf(education.getEndDate()));
                } else {
                    ps.setNull(4, Types.DATE);
                }
                ps.setString(5, education.getDescription());
                ps.setLong(6, personalInfoId);
                return ps;
            }, keyHolder);

            if (keyHolder.getKey() != null) {
                education.setId(keyHolder.getKey().longValue());
            }
        } else {
            // UPDATE: registro existente, lo identificamos por su id.
            String sql = "UPDATE educations SET degree = ?, institution = ?, start_date = ?, end_date = ?, description = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    education.getDegree(),
                    education.getInstitution(),
                    Date.valueOf(education.getStartDate()),
                    education.getEndDate() != null ? Date.valueOf(education.getEndDate()) : null,
                    education.getDescription(),
                    education.getId());
        }
        return education;
    }

    @Override
    public Optional<Education> findById(Long id) {
        String sql = "SELECT * FROM educations WHERE id = ?";
        return jdbcTemplate.query(sql, educationRowMapper, id).stream().findFirst();
    }

    @Override
    public List<Education> findAll() {
        String sql = "SELECT * FROM educations";
        return jdbcTemplate.query(sql, educationRowMapper);
    }

    @Override
    public List<Education> findByPersonalInfoId(Long personalInfoId) {
        String sql = "SELECT * FROM educations WHERE personal_info_id = ?";
        return jdbcTemplate.query(sql, educationRowMapper, personalInfoId);
    }

    @Override
    public List<Education> findByInstitution(String institution) {
        // ILIKE no existe en H2; usamos LOWER(...) LIKE para que sea case-insensitive en ambas BDs.
        String sql = "SELECT * FROM educations WHERE LOWER(institution) LIKE LOWER(?)";
        return jdbcTemplate.query(sql, educationRowMapper, "%" + institution + "%");
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM educations WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
