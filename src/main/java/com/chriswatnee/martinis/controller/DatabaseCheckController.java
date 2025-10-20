package com.chriswatnee.martinis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class DatabaseCheckController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @RequestMapping(value = "/dbcheck", method = RequestMethod.GET)
    @ResponseBody
    public String checkDatabase() {
        StringBuilder result = new StringBuilder();
        result.append("<h1>Database Verification</h1>");
        result.append("<style>body{font-family:monospace;padding:20px;} table{border-collapse:collapse;margin:10px 0;} th,td{border:1px solid #ddd;padding:8px;text-align:left;}</style>");

        try {
            // Check users
            result.append("<h2>Users in database:</h2>");
            List<Map<String, Object>> users = jdbcTemplate.queryForList(
                "SELECT id, username, SUBSTRING(`password`, 1, 30) as password_hash, enabled, first_name, last_name FROM `user`"
            );

            result.append("<table><tr><th>ID</th><th>Username</th><th>Password Hash (first 30 chars)</th><th>Enabled</th><th>First Name</th><th>Last Name</th></tr>");
            for (Map<String, Object> user : users) {
                result.append("<tr>");
                result.append("<td>").append(user.get("id")).append("</td>");
                result.append("<td>").append(user.get("username")).append("</td>");
                result.append("<td>").append(user.get("password_hash")).append("</td>");
                result.append("<td>").append(user.get("enabled")).append("</td>");
                result.append("<td>").append(user.get("first_name")).append("</td>");
                result.append("<td>").append(user.get("last_name")).append("</td>");
                result.append("</tr>");
            }
            result.append("</table>");

            // Check authorities
            result.append("<h2>Authorities in database:</h2>");
            List<Map<String, Object>> authorities = jdbcTemplate.queryForList(
                "SELECT username, authority FROM authority"
            );

            result.append("<table><tr><th>Username</th><th>Authority</th></tr>");
            for (Map<String, Object> auth : authorities) {
                result.append("<tr>");
                result.append("<td>").append(auth.get("username")).append("</td>");
                result.append("<td>").append(auth.get("authority")).append("</td>");
                result.append("</tr>");
            }
            result.append("</table>");

            // Check what Spring Security queries will return
            result.append("<h2>Spring Security User Query Test (for 'admin'):</h2>");
            List<Map<String, Object>> securityUser = jdbcTemplate.queryForList(
                "SELECT username, `password`, enabled FROM `user` WHERE username = ?", "admin"
            );

            result.append("<table><tr><th>Username</th><th>Password Hash (first 30 chars)</th><th>Enabled</th></tr>");
            for (Map<String, Object> user : securityUser) {
                result.append("<tr>");
                result.append("<td>").append(user.get("username")).append("</td>");
                result.append("<td>").append(String.valueOf(user.get("password")).substring(0, 30)).append("...</td>");
                result.append("<td>").append(user.get("enabled")).append("</td>");
                result.append("</tr>");
            }
            result.append("</table>");

            result.append("<h2>Spring Security Authority Query Test (for 'admin'):</h2>");
            List<Map<String, Object>> securityAuth = jdbcTemplate.queryForList(
                "SELECT username, authority FROM authority WHERE username = ?", "admin"
            );

            result.append("<table><tr><th>Username</th><th>Authority</th></tr>");
            for (Map<String, Object> auth : securityAuth) {
                result.append("<tr>");
                result.append("<td>").append(auth.get("username")).append("</td>");
                result.append("<td>").append(auth.get("authority")).append("</td>");
                result.append("</tr>");
            }
            result.append("</table>");

            result.append("<p style='color:green;font-weight:bold;'>✓ Database connection successful!</p>");
            result.append("<p><strong>Login credentials:</strong> Username: <code>admin</code> / Password: <code>password</code></p>");

        } catch (Exception e) {
            result.append("<p style='color:red;'>Error: ").append(e.getMessage()).append("</p>");
            result.append("<pre>").append(e.getClass().getName()).append("</pre>");
        }

        return result.toString();
    }
}
