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

/**
 * Database configuration optimized for Railway.com deployment
 * Supports multiple database URL formats:
 * - Railway format: mysql://user:password@host:port/database
 * - JDBC format: jdbc:mysql://host:port/database
 * - PostgreSQL format: postgres://user:password@host:port/database (converts to JDBC)
 */
@Configuration
@EnableTransactionManagement
public class DatabaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    @Value("${DATABASE_URL:jdbc:mysql://localhost:3306/martinis?useSSL=false&serverTimezone=UTC}")
    private String databaseUrl;

    @Bean
    public DataSource dataSource() {
        logger.info("Initializing HikariCP DataSource for Railway deployment");

        HikariConfig config = new HikariConfig();

        try {
            parseDatabaseUrl(config, databaseUrl);
        } catch (Exception e) {
            logger.error("Failed to parse DATABASE_URL: {}", databaseUrl, e);
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
     * Parse database URL in various formats and configure HikariCP
     */
    private void parseDatabaseUrl(HikariConfig config, String url) throws URISyntaxException {
        logger.debug("Parsing DATABASE_URL: {}", maskPassword(url));

        // Railway MySQL format: mysql://user:password@host:port/database
        if (url.startsWith("mysql://") && !url.startsWith("jdbc:")) {
            parseRailwayMySqlUrl(config, url);
        }
        // Railway PostgreSQL format: postgres://user:password@host:port/database
        else if (url.startsWith("postgres://") && !url.startsWith("jdbc:")) {
            parseRailwayPostgresUrl(config, url);
        }
        // Standard JDBC format
        else if (url.startsWith("jdbc:")) {
            parseJdbcUrl(config, url);
        }
        else {
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

        String jdbcUrl = String.format(
            "jdbc:mysql://%s:%d/%s?useSSL=true&requireSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=false",
            host, port, database
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
