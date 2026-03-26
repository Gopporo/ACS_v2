package org.example.acs_v2.configurations;

import lombok.extern.slf4j.Slf4j;
import org.example.acs_v2.exceptions.AccessDeniedException;
import org.example.acs_v2.exceptions.ResourceNotFoundException;
import org.example.acs_v2.exceptions.UserAlreadyExistsException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.example.acs_v2.constants.ModelAttributeConstants.ERROR_MESSAGE;
import static org.example.acs_v2.constants.ViewConstants.ERROR;
import static org.example.acs_v2.constants.ViewConstants.ERROR_403;
import static org.example.acs_v2.constants.ViewConstants.REGISTRATION;

/**
 * Глобальный обработчик доменных исключений.
 * Позволяет показывать пользователю корректную страницу без дублирования try/catch в контроллерах.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFound(ResourceNotFoundException e, Model model) {
        log.warn("Resource not found: {}", e.getMessage());
        model.addAttribute(ERROR_MESSAGE, e.getMessage());
        return ERROR;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDenied(AccessDeniedException e, Model model) {
        log.warn("Access denied: {}", e.getMessage());
        model.addAttribute(ERROR_MESSAGE, e.getMessage());
        return ERROR_403;
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public String handleUserAlreadyExists(UserAlreadyExistsException e, Model model) {
        log.warn("User already exists: {}", e.getMessage());
        model.addAttribute(ERROR_MESSAGE, e.getMessage());
        return REGISTRATION;
    }

    @ExceptionHandler(Exception.class)
    public String handleUnexpected(Exception e, Model model) {
        log.error("Unexpected error", e);
        model.addAttribute(ERROR_MESSAGE, "Произошла непредвиденная ошибка. Попробуйте позже.");
        return ERROR;
    }
}

