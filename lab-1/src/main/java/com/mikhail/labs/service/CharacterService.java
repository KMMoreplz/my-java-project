package com.mikhail.labs.service;

import com.mikhail.labs.model.Character;
import com.mikhail.labs.repository.CharacterRepository;

import java.util.LinkedList;
import java.util.Optional;

public class CharacterService {

    private final CharacterRepository repository;

    public CharacterService(CharacterRepository repository) {
        this.repository = repository;
    }

    /**
     * Получает всех персонажей.
     *
     * @return список всех персонажей
     */
    public LinkedList<Character> findAllCharacters() {
        return repository.findAll();
    }

    /**
     * Находит персонажа по ID.
     *
     * @param id идентификатор персонажа
     * @return Optional с персонажем или пустой
     */
    public Optional<Character> findCharacterById(int id) {
        return repository.findById(id);
    }

    /**
     * Удаляет всех персонажей с неизвестным полом (unknown).
     *
     * @param characters список для фильтрации
     */
    public void removeByGenderUnknown(LinkedList<Character> characters) {
        characters.removeIf(c -> "unknown".equals(c.getGender()));
    }

    /**
     * Сохраняет персонажей в указанный файл.
     *
     * @param characters список персонажей
     * @param path       путь к файлу
     */
    public void saveCharacters(LinkedList<Character> characters, String path) {
        repository.saveAll(characters, path);
    }

    // ==================== CRUD операции ====================

    /**
     * Создаёт нового персонажа.
     *
     * @param character персонаж для создания
     * @return созданный персонаж
     */
    public Character createCharacter(Character character) {
        return repository.save(character);
    }

    /**
     * Обновляет существующего персонажа.
     *
     * @param id              ID персонажа для обновления
     * @param updatedCharacter персонаж с новыми данными
     * @return Optional с обновлённым персонажем или пустой, если не найден
     */
    public Optional<Character> updateCharacter(int id, Character updatedCharacter) {
        Optional<Character> existing = repository.findById(id);
        if (existing.isEmpty()) {
            return Optional.empty();
        }

        updatedCharacter.setId(id);
        repository.save(updatedCharacter);
        return Optional.of(updatedCharacter);
    }

    /**
     * Удаляет персонажа по ID.
     *
     * @param id идентификатор персонажа
     * @return true если персонаж удалён, false если не найден
     */
    public boolean deleteCharacter(int id) {
        return repository.deleteById(id);
    }
}
