package com.chriswatnee.martinis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DatabaseCheckController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @RequestMapping(value = "/dbcheck", method = RequestMethod.GET)
    @ResponseBody
    public String checkDatabase() {
        StringBuilder result = new StringBuilder();
        result.append("<h1>Database Status</h1>");

        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            result.append("<p style='color:green;font-weight:bold;'>Database connection successful.</p>");
        } catch (Exception e) {
            result.append("<p style='color:red;font-weight:bold;'>Database connection failed.</p>");
        }

        return result.toString();
    }
}
