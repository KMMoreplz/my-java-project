package com.mikhail.labs.config;

import com.mikhail.labs.repository.AlbumRepository;
import com.mikhail.labs.repository.ConnectionFactory;
import com.mikhail.labs.repository.DatabaseMigrator;
import com.mikhail.labs.repository.JdbcAlbumRepository;
import com.mikhail.labs.repository.PropertiesService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class AppConfig {

    private static final String TABLE_NAME = "album";

    @Bean
    public PropertiesService propertiesService() {
        return new PropertiesService();
    }

    @Bean
    @Lazy
    public ConnectionFactory connectionFactory(PropertiesService properties) {
        return new ConnectionFactory(
                properties.get("database.url"),
                properties.get("database.username"),
                properties.get("database.password"),
                properties.get("database.schema"));
    }

    @Bean
    public DatabaseMigrator databaseMigrator(PropertiesService properties) {
        return new DatabaseMigrator(
                properties.get("database.url"),
                properties.get("database.username"),
                properties.get("database.password"),
                properties.get("database.schema"));
    }

    @Bean
    @Lazy
    public AlbumRepository albumRepository(ConnectionFactory connectionFactory, PropertiesService properties) {
        return new JdbcAlbumRepository(connectionFactory, properties.get("database.schema"), TABLE_NAME);
    }

    @Bean
    @ConditionalOnProperty(name = "app.migrate-on-startup", havingValue = "true", matchIfMissing = true)
    public ApplicationRunner migrationsRunner(DatabaseMigrator migrator) {
        return args -> migrator.runMigrations();
    }
}
