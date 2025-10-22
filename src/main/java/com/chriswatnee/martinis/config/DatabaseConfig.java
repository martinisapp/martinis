package com.chriswatnee.martinis.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
@EnableTransactionManagement
public class DatabaseConfig {

    @Value("${DATABASE_URL:jdbc:mysql://localhost:3306/martinis?useSSL=false&serverTimezone=UTC}")
    private String databaseUrl;

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();

        // Parse Railway DATABASE_URL format: mysql://user:password@host:port/database
        if (databaseUrl.startsWith("mysql://") && !databaseUrl.startsWith("jdbc:")) {
            try {
                URI uri = new URI(databaseUrl.replace("mysql://", "http://"));
                String host = uri.getHost();
                int port = uri.getPort() > 0 ? uri.getPort() : 3306;
                String database = uri.getPath().substring(1);
                String userInfo = uri.getUserInfo();

                String jdbcUrl = String.format(
                    "jdbc:mysql://%s:%d/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                    host, port, database
                );

                config.setJdbcUrl(jdbcUrl);

                if (userInfo != null) {
                    String[] credentials = userInfo.split(":");
                    config.setUsername(credentials[0]);
                    if (credentials.length > 1) {
                        config.setPassword(credentials[1]);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse DATABASE_URL: " + databaseUrl, e);
            }
        } else {
            // Already in JDBC format
            config.setJdbcUrl(databaseUrl);
        }

        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(20000);
        config.setIdleTimeout(300000);
        config.setMaxLifetime(1200000);

        return new HikariDataSource(config);
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
