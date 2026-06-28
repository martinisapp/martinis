package com.chriswatnee.martinis.dao.support;

import org.springframework.jdbc.core.JdbcTemplate;

public final class OrderedEntityDaoHelper {

    private OrderedEntityDaoHelper() {
    }

    public static void swapOrders(JdbcTemplate jdbcTemplate, String updateOrderQuery,
                                  Integer id1, Integer order1, Integer id2, Integer order2) {
        jdbcTemplate.update(updateOrderQuery, order1, id2);
        jdbcTemplate.update(updateOrderQuery, order2, id1);
    }

    public static void shiftOrdersUp(JdbcTemplate jdbcTemplate, String addOrdersQuery,
                                     Integer currentOrder, Integer parentId) {
        jdbcTemplate.update(addOrdersQuery, currentOrder, parentId);
    }

    public static void shiftOrdersDown(JdbcTemplate jdbcTemplate, String subtractOrdersQuery,
                                       Integer currentOrder, Integer parentId) {
        jdbcTemplate.update(subtractOrdersQuery, currentOrder, parentId);
    }

    public static int getNextOrder(JdbcTemplate jdbcTemplate, String countQuery, Integer parentId) {
        return jdbcTemplate.queryForObject(countQuery, Integer.class, parentId) + 1;
    }
}
