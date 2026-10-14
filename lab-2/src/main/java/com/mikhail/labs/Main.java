package com.mikhail.labs;

import com.mikhail.labs.model.Album;
import com.mikhail.labs.repository.AlbumRepository;
import com.mikhail.labs.repository.ConnectionFactory;
import com.mikhail.labs.repository.DatabaseMigrator;
import com.mikhail.labs.repository.JdbcAlbumRepository;
import com.mikhail.labs.repository.PropertiesService;
import com.mikhail.labs.service.AlbumService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private static final String TABLE_NAME = "album";

    public static void main(String[] args) {
        // Настройки подключения — из application.properties
        PropertiesService properties = new PropertiesService();
        String url = properties.get("database.url");
        String user = properties.get("database.username");
        String password = properties.get("database.password");
        String schema = properties.get("database.schema");

        ConnectionFactory connectionFactory = new ConnectionFactory(url, user, password, schema);

        new DatabaseMigrator(url, user, password, schema).runMigrations();

        AlbumRepository repository = new JdbcAlbumRepository(connectionFactory, schema, TABLE_NAME);
        AlbumService service = new AlbumService(repository);

        log.info("=== CREATE: добавляем альбомы ===");
        int id1 = service.createAlbum(new Album("The Dark Side of the Moon", 1973));
        log.info("Создан альбом с id={}", id1);
        int id2 = service.createAlbum(new Album("Abbey Road", 1969));
        log.info("Создан альбом с id={}", id2);

        log.info("=== READ: поиск по id ===");
        Album foundById = service.findAlbumById(id1);
        log.info("Найден по id={}: {}", id1, foundById);

        log.info("=== READ: поиск по названию ===");
        Album foundByTitle = service.findAlbumByTitle("Abbey Road");
        log.info("Найден по названию: {}", foundByTitle);

        log.info("=== READ: все альбомы ===");
        List<Album> all = service.findAllAlbums();
        all.forEach(album -> log.info("{}", album));

        log.info("=== UPDATE: обновляем альбом с id={} ===", id1);
        Album updated = new Album(id1, "The Dark Side of the Moon (Remaster)", 1974);
        boolean updateResult = service.updateAlbum(updated);
        log.info("Обновление выполнено: {}, теперь: {}", updateResult, service.findAlbumById(id1));

        log.info("=== UPDATE: обновление несуществующей записи ===");
        boolean missingUpdate = service.updateAlbum(new Album(9999, "Not Exists", 2000));
        log.info("Обновление несуществующей записи (id=9999): {}", missingUpdate);

        log.info("=== DELETE: удаляем альбом с id={} ===", id2);
        service.deleteAlbumById(id2);
        log.info("Альбом с id={} после удаления: {}", id2, service.findAlbumById(id2));

        log.info("=== Идемпотентность: повторный вызов findAll и deleteById ===");
        service.deleteAlbumById(id2);
        log.info("Повторный deleteById({}) без ошибок, альбомов в таблице: {}", id2, service.findAllAlbums().size());

        log.info("=== РАБОТА ЗАВЕРШЕНА ===");
    }
}
