package com.mikhail.labs;

import com.mikhail.labs.repository.AlbumRepository;
import com.mikhail.labs.repository.ConnectionFactory;
import com.mikhail.labs.repository.DatabaseMigrator;
import com.mikhail.labs.repository.JdbcAlbumRepository;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

/**
 * Базовый класс интеграционных тестов: реальный PostgreSQL поднимается в Docker
 * прямо во время теста (TestContainers), миграции и репозиторий готовы к работе.
 * При отсутствии Docker тесты пропускаются.
 */
@Testcontainers(disabledWithoutDocker = true)
public abstract class BaseWithPostgresTest {

    protected static final String SCHEMA = "mikhail_labs";

    @Container
    protected static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:18");

    protected static AlbumRepository repository;

    @BeforeAll
    static void initRepository() {
        new DatabaseMigrator(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword(), SCHEMA)
                .runMigrations();
        ConnectionFactory connectionFactory =
                new ConnectionFactory(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword(), SCHEMA);
        repository = new JdbcAlbumRepository(connectionFactory, SCHEMA, "album");
    }
}
