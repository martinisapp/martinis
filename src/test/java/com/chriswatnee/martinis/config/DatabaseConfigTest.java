package com.chriswatnee.martinis.config;

import com.zaxxer.hikari.HikariConfig;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabaseConfigTest {

    private static final String BASE_RAILWAY_URL =
        "mysql://railway" + ":" + "secret" + "@" + "db.railway.internal:3306/martinis";

    @Test
    void railwayMySqlUrlPreservesIncomingSslSettings() throws Exception {
        HikariConfig dataSourceConfig = createDataSourceConfig(
            BASE_RAILWAY_URL + "?useSSL=false&serverTimezone=America%2FNew_York"
        );

        assertEquals("railway", dataSourceConfig.getUsername());
        assertEquals("secret", dataSourceConfig.getPassword());
        assertTrue(dataSourceConfig.getJdbcUrl().startsWith("jdbc:mysql://db.railway.internal:3306/martinis?"));
        assertTrue(dataSourceConfig.getJdbcUrl().contains("useSSL=false"));
        assertTrue(dataSourceConfig.getJdbcUrl().contains("serverTimezone=America%2FNew_York"));
        assertTrue(dataSourceConfig.getJdbcUrl().contains("allowPublicKeyRetrieval=true"));
        assertTrue(dataSourceConfig.getJdbcUrl().contains("createDatabaseIfNotExist=false"));
        assertFalse(dataSourceConfig.getJdbcUrl().contains("requireSSL=true"));
    }

    @Test
    void railwayMySqlUrlAddsSafeDefaultsWhenQueryIsMissing() throws Exception {
        HikariConfig dataSourceConfig = createDataSourceConfig(BASE_RAILWAY_URL);

        assertTrue(dataSourceConfig.getJdbcUrl().startsWith("jdbc:mysql://db.railway.internal:3306/martinis?"));
        assertTrue(dataSourceConfig.getJdbcUrl().contains("serverTimezone=UTC"));
        assertTrue(dataSourceConfig.getJdbcUrl().contains("allowPublicKeyRetrieval=true"));
        assertTrue(dataSourceConfig.getJdbcUrl().contains("createDatabaseIfNotExist=false"));
    }

    private HikariConfig createDataSourceConfig(String databaseUrl) throws Exception {
        DatabaseConfig config = new DatabaseConfig();
        HikariConfig hikariConfig = new HikariConfig();
        setField(config, "databaseUrl", databaseUrl);
        invokeParseDatabaseUrl(config, hikariConfig, databaseUrl);
        return hikariConfig;
    }

    private void setField(Object target, String name, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    private void invokeParseDatabaseUrl(DatabaseConfig config, HikariConfig hikariConfig, String databaseUrl) throws Exception {
        Method method = DatabaseConfig.class.getDeclaredMethod("parseDatabaseUrl", HikariConfig.class, String.class);
        method.setAccessible(true);
        method.invoke(config, hikariConfig, databaseUrl);
    }
}
