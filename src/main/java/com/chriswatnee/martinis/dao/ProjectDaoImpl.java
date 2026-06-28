package com.chriswatnee.martinis.dao;

import com.chriswatnee.martinis.dao.support.AbstractBaseDao;
import com.chriswatnee.martinis.dto.Project;
import com.chriswatnee.martinis.dto.Scene;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import jakarta.inject.Inject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ProjectDaoImpl extends AbstractBaseDao<Project> implements ProjectDao {

    private static final String CREATE_QUERY = "INSERT INTO project (title) VALUES (?)";
    private static final String READ_QUERY = "SELECT * FROM project WHERE id = ?";
    private static final String UPDATE_QUERY = "UPDATE project SET title = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM project WHERE id = ?";
    private static final String LIST_QUERY = "SELECT * FROM project ORDER BY title";
    private static final String GET_PROJECT_BY_SCENE_QUERY = "SELECT * FROM project p " +
                                               "INNER JOIN scene s on p.id = s.project_id " +
                                               "WHERE s.id = ?";

    private final RowMapper<Project> mapper = new ProjectMapper();

    @Inject
    public ProjectDaoImpl(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Project create(Project project) {
        jdbcTemplate.update(CREATE_QUERY, project.getTitle());
        project.setId(getLastInsertId());
        return project;
    }

    @Override
    public Project read(Integer id) {
        return findById(READ_QUERY, mapper, id);
    }

    @Override
    public void update(Project project) {
        jdbcTemplate.update(UPDATE_QUERY, project.getTitle(), project.getId());
    }

    @Override
    public void delete(Project project) {
        deleteById(DELETE_QUERY, project.getId());
    }

    @Override
    public List<Project> list() {
        return findAll(LIST_QUERY, mapper);
    }

    @Override
    public Project getProjectByScene(Scene scene) {
        return findById(GET_PROJECT_BY_SCENE_QUERY, mapper, scene.getId());
    }

    private static class ProjectMapper implements RowMapper<Project> {

        @Override
        public Project mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            Project project = new Project();
            project.setId(resultSet.getInt("id"));
            project.setTitle(resultSet.getString("title"));
            return project;
        }
    }
}
