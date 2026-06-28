package com.chriswatnee.martinis.dao;

import com.chriswatnee.martinis.dao.support.AbstractBaseDao;
import com.chriswatnee.martinis.dao.support.OrderedEntityDaoHelper;
import com.chriswatnee.martinis.dto.Project;
import com.chriswatnee.martinis.dto.Scene;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import jakarta.inject.Inject;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class SceneDaoImpl extends AbstractBaseDao<Scene> implements SceneDao {

    private static final String CREATE_QUERY = "INSERT INTO scene (`order`, `name`, project_id) VALUES (?,?,?)";
    private static final String READ_QUERY = "SELECT * FROM scene WHERE id = ?";
    private static final String UPDATE_QUERY = "UPDATE scene SET `order` = ?, `name` = ?, project_id = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM scene WHERE id = ?";
    private static final String LIST_QUERY = "SELECT * FROM scene";
    private static final String GET_SCENE_BY_ORDER_QUERY = "SELECT * FROM scene WHERE `order` = ? AND project_id = ?";
    private static final String UPDATE_ORDER_QUERY = "UPDATE scene SET `order` = ? WHERE id = ?";
    private static final String ADD_ORDERS_QUERY = "UPDATE scene SET `order` = `order` + 1 WHERE `order` > ? AND project_id = ?";
    private static final String SUBTRACT_ORDERS_QUERY = "UPDATE scene SET `order` = `order` - 1 WHERE `order` > ? AND project_id = ?";
    private static final String GET_SCENES_BY_PROJECT_QUERY = "SELECT * FROM scene WHERE project_id = ? ORDER BY `order`";
    private static final String GET_SCENE_COUNT_BY_PROJECT_QUERY = "SELECT COUNT(*) FROM scene WHERE project_id = ?";

    private final RowMapper<Scene> mapper = new SceneMapper();

    @Inject
    public SceneDaoImpl(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Scene create(Scene scene) {
        Integer projectId = scene.getProject() != null ? scene.getProject().getId() : null;
        int order = OrderedEntityDaoHelper.getNextOrder(jdbcTemplate, GET_SCENE_COUNT_BY_PROJECT_QUERY, projectId);

        jdbcTemplate.update(CREATE_QUERY, order, scene.getName(), projectId);
        scene.setId(getLastInsertId());
        return scene;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Scene createBelow(Scene scene) {
        Integer projectId = scene.getProject() != null ? scene.getProject().getId() : null;
        Integer order = scene.getOrder() != null ? scene.getOrder() + 1 : null;

        OrderedEntityDaoHelper.shiftOrdersUp(jdbcTemplate, ADD_ORDERS_QUERY, scene.getOrder(), projectId);
        jdbcTemplate.update(CREATE_QUERY, order, scene.getName(), projectId);
        scene.setId(getLastInsertId());
        return scene;
    }

    @Override
    public Scene read(Integer id) {
        return findById(READ_QUERY, mapper, id);
    }

    @Override
    public void update(Scene scene) {
        Integer projectId = scene.getProject() != null ? scene.getProject().getId() : null;
        jdbcTemplate.update(UPDATE_QUERY, scene.getOrder(), scene.getName(), projectId, scene.getId());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Scene scene) {
        deleteById(DELETE_QUERY, scene.getId());
        OrderedEntityDaoHelper.shiftOrdersDown(jdbcTemplate, SUBTRACT_ORDERS_QUERY, scene.getOrder(), scene.getProject().getId());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void moveUp(Scene scene) {
        Scene previousScene = getSceneByProjectAndOrder(scene.getProject(), scene.getOrder() - 1);
        if (previousScene != null) {
            OrderedEntityDaoHelper.swapOrders(jdbcTemplate, UPDATE_ORDER_QUERY,
                    scene.getId(), scene.getOrder(), previousScene.getId(), previousScene.getOrder());
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void moveDown(Scene scene) {
        Scene nextScene = getSceneByProjectAndOrder(scene.getProject(), scene.getOrder() + 1);
        if (nextScene != null) {
            OrderedEntityDaoHelper.swapOrders(jdbcTemplate, UPDATE_ORDER_QUERY,
                    scene.getId(), scene.getOrder(), nextScene.getId(), nextScene.getOrder());
        }
    }

    @Override
    public List<Scene> list() {
        return findAll(LIST_QUERY, mapper);
    }

    @Override
    public Scene getPreviousScene(Scene scene) {
        return getSceneByProjectAndOrder(scene.getProject(), scene.getOrder() - 1);
    }

    @Override
    public Scene getNextScene(Scene scene) {
        return getSceneByProjectAndOrder(scene.getProject(), scene.getOrder() + 1);
    }

    @Override
    public List<Scene> getScenesByProject(Project project) {
        return findAllByParentId(GET_SCENES_BY_PROJECT_QUERY, mapper, project.getId());
    }

    private Scene getSceneByProjectAndOrder(Project project, Integer order) {
        try {
            return jdbcTemplate.queryForObject(GET_SCENE_BY_ORDER_QUERY, mapper, order, project.getId());
        } catch (EmptyResultDataAccessException ex) {
            logger.debug("Scene not found with order: {} in project: {}", order, project.getId());
            return null;
        }
    }

    private static class SceneMapper implements RowMapper<Scene> {

        @Override
        public Scene mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            Scene scene = new Scene();
            scene.setId(resultSet.getInt("id"));
            scene.setOrder(resultSet.getInt("order"));
            scene.setName(resultSet.getString("name"));

            Integer projectId = resultSet.getInt("project_id");
            if (projectId != null) {
                Project project = new Project();
                project.setId(projectId);
                scene.setProject(project);
            }

            return scene;
        }
    }
}
