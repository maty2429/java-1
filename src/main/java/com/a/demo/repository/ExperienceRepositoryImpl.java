package com.a.demo.repository;

import com.a.demo.model.Experience;
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
 * Repositorio de Experience. Estructura analoga a EducationRepositoryImpl.
 * Las fechas se manejan como LocalDate en Java <-> DATE en SQL.
 */
@Repository
@RequiredArgsConstructor
public class ExperienceRepositoryImpl implements IExperienceRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Experience> experienceRowMapper = (rs, rowNum) -> {
        Experience ex = new Experience();
        ex.setId(rs.getLong("id"));
        ex.setJobTitle(rs.getString("job_title"));
        ex.setCompanyName(rs.getString("company_name"));
        Date start = rs.getDate("start_date");
        ex.setStartDate(start != null ? start.toLocalDate() : null);
        Date end = rs.getDate("end_date");
        ex.setEndDate(end != null ? end.toLocalDate() : null);
        ex.setDescription(rs.getString("description"));
        return ex;
    };

    @Override
    public Experience save(Experience experience, Long personalInfoId) {
        if (experience.getId() == null) {
            String sql = "INSERT INTO experiences (job_title, company_name, start_date, end_date, description, personal_info_id) VALUES (?, ?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                var ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, experience.getJobTitle());
                ps.setString(2, experience.getCompanyName());
                ps.setDate(3, Date.valueOf(experience.getStartDate()));
                if (experience.getEndDate() != null) {
                    ps.setDate(4, Date.valueOf(experience.getEndDate()));
                } else {
                    ps.setNull(4, Types.DATE);
                }
                ps.setString(5, experience.getDescription());
                ps.setLong(6, personalInfoId);
                return ps;
            }, keyHolder);

            if (keyHolder.getKey() != null) {
                experience.setId(keyHolder.getKey().longValue());
            }
        } else {
            String sql = "UPDATE experiences SET job_title = ?, company_name = ?, start_date = ?, end_date = ?, description = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    experience.getJobTitle(),
                    experience.getCompanyName(),
                    Date.valueOf(experience.getStartDate()),
                    experience.getEndDate() != null ? Date.valueOf(experience.getEndDate()) : null,
                    experience.getDescription(),
                    experience.getId());
        }
        return experience;
    }

    @Override
    public Optional<Experience> findById(Long id) {
        String sql = "SELECT * FROM experiences WHERE id = ?";
        return jdbcTemplate.query(sql, experienceRowMapper, id).stream().findFirst();
    }

    @Override
    public List<Experience> findAll() {
        String sql = "SELECT * FROM experiences";
        return jdbcTemplate.query(sql, experienceRowMapper);
    }

    @Override
    public List<Experience> findByPersonalInfoId(Long personalInfoId) {
        String sql = "SELECT * FROM experiences WHERE personal_info_id = ?";
        return jdbcTemplate.query(sql, experienceRowMapper, personalInfoId);
    }

    @Override
    public List<Experience> findCurrentByPersonalInfoId(Long personalInfoId) {
        // "Experiencias actuales" = aquellas donde no se completo endDate (trabajo en curso).
        String sql = "SELECT * FROM experiences WHERE personal_info_id = ? AND end_date IS NULL";
        return jdbcTemplate.query(sql, experienceRowMapper, personalInfoId);
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM experiences WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
