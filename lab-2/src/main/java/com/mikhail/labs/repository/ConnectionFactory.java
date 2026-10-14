package com.mikhail.labs.repository;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionFactory {

    private static final Logger log = LoggerFactory.getLogger(ConnectionFactory.class);

    private final DataSource dataSource;

    public ConnectionFactory(String url, String user, String password, String schema) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(password);
        config.setDriverClassName("org.postgresql.Driver");
        config.setSchema(schema);


        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);
        config.setIdleTimeout(300000);
        config.setMaxLifetime(1800000);
        config.setConnectionTimeout(30000);

        dataSource = new HikariDataSource(config);
        log.info("Connection pool HikariCP инициализирован, schema={}", schema);

        Runtime.getRuntime().addShutdownHook(new Thread(this::closeDataSource));
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void closeDataSource() {
        if (dataSource instanceof HikariDataSource hikariDataSource) {
            hikariDataSource.close();
            log.info("Connection pool HikariCP закрыт");
        }
    }
}
