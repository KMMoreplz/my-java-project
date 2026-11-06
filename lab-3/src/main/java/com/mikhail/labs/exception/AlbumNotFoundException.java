package com.mikhail.labs.exception;

/**
 * Выбрасывается сервисным слоем, когда альбом не обнаружен в базе данных.
 */
public class AlbumNotFoundException extends RuntimeException {

    public AlbumNotFoundException(String message) {
        super(message);
    }
}
