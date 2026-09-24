package com.mikhail.labs.repository;

import com.mikhail.labs.model.Character;

import java.util.LinkedList;
import java.util.Optional;

/**
 * Интерфейс репозитория для работы с персонажами.
 * Определяет CRUD-операции и основные методы поиска.
 */
public interface CharacterRepository {

    /**
     * Находит все персонажей в репозитории.
     *
     * @return список всех персонажей
     */
    LinkedList<Character> findAll();

    /**
     * Находит персонажа по ID.
     *
     * @param id идентификатор персонажа
     * @return Optional с персонажем или пустой, если не найден
     */
    Optional<Character> findById(int id);

    /**
     * Сохраняет персонажа. Если персонаж с таким ID уже существует - обновляет,
     * иначе создаёт нового.
     *
     * @param character персонаж для сохранения
     * @return сохранённый персонаж
     */
    Character save(Character character);

    /**
     * Удаляет персонажа по ID.
     *
     * @param id идентификатор персонажа
     * @return true если персонаж был удалён, false если не найден
     */
    boolean deleteById(int id);

    /**
     * Сохраняет всех персонажей в файл (для основного задания).
     *
     * @param characters список персонажей
     * @param outputPath путь к выходному файлу
     */
    void saveAll(LinkedList<Character> characters, String outputPath);
}
