package com.chriswatnee.martinis.dao.support;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public abstract class AbstractBaseDao<T> {

    protected final JdbcTemplate jdbcTemplate;
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected AbstractBaseDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    protected T findById(String query, RowMapper<T> mapper, Integer id) {
        try {
            return jdbcTemplate.queryForObject(query, mapper, id);
        } catch (EmptyResultDataAccessException ex) {
            logger.debug("Entity not found with id: {}", id);
            return null;
        }
    }

    protected int getLastInsertId() {
        return jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
    }

    protected void deleteById(String query, Integer id) {
        jdbcTemplate.update(query, id);
    }

    protected List<T> findAll(String query, RowMapper<T> mapper) {
        return jdbcTemplate.query(query, mapper);
    }

    protected List<T> findAllByParentId(String query, RowMapper<T> mapper, Integer parentId) {
        return jdbcTemplate.query(query, mapper, parentId);
    }
}
