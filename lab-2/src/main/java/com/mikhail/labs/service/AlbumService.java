package com.mikhail.labs.service;

import com.mikhail.labs.model.Album;
import com.mikhail.labs.repository.AlbumRepository;

import java.util.List;


public class AlbumService {

    private final AlbumRepository repository;

    public AlbumService(AlbumRepository repository) {
        this.repository = repository;
    }

    /**
     * Создаёт новый альбом.
     *
     * @param album альбом для создания
     * @return id созданной записи
     */
    public int createAlbum(Album album) {
        return repository.save(album);
    }

    /**
     * Находит альбом по id.
     *
     * @param id идентификатор альбома
     * @return альбом или null, если не найден
     */
    public Album findAlbumById(int id) {
        return repository.findById(id);
    }

    /**
     * Находит альбом по названию.
     *
     * @param title название альбома
     * @return альбом или null, если не найден
     */
    public Album findAlbumByTitle(String title) {
        return repository.findByTitle(title);
    }

    /**
     * Возвращает все альбомы.
     *
     * @return список всех альбомов
     */
    public List<Album> findAllAlbums() {
        return repository.findAll();
    }

    /**
     * Обновляет альбом по id.
     *
     * @param album альбом с новыми данными
     * @return true, если запись обновлена; false, если не найдена
     */
    public boolean updateAlbum(Album album) {
        return repository.update(album);
    }

    /**
     * Удаляет альбом по id.
     *
     * @param id идентификатор альбома
     */
    public void deleteAlbumById(int id) {
        repository.deleteById(id);
    }
}
