package com.a.demo.repository;

import com.a.demo.model.Skill;
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
public class SkillRepositoryImpl implements ISkillRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Mapea cada fila de la tabla 'skills' a un objeto Java 'Skill'.
     */
    private final RowMapper<Skill> skillRowMapper = (rs, rowNum) -> {
        Skill s = new Skill();
        s.setId(rs.getLong("id"));
        s.setName(rs.getString("name"));
        s.setLevelPercentage(rs.getInt("level_percentage"));
        s.setIconClass(rs.getString("icon_class"));
        return s;
    };

    /**
     * Guarda o actualiza una habilidad vinculada a una persona.
     */
    @Override
    public Skill save(Skill skill, Long personalInfoId) {
        if (skill.getId() == null) {
            // OPERACIÓN: Crear nueva habilidad vinculada a una persona
            String sql = "INSERT INTO skills (name, level_percentage, icon_class, personal_info_id) VALUES (?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                var ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, skill.getName());
                ps.setInt(2, skill.getLevelPercentage());
                ps.setString(3, skill.getIconClass());
                ps.setLong(4, personalInfoId);
                return ps;
            }, keyHolder);

            if (keyHolder.getKey() != null) {
                skill.setId(keyHolder.getKey().longValue());
            }
        } else {
            // OPERACIÓN: Actualizar habilidad existente
            String sql = "UPDATE skills SET name = ?, level_percentage = ?, icon_class = ? WHERE id = ?";
            jdbcTemplate.update(sql, skill.getName(), skill.getLevelPercentage(), skill.getIconClass(), skill.getId());
        }
        return skill;
    }

    @Override
    public Optional<Skill> findById(Long id) {
        String sql = "SELECT * FROM skills WHERE id = ?";
        return jdbcTemplate.query(sql, skillRowMapper, id).stream().findFirst();
    }

    @Override
    public List<Skill> findAll() {
        String sql = "SELECT * FROM skills";
        return jdbcTemplate.query(sql, skillRowMapper);
    }

    @Override
    public List<Skill> findByPersonalInfoId(Long personalInfoId) {
        String sql = "SELECT * FROM skills WHERE personal_info_id = ?";
        return jdbcTemplate.query(sql, skillRowMapper, personalInfoId);
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM skills WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
