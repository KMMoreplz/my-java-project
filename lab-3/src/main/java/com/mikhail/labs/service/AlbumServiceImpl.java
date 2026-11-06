package com.mikhail.labs.service;

import com.mikhail.labs.exception.AlbumNotFoundException;
import com.mikhail.labs.model.Album;
import com.mikhail.labs.repository.AlbumRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Реализация бизнес-логики поверх {@link AlbumRepository}.
 */
@Service
public class AlbumServiceImpl implements AlbumService {

    private static final Logger log = LoggerFactory.getLogger(AlbumServiceImpl.class);

    private final AlbumRepository repository;

    public AlbumServiceImpl(AlbumRepository repository) {
        this.repository = repository;
    }

    @Override
    public int save(String title, int releaseYear) {
        int id = repository.save(new Album(title, releaseYear));
        log.info("Создан альбом id={}, title='{}', releaseYear={}", id, title, releaseYear);
        return id;
    }

    @Override
    public Album findById(int id) {
        Album album = repository.findById(id);
        if (album == null) {
            throw new AlbumNotFoundException("Альбом с id=" + id + " не найден");
        }
        return album;
    }

    @Override
    public Album findByTitle(String title) {
        Album album = repository.findByTitle(title);
        if (album == null) {
            throw new AlbumNotFoundException("Альбом с названием '" + title + "' не найден");
        }
        return album;
    }

    @Override
    public List<Album> findAll() {
        return repository.findAll();
    }

    @Override
    public void update(Album album) {
        if (!repository.update(album)) {
            throw new AlbumNotFoundException("Альбом с id=" + album.getId() + " не найден");
        }
        log.info("Обновлён альбом id={}", album.getId());
    }

    @Override
    public void deleteById(int id) {
        repository.deleteById(id);
        log.info("Удалён альбом id={}", id);
    }
}
