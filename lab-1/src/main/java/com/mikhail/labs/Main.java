package com.mikhail.labs;

import com.mikhail.labs.model.Character;
import com.mikhail.labs.repository.CharacterRepository;
import com.mikhail.labs.repository.CsvCharacterRepository;
import com.mikhail.labs.service.CharacterService;

import java.time.LocalDateTime;
import java.util.LinkedList;

public class Main {

    private static final String OUTPUT_PATH = "filtered_characters.csv";

    public static void main(String[] args) {
        // Создаём репозиторий и сервис
        CharacterRepository repository = new CsvCharacterRepository();
        CharacterService service = new CharacterService(repository);

        // ==================== Основное задание ====================
        System.out.println("=== ОСНОВНОЕ ЗАДАНИЕ ===");

        // 1) Чтение данных
        LinkedList<Character> characters = service.findAllCharacters();
        System.out.println("Загружено персонажей: " + characters.size());

        // 2) Обработка данных - удаляем персонажей с неизвестным полом
        service.removeByGenderUnknown(characters);
        System.out.println("После фильтрации (пол ≠ unknown): " + characters.size());

        // 3) Запись результата
        service.saveCharacters(characters, OUTPUT_PATH);
        System.out.println("Сохранено в: " + OUTPUT_PATH);


        System.out.println("\n=== CRUD ОПЕРАЦИИ ===");

        // CREATE - создание нового персонажа
        System.out.println("\n--- CREATE: Добавляем нового персонажа ---");
        Character newCharacter = new Character();
        newCharacter.setId(999);
        newCharacter.setName("Test Character");
        newCharacter.setStatus("Alive");
        newCharacter.setSpecies("Human");
        newCharacter.setType("Test");
        newCharacter.setGender("Male");
        newCharacter.setOriginName("Earth");
        newCharacter.setLocationName("Earth");
        newCharacter.setCreated(LocalDateTime.now());

        Character created = service.createCharacter(newCharacter);
        System.out.println("Создан: " + created);

        // READ - чтение по ID
        System.out.println("\n--- READ: Находим персонажа по ID 1 ---");
        service.findCharacterById(1).ifPresentOrElse(
                c -> System.out.println("Найден: " + c),
                () -> System.out.println("Персонаж с ID 1 не найден")
        );

        // UPDATE - обновление персонажа
        System.out.println("\n--- UPDATE: Обновляем персонажа с ID 1 ---");
        Character updatedData = new Character();
        updatedData.setName("Rick Sanchez (Updated)");
        updatedData.setStatus("Alive");
        updatedData.setSpecies("Human");
        updatedData.setType("");
        updatedData.setGender("Male");
        updatedData.setOriginName("Earth (C-137)");
        updatedData.setLocationName("Citadel of Ricks");
        updatedData.setCreated(LocalDateTime.now());

        service.updateCharacter(1, updatedData).ifPresentOrElse(
                c -> System.out.println("Обновлён: " + c),
                () -> System.out.println("Персонаж для обновления не найден")
        );

        // DELETE - удаление персонажа
        System.out.println("\n--- DELETE: Удаляем персонажа с ID 999 ---");
        boolean deleted = service.deleteCharacter(999);
        System.out.println("Удалён (ID=999): " + deleted);

        // Проверяем, что удаление сработало
        System.out.println("Персонаж с ID 999 после удаления: " +
                service.findCharacterById(999).isPresent());

        System.out.println("\n=== РАБОТА ЗАВЕРШЕНА ===");
    }
}
