package com.chriswatnee.martinis.dao;

import com.chriswatnee.martinis.dao.support.AbstractBaseDao;
import com.chriswatnee.martinis.dto.Actor;
import com.chriswatnee.martinis.dto.Person;
import com.chriswatnee.martinis.dto.Project;
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
public class PersonDaoImpl extends AbstractBaseDao<Person> implements PersonDao {

    private static final String CREATE_QUERY = "INSERT INTO person (`name`, full_name, actor_id, project_id) VALUES (?,?,?,?)";
    private static final String READ_QUERY = "SELECT * FROM person WHERE id = ?";
    private static final String UPDATE_QUERY = "UPDATE person SET `name` = ?, full_name = ?, actor_id = ?, project_id = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM person WHERE id = ?";
    private static final String LIST_QUERY = "SELECT * FROM person";
    private static final String GET_PERSONS_BY_PROJECT_QUERY = "SELECT * FROM person where project_id = ? ORDER BY `name`";

    private final RowMapper<Person> mapper = new PersonMapper();

    @Inject
    public PersonDaoImpl(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Person create(Person person) {
        Integer actorId = person.getActor() != null ? person.getActor().getId() : null;
        Integer projectId = person.getProject() != null ? person.getProject().getId() : null;

        jdbcTemplate.update(CREATE_QUERY,
                            person.getName(),
                            person.getFullName(),
                            actorId,
                            projectId);
        person.setId(getLastInsertId());
        return person;
    }

    @Override
    public Person read(Integer id) {
        return findById(READ_QUERY, mapper, id);
    }

    @Override
    public void update(Person person) {
        Integer actorId = person.getActor() != null ? person.getActor().getId() : null;
        Integer projectId = person.getProject() != null ? person.getProject().getId() : null;

        jdbcTemplate.update(UPDATE_QUERY,
                            person.getName(),
                            person.getFullName(),
                            actorId,
                            projectId,
                            person.getId());
    }

    @Override
    public void delete(Person person) {
        deleteById(DELETE_QUERY, person.getId());
    }

    @Override
    public List<Person> list() {
        return findAll(LIST_QUERY, mapper);
    }

    @Override
    public List<Person> getPersonsByProject(Project project) {
        return findAllByParentId(GET_PERSONS_BY_PROJECT_QUERY, mapper, project.getId());
    }

    private static class PersonMapper implements RowMapper<Person> {

        @Override
        public Person mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            Person person = new Person();
            person.setId(resultSet.getInt("id"));
            person.setName(resultSet.getString("name"));
            person.setFullName(resultSet.getString("full_name"));

            Integer actorId = resultSet.getInt("actor_id");
            if (actorId != null) {
                Actor actor = new Actor();
                actor.setId(actorId);
                person.setActor(actor);
            }

            Integer projectId = resultSet.getInt("project_id");
            if (projectId != null) {
                Project project = new Project();
                project.setId(projectId);
                person.setProject(project);
            }

            return person;
        }
    }
}
