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

/**
 * Repositorio de Skill. Usa JdbcTemplate puro.
 *
 * @Repository: marca la clase como componente Spring de acceso a datos.
 *              Spring lo detecta automaticamente y lo inyecta donde se necesite.
 */
@Repository
@RequiredArgsConstructor
public class SkillRepositoryImpl implements ISkillRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Mapea cada fila de la tabla 'skills' a un objeto Java 'Skill'.
     * Es como un "traductor" SQL -> Java.
     */
    private final RowMapper<Skill> skillRowMapper = (rs, rowNum) -> {
        Skill s = new Skill();
        s.setId(rs.getLong("id"));
        s.setName(rs.getString("name"));
        // getInt() devuelve 0 si la columna es null. Usamos getObject(...,Integer.class)
        // para conservar el null explicitamente.
        s.setLevelPercentage(rs.getObject("level_percentage", Integer.class));
        s.setIconClass(rs.getString("icon_class"));
        return s;
    };

    /**
     * Guarda o actualiza una habilidad vinculada a una persona.
     * Si el id es null -> INSERT. Si tiene valor -> UPDATE.
     */
    @Override
    public Skill save(Skill skill, Long personalInfoId) {
        if (skill.getId() == null) {
            // INSERT: nueva habilidad vinculada a una persona via personal_info_id
            String sql = "INSERT INTO skills (name, level_percentage, icon_class, personal_info_id) VALUES (?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                var ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, skill.getName());
                // setObject permite pasar null sin romper (a diferencia de setInt)
                ps.setObject(2, skill.getLevelPercentage());
                ps.setString(3, skill.getIconClass());
                ps.setLong(4, personalInfoId);
                return ps;
            }, keyHolder);

            if (keyHolder.getKey() != null) {
                skill.setId(keyHolder.getKey().longValue());
            }
        } else {
            // UPDATE: habilidad existente
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
    public List<Skill> findByMinLevel(int minLevel) {
        // Devuelve habilidades cuyo levelPercentage >= minLevel, ordenadas de mayor a menor.
        String sql = "SELECT * FROM skills WHERE level_percentage >= ? ORDER BY level_percentage DESC";
        return jdbcTemplate.query(sql, skillRowMapper, minLevel);
    }

    @Override
    public List<Skill> findTopSkills(int limit) {
        // Top N habilidades por nivel (limit es maximo de filas a devolver).
        // LIMIT funciona en Postgres y en H2 (no en bases mas viejas como Oracle).
        String sql = "SELECT * FROM skills ORDER BY level_percentage DESC LIMIT ?";
        return jdbcTemplate.query(sql, skillRowMapper, limit);
    }

    /**
     * Obtiene el personal_info_id al que pertenece una habilidad.
     * Devuelve Optional vacio si la habilidad no existe.
     * Lo usa el controller en updateSkill para preservar la relacion.
     */
    @Override
    public Optional<Long> findPersonalInfoIdBySkillId(Long skillId) {
        String sql = "SELECT personal_info_id FROM skills WHERE id = ?";
        return jdbcTemplate.query(sql, (rs, n) -> rs.getLong("personal_info_id"), skillId)
                .stream()
                .findFirst();
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM skills WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
