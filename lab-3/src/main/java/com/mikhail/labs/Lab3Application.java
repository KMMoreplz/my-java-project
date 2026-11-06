package com.mikhail.labs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Точка входа REST-приложения лабораторной работы №3.
 * Управляет веб-сервером и собирает бины из {@link com.mikhail.labs.config.AppConfig}.
 */
@SpringBootApplication
public class Lab3Application {

    public static void main(String[] args) {
        SpringApplication.run(Lab3Application.class, args);
    }
}
