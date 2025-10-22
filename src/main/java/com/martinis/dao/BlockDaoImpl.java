/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.martinis.dao;

import com.martinis.dto.Block;
import com.martinis.dto.Person;
import com.martinis.dto.Scene;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.inject.Inject;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author chris
 */
public class BlockDaoImpl implements BlockDao {
    
    JdbcTemplate jdbcTemplate;
    
    static String CREATE_QUERY = "INSERT INTO block (`order`, content, person_id, scene_id, is_bookmarked) VALUES (?,?,?,?,?)";
    static String READ_QUERY = "SELECT * FROM block WHERE id = ?";
    static String UPDATE_QUERY = "UPDATE block SET `order` = ?, content = ?, person_id = ?, scene_id = ?, is_bookmarked = ? WHERE id = ?";
    static String DELETE_QUERY = "DELETE FROM block WHERE id = ?";
    static String LIST_QUERY = "SELECT * FROM block";
    static String GET_BLOCK_BY_ORDER_QUERY = "SELECT * FROM block WHERE `order` = ? AND scene_id = ?";
    static String GET_ORDER_QUERY = "SELECT `order` FROM block WHERE id = ?";
    static String UPDATE_ORDER_QUERY = "UPDATE block SET `order` = ? WHERE id = ?";
    static String TOGGLE_BOOKMARK_QUERY = "UPDATE block SET is_bookmarked = NOT is_bookmarked WHERE id = ?";
    static String ADD_ORDERS_QUERY = "UPDATE block SET `order` = `order` + 1 WHERE `order` > ? AND scene_id = ?";
    static String SUBTRACT_ORDERS_QUERY = "UPDATE block SET `order` = `order` - 1 WHERE `order` > ? AND scene_id = ?";
    static String GET_BLOCKS_BY_SCENE_QUERY = "SELECT * FROM block WHERE scene_id = ? ORDER BY `order`";
    static String GET_BLOCK_COUNT_BY_SCENE_QUERY = "SELECT COUNT(*) FROM block WHERE scene_id = ?";
    
    @Inject
    public BlockDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Block create(Block block) {
        
        Integer personId = null;
        
        if (block.getPerson() != null) {
            personId = block.getPerson().getId();
        }
        
        Integer sceneId = null;
        
        if (block.getScene() != null) {
            sceneId = block.getScene().getId();
        }
        
        Integer order = jdbcTemplate.queryForObject(GET_BLOCK_COUNT_BY_SCENE_QUERY, Integer.class, sceneId) + 1;

        Boolean isBookmarked = block.getIsBookmarked() != null ? block.getIsBookmarked() : false;

        jdbcTemplate.update(CREATE_QUERY,
                            order,
                            block.getContent(),
                            personId,
                            sceneId,
                            isBookmarked
        );

        int createdId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);

        block.setId(createdId);

        return block;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Block createBelow(Block block) {
        
        Integer personId = null;
        
        if (block.getPerson() != null) {
            personId = block.getPerson().getId();
        }
        
        Integer order = null;
        
        if (block.getOrder() != null) {
            order = block.getOrder() + 1;
        }
        
        Integer sceneId = null;
        
        if (block.getScene() != null) {
            sceneId = block.getScene().getId();
        }
        
        jdbcTemplate.update(ADD_ORDERS_QUERY,
                            block.getOrder(),
                            sceneId
        );
        
        Boolean isBookmarked = block.getIsBookmarked() != null ? block.getIsBookmarked() : false;

        jdbcTemplate.update(CREATE_QUERY,
                            order,
                            block.getContent(),
                            personId,
                            sceneId,
                            isBookmarked
        );

        int createdId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);

        block.setId(createdId);

