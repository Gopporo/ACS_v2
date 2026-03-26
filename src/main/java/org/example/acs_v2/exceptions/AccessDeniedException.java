package org.example.acs_v2.exceptions;

/**
 * Исключение, выбрасываемое при недостаточных правах доступа
 */
public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(String message) {
        super(message);
    }
}
