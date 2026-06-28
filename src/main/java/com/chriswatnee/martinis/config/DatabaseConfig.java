package com.chriswatnee.martinis.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Database configuration optimized for Railway.com deployment.
 *
 * Supports multiple database URL formats:
 * - Railway format: MYSQL_URL or DATABASE_URL (e.g. ******host:port/db)
 * - JDBC format: jdbc:mysql://host:port/database
 * - PostgreSQL Railway format: ******host:port/db
 *
 * Environment variable resolution order:
 * 1. DATABASE_URL  - standard JDBC or Railway URL
 * 2. MYSQL_URL     - Railway MySQL service primary variable (same format)
 * 3. MYSQLHOST + MYSQLPORT + MYSQLDATABASE + MYSQLUSER + MYSQLPASSWORD - individual Railway vars
 * 4. Localhost fallback for local development
 */
@Configuration
@EnableTransactionManagement
public class DatabaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    @Value("${DATABASE_URL:#{null}}")
    private String databaseUrl;

    @Value("${MYSQL_URL:#{null}}")
    private String mysqlUrl;

    @Value("${MYSQLHOST:#{null}}")
    private String mysqlHost;

    @Value("${MYSQLPORT:3306}")
    private int mysqlPort;

    @Value("${MYSQLDATABASE:railway}")
    private String mysqlDatabase;

    @Value("${MYSQLUSER:#{null}}")
    private String mysqlUser;

    @Value("${MYSQLPASSWORD:#{null}}")
    private String mysqlPassword;

    @Value("${SPRING_DATASOURCE_USERNAME:#{null}}")
    private String username;

    @Value("${SPRING_DATASOURCE_PASSWORD:#{null}}")
    private String password;

    @Bean
    public DataSource dataSource() {
        logger.info("Initializing HikariCP DataSource for Railway deployment");

        HikariConfig config = new HikariConfig();

        try {
            String resolvedUrl = resolveUrl();
            parseDatabaseUrl(config, resolvedUrl);

            // If username/password are provided via environment variables, use them
            // This is useful for Docker Compose where DATABASE_URL doesn't contain credentials
            if (username != null && !username.isEmpty()) {
                config.setUsername(username);
                logger.info("Using username from SPRING_DATASOURCE_USERNAME environment variable");
            }
            if (password != null && !password.isEmpty()) {
                config.setPassword(password);
                logger.info("Using password from SPRING_DATASOURCE_PASSWORD environment variable");
            }
        } catch (Exception e) {
            logger.error("Failed to configure database connection", e);
            throw new RuntimeException("Failed to configure database connection", e);
        }

        // Connection pool settings optimized for Railway
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000); // 30 seconds
        config.setIdleTimeout(600000); // 10 minutes
        config.setMaxLifetime(1800000); // 30 minutes
        config.setLeakDetectionThreshold(60000); // 1 minute

        // Performance optimizations
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");

        // Connection validation
        config.setConnectionTestQuery("SELECT 1");
        config.setValidationTimeout(5000);

        logger.info("HikariCP DataSource configured successfully");
        logger.info("JDBC URL: {}", maskPassword(config.getJdbcUrl()));
        logger.info("Max Pool Size: {}, Min Idle: {}", config.getMaximumPoolSize(), config.getMinimumIdle());

        return new HikariDataSource(config);
    }

    /**
     * Resolve the database URL from environment variables in priority order:
     * 1. DATABASE_URL
     * 2. MYSQL_URL (Railway MySQL service primary variable)
     * 3. Build from individual MYSQLHOST/MYSQLPORT/MYSQLDATABASE/MYSQLUSER/MYSQLPASSWORD
     * 4. Localhost fallback
     */
    private String resolveUrl() {
        if (databaseUrl != null && !databaseUrl.isBlank()) {
            logger.info("Using DATABASE_URL environment variable");
            return databaseUrl;
        }
        if (mysqlUrl != null && !mysqlUrl.isBlank()) {
            logger.info("Using MYSQL_URL environment variable");
            return mysqlUrl;
        }
        if (mysqlHost != null && !mysqlHost.isBlank()) {
            logger.info("Building database URL from MYSQLHOST/MYSQLPORT/MYSQLDATABASE variables");
            String user = mysqlUser != null ? mysqlUser : "root";
            String pass = mysqlPassword != null ? mysqlPassword : "";
            return "mysql://" + user + ":" + pass + "@" + mysqlHost + ":" + mysqlPort + "/" + mysqlDatabase;
        }
        logger.warn("No database URL environment variable found; falling back to localhost default");
        return "jdbc:mysql://localhost:3306/martinis?useSSL=false&serverTimezone=UTC";
    }

    /**
     * Parse database URL in various formats and configure HikariCP
     */
    private void parseDatabaseUrl(HikariConfig config, String url) throws URISyntaxException {
        logger.debug("Parsing database URL: {}", maskPassword(url));

        if (url.startsWith("mysql://") && !url.startsWith("jdbc:")) {
            parseRailwayMySqlUrl(config, url);
        } else if (url.startsWith("postgres://") && !url.startsWith("jdbc:")) {
            parseRailwayPostgresUrl(config, url);
        } else if (url.startsWith("jdbc:")) {
            parseJdbcUrl(config, url);
        } else {
            throw new IllegalArgumentException("Unsupported database URL format: " + maskPassword(url));
        }
    }

    /**
     * Parse Railway MySQL URL format: mysql://user:password@host:port/database
     */
    private void parseRailwayMySqlUrl(HikariConfig config, String url) throws URISyntaxException {
        logger.info("Detected Railway MySQL URL format");

        URI uri = new URI(url.replace("mysql://", "http://"));
        String host = uri.getHost();
        int port = uri.getPort() > 0 ? uri.getPort() : 3306;
        String database = uri.getPath().substring(1);
        String jdbcUrl = buildJdbcUrl(
            "jdbc:mysql",
            host,
            port,
            database,
            uri.getRawQuery(),
            Map.of(
                "serverTimezone", "UTC",
                "allowPublicKeyRetrieval", "true",
                "createDatabaseIfNotExist", "false"
            )
        );

        config.setJdbcUrl(jdbcUrl);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");

        String userInfo = uri.getUserInfo();
        if (userInfo != null && userInfo.contains(":")) {
            String[] credentials = userInfo.split(":", 2);
            config.setUsername(credentials[0]);
            config.setPassword(credentials[1]);
            logger.info("Database credentials extracted from URL");
        }

        logger.info("MySQL JDBC URL configured: {}", maskPassword(jdbcUrl));
    }

    /**
     * Parse Railway PostgreSQL URL format: postgres://user:password@host:port/database
     */
    private void parseRailwayPostgresUrl(HikariConfig config, String url) throws URISyntaxException {
        logger.info("Detected Railway PostgreSQL URL format");

        URI uri = new URI(url.replace("postgres://", "http://"));
        String host = uri.getHost();
        int port = uri.getPort() > 0 ? uri.getPort() : 5432;
        String database = uri.getPath().substring(1);

        String jdbcUrl = String.format(
            "jdbc:postgresql://%s:%d/%s?ssl=true&sslmode=require",
            host, port, database
        );

        config.setJdbcUrl(jdbcUrl);
        config.setDriverClassName("org.postgresql.Driver");

        String userInfo = uri.getUserInfo();
        if (userInfo != null && userInfo.contains(":")) {
            String[] credentials = userInfo.split(":", 2);
            config.setUsername(credentials[0]);
            config.setPassword(credentials[1]);
            logger.info("Database credentials extracted from URL");
        }

        logger.info("PostgreSQL JDBC URL configured: {}", maskPassword(jdbcUrl));
    }

    /**
     * Parse standard JDBC URL format
     */
    private void parseJdbcUrl(HikariConfig config, String url) {
        logger.info("Detected standard JDBC URL format");

        config.setJdbcUrl(url);

        if (url.contains("mysql")) {
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            logger.info("MySQL driver selected");
        } else if (url.contains("postgresql")) {
            config.setDriverClassName("org.postgresql.Driver");
            logger.info("PostgreSQL driver selected");
        }
    }

    private String buildJdbcUrl(String scheme,
                                String host,
                                int port,
                                String database,
                                String rawQuery,
                                Map<String, String> defaultParams) {
        Map<String, String> params = new LinkedHashMap<>();

        if (rawQuery != null && !rawQuery.isBlank()) {
            for (String pair : rawQuery.split("&")) {
                if (pair.isBlank()) {
                    continue;
                }

                int separatorIndex = pair.indexOf('=');
                String key = separatorIndex >= 0 ? pair.substring(0, separatorIndex) : pair;
                String value = separatorIndex >= 0 ? pair.substring(separatorIndex + 1) : "";
                params.putIfAbsent(key, value);
            }
        }

        Set<String> normalizedKeys = new HashSet<>();
        params.keySet().forEach(key -> normalizedKeys.add(key.toLowerCase(Locale.ROOT)));
        defaultParams.forEach((key, value) -> {
            if (normalizedKeys.add(key.toLowerCase(Locale.ROOT))) {
                params.put(key, value);
            }
        });

        StringBuilder jdbcUrl = new StringBuilder(String.format("%s://%s:%d/%s", scheme, host, port, database));
        if (!params.isEmpty()) {
            jdbcUrl.append('?');
            boolean first = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (!first) {
                    jdbcUrl.append('&');
                }
                jdbcUrl.append(entry.getKey()).append('=').append(entry.getValue());
                first = false;
            }
        }

        return jdbcUrl.toString();
    }

    /**
     * Mask password in URL for logging
     */
    private String maskPassword(String url) {
        if (url == null) return "null";
        return url.replaceAll(":[^:@]+@", ":****@")
                  .replaceAll("password=[^&]+", "password=****");
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        logger.info("Creating JdbcTemplate bean");
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        logger.info("Creating DataSourceTransactionManager bean");
        return new DataSourceTransactionManager(dataSource);
    }
}