        return block;
    }

    @Override
    public Block read(Integer id) {
        
        try {
            Block block = jdbcTemplate.queryForObject(READ_QUERY, new BlockMapper(), id);
            return block;
        } catch (EmptyResultDataAccessException ex) {}
        
        return null;
        
    }

    @Override
    public void update(Block block) {
        
        Integer personId = null;
        
        if (block.getPerson() != null) {
            personId = block.getPerson().getId();
        }
        
        Integer sceneId = null;
        
        if (block.getScene() != null) {
            sceneId = block.getScene().getId();
        }
        
        Boolean isBookmarked = block.getIsBookmarked() != null ? block.getIsBookmarked() : false;

        jdbcTemplate.update(UPDATE_QUERY,
                            block.getOrder(),
                            block.getContent(),
                            personId,
                            sceneId,
                            isBookmarked,
                            block.getId()
        );
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Block block) {

        jdbcTemplate.update(DELETE_QUERY, block.getId());

        jdbcTemplate.update(SUBTRACT_ORDERS_QUERY,
                            block.getOrder(),
                            block.getScene().getId()
        );
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void restore(Block block) {

        Integer personId = null;

        if (block.getPerson() != null) {
            personId = block.getPerson().getId();
        }

        Integer sceneId = null;

        if (block.getScene() != null) {
            sceneId = block.getScene().getId();
        }

        // Make space for the restored block by adding 1 to all blocks with order >= restored block's order
        jdbcTemplate.update(ADD_ORDERS_QUERY,
                            block.getOrder() - 1,
                            sceneId
        );

        Boolean isBookmarked = block.getIsBookmarked() != null ? block.getIsBookmarked() : false;

        // Insert the block at its original order position
        jdbcTemplate.update(CREATE_QUERY,
                            block.getOrder(),
                            block.getContent(),
                            personId,
                            sceneId,
                            isBookmarked
        );
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void moveUp(Block block) {
        
        Block blockAbove = readByOrder(block.getOrder() - 1, block.getScene().getId());
        
        if (blockAbove != null) {
            jdbcTemplate.update(UPDATE_ORDER_QUERY,
                                blockAbove.getOrder(),
                                block.getId()
            );

            jdbcTemplate.update(UPDATE_ORDER_QUERY,
                                block.getOrder(),
                                blockAbove.getId()
            );
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void moveDown(Block block) {
        
        Block blockBelow = readByOrder(block.getOrder() + 1, block.getScene().getId());
        
        if (blockBelow != null) {
            jdbcTemplate.update(UPDATE_ORDER_QUERY,
                                blockBelow.getOrder(),
                                block.getId()
            );

            jdbcTemplate.update(UPDATE_ORDER_QUERY,
                                block.getOrder(),
                                blockBelow.getId()
            );
        }
    }

    @Override
    public void updateOrder(Integer blockId, Integer newOrder) {
        jdbcTemplate.update(UPDATE_ORDER_QUERY, newOrder, blockId);
    }

    @Override
    public void toggleBookmark(Integer blockId) {
        jdbcTemplate.update(TOGGLE_BOOKMARK_QUERY, blockId);
    }

    @Override
    public List<Block> list() {
        return jdbcTemplate.query(LIST_QUERY, new BlockMapper());
    }

    @Override
    public List<Block> getBlocksByScene(Scene scene) {
        return jdbcTemplate.query(GET_BLOCKS_BY_SCENE_QUERY,
                                  new BlockMapper(),
                                  scene.getId()
        );
    }
    
    private class BlockMapper implements RowMapper<Block> {
        
        @Override
        public Block mapRow(ResultSet resultSet, int i) throws SQLException {
            
            Block block = new Block();
            
            block.setId(resultSet.getInt("id"));
            block.setOrder(resultSet.getInt("order"));
            block.setContent(resultSet.getString("content"));
            block.setIsBookmarked(resultSet.getBoolean("is_bookmarked"));

            Integer personId = resultSet.getInt("person_id");
            
            if (personId != null) {
                Person person = new Person();
                person.setId(personId);
                
                block.setPerson(person);
            }
            
            Integer sceneId = resultSet.getInt("scene_id");
            
            if (sceneId != null) {
                Scene scene = new Scene();
                scene.setId(sceneId);
                
                block.setScene(scene);
            }
            
            return block;
        }
    }
    
    private Block readByOrder(Integer order, Integer sceneId) {
        
        try {
            Block block = jdbcTemplate.queryForObject(GET_BLOCK_BY_ORDER_QUERY,
                                                      new BlockMapper(),
                                                      order,
                                                      sceneId);
            return block;
        } catch (EmptyResultDataAccessException ex) {}
        
        return null;
    }
    
}
