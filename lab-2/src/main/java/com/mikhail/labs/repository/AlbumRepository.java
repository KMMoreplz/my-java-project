package com.mikhail.labs.repository;

import com.mikhail.labs.model.Album;

import java.util.List;

public interface AlbumRepository {

    /**
     * Сохраняет новый альбом в базе данных.
     *
     * @param album альбом для сохранения (id заполняется автоматически)
     * @return id созданной записи
     */
    int save(Album album);

    /**
     * Находит альбом по id.
     *
     * @param id идентификатор альбома
     * @return альбом или null, если запись не найдена (контракт задания)
     */
    Album findById(int id);

    /**
     * Находит альбом по названию (второе поле сущности из варианта).
     *
     * @param title название альбома
     * @return альбом или null, если запись не найдена (контракт задания)
     */
    Album findByTitle(String title);

    /**
     * Возвращает все альбомы.
     *
     * @return список всех альбомов, отсортированный по id
     */
    List<Album> findAll();

    /**
     * Обновляет альбом. Сущность ищется по id.
     *
     * @param album альбом с новыми данными (id определяет обновляемую запись)
     * @return true, если запись найдена и обновлена; false, если запись не найдена
     */
    boolean update(Album album);

    /**
     * Удаляет альбом по id.
     *
     * @param id идентификатор альбома
     */
    void deleteById(int id);
}
