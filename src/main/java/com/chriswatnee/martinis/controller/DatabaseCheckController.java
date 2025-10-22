package com.chriswatnee.martinis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    @Autowired
    private BCryptPasswordEncoder encoder;

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

            // Generate correct bcrypt hash
            result.append("<h2>Generate Correct BCrypt Hash:</h2>");
            String correctPassword = "password";
            String newHash = encoder.encode(correctPassword);

            result.append("<table><tr><th>Item</th><th>Value</th></tr>");
            result.append("<tr><td>Password to encode</td><td><code>").append(correctPassword).append("</code></td></tr>");
            result.append("<tr><td>Correct BCrypt hash</td><td><code>").append(newHash).append("</code></td></tr>");
            result.append("<tr><td>Verification</td><td style='color:green;font-weight:bold;'>✓ ").append(encoder.matches(correctPassword, newHash) ? "VALID" : "INVALID").append("</td></tr>");
            result.append("</table>");

            result.append("<p><strong>Copy this hash and use it in data.sql:</strong></p>");
            result.append("<pre style='background:#f5f5f5;padding:10px;border:1px solid #ddd;overflow-x:auto;'>");
            result.append(newHash);
            result.append("</pre>");

            // Test bcrypt password matching
            result.append("<h2>BCrypt Password Verification (Current Database):</h2>");
            List<Map<String, Object>> adminUser = jdbcTemplate.queryForList(
                "SELECT username, `password` FROM `user` WHERE username = ?", "admin"
            );

            if (!adminUser.isEmpty()) {
                String storedHash = (String) adminUser.get(0).get("password");
                String testPassword = "password";

                boolean matches = encoder.matches(testPassword, storedHash);

                result.append("<table><tr><th>Test</th><th>Result</th></tr>");
                result.append("<tr><td>Stored hash</td><td><code>").append(storedHash).append("</code></td></tr>");
                result.append("<tr><td>Test password</td><td><code>").append(testPassword).append("</code></td></tr>");
                result.append("<tr><td>BCrypt matches?</td><td style='font-weight:bold;color:");
                result.append(matches ? "green'>✓ YES" : "red'>✗ NO");
                result.append("</td></tr></table>");

                if (matches) {
                    result.append("<p style='color:green;font-weight:bold;'>✓ Password 'password' matches the stored hash!</p>");
                    result.append("<p><strong>Login should work with:</strong> Username: <code>admin</code> / Password: <code>password</code></p>");
                } else {
                    result.append("<p style='color:red;font-weight:bold;'>✗ Password 'password' does NOT match the stored hash!</p>");
                    result.append("<p style='background:#fff3cd;padding:10px;border:1px solid #ffc107;'>");
                    result.append("<strong>SOLUTION:</strong> Copy the correct hash from the 'Generate Correct BCrypt Hash' section above ");
                    result.append("and paste it into your <code>src/main/resources/data.sql</code> file, replacing the old hash. ");
                    result.append("Then commit and push the change to trigger a new deployment.");
                    result.append("</p>");
                }
            }

        } catch (Exception e) {
            result.append("<p style='color:red;'>Error: ").append(e.getMessage()).append("</p>");
            result.append("<pre>").append(e.getClass().getName()).append("</pre>");
            e.printStackTrace();
        }

        return result.toString();
    }
}
