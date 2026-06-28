package com.chriswatnee.martinis.dao;

import com.chriswatnee.martinis.dao.support.AbstractBaseDao;
import com.chriswatnee.martinis.dto.Actor;
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
public class ActorDaoImpl extends AbstractBaseDao<Actor> implements ActorDao {

    private static final String CREATE_QUERY = "INSERT INTO actor (first_name, last_name, phone, email) VALUES (?,?,?,?)";
    private static final String READ_QUERY = "SELECT * FROM actor WHERE id = ?";
    private static final String UPDATE_QUERY = "UPDATE actor SET first_name = ?, last_name = ?, phone = ?, email = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM actor WHERE id = ?";
    private static final String LIST_QUERY = "SELECT * FROM actor ORDER BY first_name";

    private final RowMapper<Actor> mapper = new ActorMapper();

    @Inject
    public ActorDaoImpl(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Actor create(Actor actor) {
        jdbcTemplate.update(CREATE_QUERY,
                            actor.getFirstName(),
                            actor.getLastName(),
                            actor.getPhone(),
                            actor.getEmail());
        actor.setId(getLastInsertId());
        return actor;
    }

    @Override
    public Actor read(Integer id) {
        return findById(READ_QUERY, mapper, id);
    }

    @Override
    public void update(Actor actor) {
        jdbcTemplate.update(UPDATE_QUERY,
                            actor.getFirstName(),
                            actor.getLastName(),
                            actor.getPhone(),
                            actor.getEmail(),
                            actor.getId());
    }

    @Override
    public void delete(Actor actor) {
        deleteById(DELETE_QUERY, actor.getId());
    }

    @Override
    public List<Actor> list() {
        return findAll(LIST_QUERY, mapper);
    }

    private static class ActorMapper implements RowMapper<Actor> {

        @Override
        public Actor mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            Actor actor = new Actor();
            actor.setId(resultSet.getInt("id"));
            actor.setFirstName(resultSet.getString("first_name"));
            actor.setLastName(resultSet.getString("last_name"));
            actor.setPhone(resultSet.getString("phone"));
            actor.setEmail(resultSet.getString("email"));
            return actor;
        }
    }
}
