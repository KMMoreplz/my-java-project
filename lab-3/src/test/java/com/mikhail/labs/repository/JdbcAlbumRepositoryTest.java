package com.mikhail.labs.repository;

import com.mikhail.labs.BaseWithPostgresTest;
import com.mikhail.labs.model.Album;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Интеграционные тесты JDBC-репозитория на реальном PostgreSQL (TestContainers).
 * Контейнер, миграции и репозиторий готовит базовый класс {@link BaseWithPostgresTest}.
 */
class JdbcAlbumRepositoryTest extends BaseWithPostgresTest {

    @BeforeEach
    void cleanUp() {
        repository.findAll().forEach(album -> repository.deleteById(album.getId()));
    }

    @Test
    void save_returnsGeneratedId() {
        Album album = new Album("Abbey Road", 1969);

        int id = repository.save(album);

        assertTrue(id > 0);
        assertEquals("Abbey Road", album.getTitle());
    }

    @Test
    void findById_returnsAlbumWhenFound() {
        int id = repository.save(new Album("Abbey Road", 1969));

        Album found = repository.findById(id);

        assertEquals(id, found.getId());
        assertEquals("Abbey Road", found.getTitle());
        assertEquals(1969, found.getReleaseYear());
    }

    @Test
    void findById_returnsNullWhenNotFound() {
        assertNull(repository.findById(99));
    }

    @Test
    void findByTitle_returnsAlbumWhenFound() {
        Album saved = new Album("Abbey Road", 1969);
        repository.save(saved);

        Album found = repository.findByTitle("Abbey Road");

        assertEquals(saved.getId(), found.getId());
    }

    @Test
    void findByTitle_returnsNullWhenNotFound() {
        assertNull(repository.findByTitle("Unknown"));
    }

    @Test
    void findAll_returnsEmptyListWhenNothingStored() {
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    void findAll_returnsAllAlbumsSortedById() {
        Album first = new Album("Abbey Road", 1969);
        Album second = new Album("The Dark Side of the Moon", 1973);
        repository.save(first);
        repository.save(second);

        List<Album> all = repository.findAll();

        assertEquals(2, all.size());
        assertEquals(first.getId(), all.get(0).getId());
        assertEquals(second.getId(), all.get(1).getId());
    }

    @Test
    void update_returnsTrueWhenFound() {
        int id = repository.save(new Album("Abbey Road", 1969));

        boolean updated = repository.update(new Album(id, "Abbey Road (Remaster)", 1970));

        assertTrue(updated);
        assertEquals("Abbey Road (Remaster)", repository.findById(id).getTitle());
    }

    @Test
    void update_returnsFalseWhenNotFound() {
        assertFalse(repository.update(new Album(99, "Missing", 2000)));
    }

    @Test
    void deleteById_isIdempotent() {
        int id = repository.save(new Album("Abbey Road", 1969));

        assertDoesNotThrow(() -> repository.deleteById(id));
        assertDoesNotThrow(() -> repository.deleteById(id));
        assertNull(repository.findById(id));
    }

    @Test
    void runMigrations_isIdempotent() {
        DatabaseMigrator migrator =
                new DatabaseMigrator(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword(), SCHEMA);

        assertDoesNotThrow(migrator::runMigrations);
    }
}
