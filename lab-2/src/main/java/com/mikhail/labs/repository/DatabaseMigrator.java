package com.mikhail.labs.repository;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Выполнение миграций базы данных с помощью Flyway.
 * При запуске программы проверяется наличие таблицы и, в случае отсутствия,
 * она создаётся миграцией из resources/db/migration.
 */
public class DatabaseMigrator {

    private static final Logger log = LoggerFactory.getLogger(DatabaseMigrator.class);

    private final String url;
    private final String user;
    private final String password;
    private final String schema;

    public DatabaseMigrator(String url, String user, String password, String schema) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.schema = schema;
    }

    /**
     * Применяет недостающие миграции (идемпотентно: повторный запуск ничего не меняет).
     */
    public void runMigrations() {
        try {
            Flyway flyway = Flyway.configure()
                    .dataSource(url, user, password)
                    .schemas(schema)
                    .defaultSchema(schema)
                    .locations("classpath:db/migration")
                    .load();

            flyway.migrate();
            log.info("Миграции успешно применены, schema={}", schema);
        } catch (Exception e) {
            log.error("Ошибка применения миграций БД", e);
            throw new RuntimeException("Ошибка при выполнении миграций", e);
        }
    }
}
