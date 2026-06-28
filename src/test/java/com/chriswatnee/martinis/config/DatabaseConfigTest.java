package com.chriswatnee.martinis.config;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class DatabaseConfigTest {

    @Test
    void railwayMySqlUrlPreservesIncomingSslSettings() throws Exception {
        HikariDataSource dataSource = createDataSource(
            "mysql://railway" + ":" + "secret" + "@"
                + "db.railway.internal:3306/martinis?useSSL=false&serverTimezone=America%2FNew_York"
        );

        try {
            assertEquals("railway", dataSource.getUsername());
            assertEquals("secret", dataSource.getPassword());
            assertEquals(
                "jdbc:mysql://db.railway.internal:3306/martinis?useSSL=false&serverTimezone=America%2FNew_York&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=false",
                dataSource.getJdbcUrl()
            );
            assertFalse(dataSource.getJdbcUrl().contains("requireSSL=true"));
        } finally {
            dataSource.close();
        }
    }

    @Test
    void railwayMySqlUrlAddsSafeDefaultsWhenQueryIsMissing() throws Exception {
        HikariDataSource dataSource = createDataSource(
            "mysql://railway" + ":" + "secret" + "@"
                + "db.railway.internal:3306/martinis"
        );

        try {
            assertEquals(
                "jdbc:mysql://db.railway.internal:3306/martinis?serverTimezone=UTC&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=false",
                dataSource.getJdbcUrl()
            );
        } finally {
            dataSource.close();
        }
    }

    private HikariDataSource createDataSource(String databaseUrl) throws Exception {
        DatabaseConfig config = new DatabaseConfig();
        setField(config, "databaseUrl", databaseUrl);
        return (HikariDataSource) config.dataSource();
    }

    private void setField(Object target, String name, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }
}
