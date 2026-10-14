package com.mikhail.labs.exception;

/**
 * Исключение уровня работы с базой данных.
 * Пробрасывается выше, чтобы вызывающий код мог обработать ошибку,
 * вместо молчаливого возврата дефолтных значений.
 */
public class RepositoryException extends RuntimeException {

    public RepositoryException(String message) {
        super(message);
    }

    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
