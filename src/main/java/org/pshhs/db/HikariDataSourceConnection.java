package org.pshhs.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

@Slf4j
@Getter
public class HikariDataSourceConnection {

    private static final HikariDataSource INSTANCE;

    private HikariDataSourceConnection() {
    }

    static {
        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USERNAME");
        String pass = System.getenv("DB_PASSWORD");
        try {

            var properties = PropertiesLoader.loadProperties("db.properties");
            if (url != null && user != null && pass != null) {
                properties.setProperty("dataSource.url", url);
                properties.setProperty("dataSource.user", user);
                properties.setProperty("dataSource.password", pass);
            }
            log.debug("PROPERTYS: \n{}",properties);
            var hikariConfig = new HikariConfig(properties);

            INSTANCE = new HikariDataSource(hikariConfig);//
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return INSTANCE.getConnection();
    }

}
