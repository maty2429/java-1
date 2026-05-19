package com.a.demo.repository;

import com.a.demo.model.Experience;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ExperienceRepositoryImpl implements IExperienceRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Experience> experienceRowMapper = (rs, rowNum) -> {
        Experience ex = new Experience();
        ex.setId(rs.getLong("id"));
        ex.setJobTitle(rs.getString("job_title"));
        ex.setCompanyName(rs.getString("company_name"));
        ex.setStartDate(rs.getString("start_date"));
        ex.setEndDate(rs.getString("end_date"));
        ex.setDescription(rs.getString("description"));
        return ex;
    };

    @Override
    public Experience save(Experience experience, Long personalInfoId) {
        if (experience.getId() == null) {
            String sql = "INSERT INTO experiences (job_title, company_name, start_date, end_date, description, personal_info_id) VALUES (?, ?, CAST(? AS DATE), CAST(? AS DATE), ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                var ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, experience.getJobTitle());
                ps.setString(2, experience.getCompanyName());
                ps.setString(3, experience.getStartDate());
                ps.setString(4, experience.getEndDate());
                ps.setString(5, experience.getDescription());
                ps.setLong(6, personalInfoId);
                return ps;
            }, keyHolder);

            if (keyHolder.getKey() != null) {
                experience.setId(keyHolder.getKey().longValue());
            }
        } else {
            String sql = "UPDATE experiences SET job_title = ?, company_name = ?, start_date = CAST(? AS DATE), end_date = CAST(? AS DATE), description = ? WHERE id = ?";
            jdbcTemplate.update(sql, experience.getJobTitle(), experience.getCompanyName(), experience.getStartDate(), experience.getEndDate(), experience.getDescription(), experience.getId());
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
    public void deleteById(Long id) {
        String sql = "DELETE FROM experiences WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
