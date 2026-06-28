package com.chriswatnee.martinis.dao;

import com.chriswatnee.martinis.dao.support.AbstractBaseDao;
import com.chriswatnee.martinis.dao.support.OrderedEntityDaoHelper;
import com.chriswatnee.martinis.dto.Block;
import com.chriswatnee.martinis.dto.Person;
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
public class BlockDaoImpl extends AbstractBaseDao<Block> implements BlockDao {

    private static final String CREATE_QUERY = "INSERT INTO block (`order`, content, person_id, scene_id) VALUES (?,?,?,?)";
    private static final String READ_QUERY = "SELECT * FROM block WHERE id = ?";
    private static final String UPDATE_QUERY = "UPDATE block SET `order` = ?, content = ?, person_id = ?, scene_id = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM block WHERE id = ?";
    private static final String LIST_QUERY = "SELECT * FROM block";
    private static final String GET_BLOCK_BY_ORDER_QUERY = "SELECT * FROM block WHERE `order` = ? AND scene_id = ?";
    private static final String UPDATE_ORDER_QUERY = "UPDATE block SET `order` = ? WHERE id = ?";
    private static final String TOGGLE_BOOKMARK_QUERY = "UPDATE block SET is_bookmarked = ? WHERE id = ?";
    private static final String ADD_ORDERS_QUERY = "UPDATE block SET `order` = `order` + 1 WHERE `order` > ? AND scene_id = ?";
    private static final String SUBTRACT_ORDERS_QUERY = "UPDATE block SET `order` = `order` - 1 WHERE `order` > ? AND scene_id = ?";
    private static final String GET_BLOCKS_BY_SCENE_QUERY = "SELECT * FROM block WHERE scene_id = ? ORDER BY `order`";
    private static final String GET_BLOCK_COUNT_BY_SCENE_QUERY = "SELECT COUNT(*) FROM block WHERE scene_id = ?";

    private final RowMapper<Block> mapper = new BlockMapper();

    @Inject
    public BlockDaoImpl(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Block create(Block block) {
        Integer personId = block.getPerson() != null ? block.getPerson().getId() : null;
        Integer sceneId = block.getScene() != null ? block.getScene().getId() : null;
        int order = OrderedEntityDaoHelper.getNextOrder(jdbcTemplate, GET_BLOCK_COUNT_BY_SCENE_QUERY, sceneId);

        jdbcTemplate.update(CREATE_QUERY, order, block.getContent(), personId, sceneId);
        block.setId(getLastInsertId());
        return block;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Block createBelow(Block block) {
        Integer personId = block.getPerson() != null ? block.getPerson().getId() : null;
        Integer order = block.getOrder() != null ? block.getOrder() + 1 : null;
        Integer sceneId = block.getScene() != null ? block.getScene().getId() : null;

        OrderedEntityDaoHelper.shiftOrdersUp(jdbcTemplate, ADD_ORDERS_QUERY, block.getOrder(), sceneId);
        jdbcTemplate.update(CREATE_QUERY, order, block.getContent(), personId, sceneId);
        block.setId(getLastInsertId());
        return block;
    }

    @Override
    public Block read(Integer id) {
        return findById(READ_QUERY, mapper, id);
    }

    @Override
    public void update(Block block) {
        Integer personId = block.getPerson() != null ? block.getPerson().getId() : null;
        Integer sceneId = block.getScene() != null ? block.getScene().getId() : null;

        jdbcTemplate.update(UPDATE_QUERY, block.getOrder(), block.getContent(), personId, sceneId, block.getId());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Block block) {
        deleteById(DELETE_QUERY, block.getId());
        OrderedEntityDaoHelper.shiftOrdersDown(jdbcTemplate, SUBTRACT_ORDERS_QUERY, block.getOrder(), block.getScene().getId());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void restore(Block block) {
        Integer personId = block.getPerson() != null ? block.getPerson().getId() : null;
        Integer sceneId = block.getScene() != null ? block.getScene().getId() : null;

        OrderedEntityDaoHelper.shiftOrdersUp(jdbcTemplate, ADD_ORDERS_QUERY, block.getOrder() - 1, sceneId);
        jdbcTemplate.update(CREATE_QUERY, block.getOrder(), block.getContent(), personId, sceneId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void moveUp(Block block) {
        Block blockAbove = readByOrder(block.getOrder() - 1, block.getScene().getId());
        if (blockAbove != null) {
            OrderedEntityDaoHelper.swapOrders(jdbcTemplate, UPDATE_ORDER_QUERY,
                    block.getId(), block.getOrder(), blockAbove.getId(), blockAbove.getOrder());
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void moveDown(Block block) {
        Block blockBelow = readByOrder(block.getOrder() + 1, block.getScene().getId());
        if (blockBelow != null) {
            OrderedEntityDaoHelper.swapOrders(jdbcTemplate, UPDATE_ORDER_QUERY,
                    block.getId(), block.getOrder(), blockBelow.getId(), blockBelow.getOrder());
        }
    }

    @Override
    public void updateOrder(Integer blockId, Integer newOrder) {
        jdbcTemplate.update(UPDATE_ORDER_QUERY, newOrder, blockId);
    }

    @Override
    public void toggleBookmark(Integer id, boolean newBookmarked) {
        jdbcTemplate.update(TOGGLE_BOOKMARK_QUERY, newBookmarked, id);
    }

    @Override
    public List<Block> list() {
        return findAll(LIST_QUERY, mapper);
    }

    @Override
    public List<Block> getBlocksByScene(Scene scene) {
        return findAllByParentId(GET_BLOCKS_BY_SCENE_QUERY, mapper, scene.getId());
    }

    private Block readByOrder(Integer order, Integer sceneId) {
        try {
            return jdbcTemplate.queryForObject(GET_BLOCK_BY_ORDER_QUERY, mapper, order, sceneId);
        } catch (EmptyResultDataAccessException ex) {
            logger.debug("Block not found with order: {} in scene: {}", order, sceneId);
            return null;
        }
    }

    private static class BlockMapper implements RowMapper<Block> {

        @Override
        public Block mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            Block block = new Block();
            block.setId(resultSet.getInt("id"));
            block.setOrder(resultSet.getInt("order"));
            block.setContent(resultSet.getString("content"));
            block.setBookmarked(resultSet.getBoolean("is_bookmarked"));

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
}
