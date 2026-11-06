package com.mikhail.labs.service;

import com.mikhail.labs.model.Album;

import java.util.List;

/**
 * Сервисный слой: бизнес-логика работы с альбомами.
 * Оборачивает репозиторий контрактом «сущность не найдена — исключение».
 */
public interface AlbumService {

    /**
     * Создаёт новый альбом.
     *
     * @param title       название альбома
     * @param releaseYear год выпуска
     * @return id созданной записи
     */
    int save(String title, int releaseYear);

    /**
     * Находит альбом по id.
     *
     * @param id идентификатор альбома
     * @return найденный альбом
     * @throws com.mikhail.labs.exception.AlbumNotFoundException если запись не найдена
     */
    Album findById(int id);

    /**
     * Находит альбом по названию.
     *
     * @param title название альбома
     * @return найденный альбом
     * @throws com.mikhail.labs.exception.AlbumNotFoundException если запись не найдена
     */
    Album findByTitle(String title);

    /**
     * Возвращает все альбомы.
     *
     * @return список всех альбомов
     */
    List<Album> findAll();

    /**
     * Обновляет альбом; сущность ищется по id из переданного объекта.
     *
     * @param album альбом с новыми данными
     * @throws com.mikhail.labs.exception.AlbumNotFoundException если запись не найдена
     */
    void update(Album album);

    /**
     * Удаляет альбом по id (повторное удаление безопасно).
     *
     * @param id идентификатор альбома
     */
    void deleteById(int id);
}
