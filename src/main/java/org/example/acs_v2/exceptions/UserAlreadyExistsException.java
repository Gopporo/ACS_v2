package org.example.acs_v2.exceptions;

/**
 * Исключение, выбрасываемое когда пользователь уже существует
 */
public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String email) {
        super(String.format("Пользователь с email %s уже существует", email));
    }
}
