package com.mikhail.labs.web;

import com.mikhail.labs.exception.AlbumNotFoundException;
import com.mikhail.labs.exception.RepositoryException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Переводит исключения сервисного слоя в HTTP-статусы.
 */
@RestControllerAdvice
public class AlbumExceptionHandler {

    @ExceptionHandler(AlbumNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(AlbumNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(RepositoryException.class)
    public ResponseEntity<Map<String, String>> handleRepositoryError(RepositoryException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
    }
}
