package com.mikhail.labs.repository;

import com.mikhail.labs.model.Character;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Optional;

/**
 * Репозиторий для работы с персонажами в CSV-файле.
 * Поддерживает CRUD-операции и чтение/запись в файл.
 * Использует LinkedList для хранения данных согласно варианту задания.
 */
public class CsvCharacterRepository implements CharacterRepository {

    private static final String CSV_FILE = "/characters.csv";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    /**
     * Хранилище персонажей в памяти.
     * LinkedList сохраняет порядок добавления.
     */
    private final LinkedList<Character> storage = new LinkedList<>();

    public CsvCharacterRepository() {
        loadFromCsv();
    }

    /**
     * Загружает персонажей из CSV-файла в память.
     */
    private void loadFromCsv() {
        try {
            Path csvPath = resolveResourcePath(CSV_FILE);
            storage.clear();

            try (BufferedReader reader = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8)) {
                // Пропускаем заголовок
                reader.readLine();

                String line;
                while ((line = reader.readLine()) != null) {
                    Character character = parseLine(line);
                    storage.add(character);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось загрузить персонажей из CSV", e);
        }
    }

    /**
     * Преобразует путь к ресурсу в Path.
     */
    private Path resolveResourcePath(String resourcePath) throws IOException {
        URL resource = getClass().getResource(resourcePath);
        if (resource == null) {
            throw new IOException("Ресурс не найден: " + resourcePath);
        }
        try {
            return Paths.get(resource.toURI());
        } catch (URISyntaxException e) {
            throw new IOException("Некорректный URI ресурса", e);
        }
    }

    @Override
    public LinkedList<Character> findAll() {
        return new LinkedList<>(storage);
    }

    @Override
    public Optional<Character> findById(int id) {
        return storage.stream()
                .filter(c -> c.getId() == id)
                .findFirst();
    }

    @Override
    public Character save(Character character) {
        // Ищем существующий элемент с таким же ID
        Optional<Character> existing = findById(character.getId());

        if (existing.isPresent()) {
            // Обновляем существующий
            int index = storage.indexOf(existing.get());
            storage.set(index, character);
        } else {
            // Добавляем новый
            storage.add(character);
        }

        persistToCsv();
        return character;
    }

    @Override
    public boolean deleteById(int id) {
        Optional<Character> toDelete = findById(id);

        if (toDelete.isPresent()) {
            storage.remove(toDelete.get());
            persistToCsv();
            return true;
        }
        return false;
    }

    @Override
    public void saveAll(LinkedList<Character> characters, String outputPath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {

            // Записываем заголовок
            writer.write("id,name,status,species,type,gender,origin/name,location/name,created");
            writer.newLine();

            // Записываем персонажей
            for (Character character : characters) {
                writer.write(formatCharacter(character));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось сохранить персонажей в файл: " + outputPath, e);
        }
    }

    /**
     * Сохраняет текущее состояние хранилища обратно в CSV-файл.
     */
    private void persistToCsv() {
        try {
            Path csvPath = resolveResourcePath(CSV_FILE);

            try (BufferedWriter writer = Files.newBufferedWriter(csvPath, StandardCharsets.UTF_8)) {
                writer.write("id,name,status,species,type,gender,origin/name,location/name,created");
                writer.newLine();

                for (Character character : storage) {
                    writer.write(formatCharacter(character));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось сохранить изменения в CSV", e);
        }
    }

    /**
     * Форматирует персонажа в строку CSV.
     */
    private String formatCharacter(Character c) {
        return String.format("%d,%s,%s,%s,%s,%s,%s,%s,%s",
                c.getId(),
                escapeField(c.getName()),
                escapeField(c.getStatus()),
                escapeField(c.getSpecies()),
                escapeField(c.getType()),
                escapeField(c.getGender()),
                escapeField(c.getOriginName()),
                escapeField(c.getLocationName()),
                c.getCreated() != null ? c.getCreated().atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT) : "");
    }

    /**
     * Экранирует поле для CSV (если содержит запятые или кавычки).
     */
    private String escapeField(String field) {
        if (field == null) return "";
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }

    /**
     * Парсит строку CSV в объект Character.
     */
    private Character parseLine(String line) {
        String[] parts = splitCsvLine(line);

        Character character = new Character();
        character.setId(Integer.parseInt(parts[0].trim()));
        character.setName(parts[1].trim());
        character.setStatus(parts[2].trim());
        character.setSpecies(parts[3].trim());
        character.setType(parts[4].trim());
        character.setGender(parts[5].trim());
        character.setOriginName(parts[6].trim());
        character.setLocationName(parts[7].trim());

        String createdStr = parts[8].trim();
        if (!createdStr.isEmpty()) {
            character.setCreated(LocalDateTime.parse(createdStr, FORMATTER));
        }

        return character;
    }

    /**
     * Разбивает CSV-строку на поля с учётом экранированных значений.
     */
    private String[] splitCsvLine(String line) {
        LinkedList<String> fields = new LinkedList<>();
        StringBuilder field = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    // Экранированная кавычка ""
                    field.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                fields.add(field.toString());
                field = new StringBuilder();
            } else {
                field.append(c);
            }
        }
        fields.add(field.toString());

        return fields.toArray(new String[0]);
    }
}
