package com.mikhail.labs.exception;

public class AlbumNotFoundException extends RuntimeException {

    public AlbumNotFoundException(String message) {
        super(message);
    }
}
