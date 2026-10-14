package com.mikhail.labs.repository;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Фабрика соединений с базой данных на основе connection pool HikariCP.
 * Пул создаётся один раз при старте и закрывается при завершении приложения.
 */
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

        // Настройки пула (можно настроить под ваши нужды)
        config.setMaximumPoolSize(10); // максимальное количество соединений в пуле
        config.setMinimumIdle(5); // минимальное количество простаивающих соединений
        config.setIdleTimeout(300000); // время простоя соединения (5 минут)
        config.setMaxLifetime(1800000); // максимальное время жизни соединения (30 минут)
        config.setConnectionTimeout(30000); // таймаут ожидания соединения (30 секунд)

        // Инициализация пула
        dataSource = new HikariDataSource(config);
        log.info("Connection pool HikariCP инициализирован, schema={}", schema);

        // Чтобы избежать утечек ресурсов БД, закрываем пул при завершении приложения
        Runtime.getRuntime().addShutdownHook(new Thread(this::closeDataSource));
    }

    /**
     * Возвращает соединение из пула.
     * Соединение обязательно закрывать через try-with-resources —
     * оно возвращается обратно в пул.
     */
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Закрывает пул соединений (вызывается при завершении приложения).
     */
    public void closeDataSource() {
        if (dataSource instanceof HikariDataSource hikariDataSource) {
            hikariDataSource.close();
            log.info("Connection pool HikariCP закрыт");
        }
    }
}
