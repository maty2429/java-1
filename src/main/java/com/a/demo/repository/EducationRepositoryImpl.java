package com.a.demo.repository;

import com.a.demo.model.Education;
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
public class EducationRepositoryImpl implements IEducationRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Education> educationRowMapper = (rs, rowNum) -> {
        Education e = new Education();
        e.setId(rs.getLong("id"));
        e.setDegree(rs.getString("degree"));
        e.setInstitution(rs.getString("institution"));
        e.setStartDate(rs.getString("start_date"));
        e.setEndDate(rs.getString("end_date"));
        e.setDescription(rs.getString("description"));
        return e;
    };

    @Override
    public Education save(Education education, Long personalInfoId) {
        if (education.getId() == null) {
            String sql = "INSERT INTO educations (degree, institution, start_date, end_date, description, personal_info_id) VALUES (?, ?, CAST(? AS DATE), CAST(? AS DATE), ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                var ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, education.getDegree());
                ps.setString(2, education.getInstitution());
                ps.setString(3, education.getStartDate());
                ps.setString(4, education.getEndDate());
                ps.setString(5, education.getDescription());
                ps.setLong(6, personalInfoId);
                return ps;
            }, keyHolder);

            if (keyHolder.getKey() != null) {
                education.setId(keyHolder.getKey().longValue());
            }
        } else {
            String sql = "UPDATE educations SET degree = ?, institution = ?, start_date = CAST(? AS DATE), end_date = CAST(? AS DATE), description = ? WHERE id = ?";
            jdbcTemplate.update(sql, education.getDegree(), education.getInstitution(), education.getStartDate(), education.getEndDate(), education.getDescription(), education.getId());
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
    public void deleteById(Long id) {
        String sql = "DELETE FROM educations WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
