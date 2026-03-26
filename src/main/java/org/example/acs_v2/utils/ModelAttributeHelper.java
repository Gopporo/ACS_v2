package org.example.acs_v2.utils;

import org.example.acs_v2.services.UserService;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.security.Principal;

import static org.example.acs_v2.constants.ModelAttributeConstants.ERROR_MESSAGE;
import static org.example.acs_v2.constants.ModelAttributeConstants.SUCCESS_MESSAGE;
import static org.example.acs_v2.constants.ModelAttributeConstants.ROLE;
import static org.example.acs_v2.constants.ModelAttributeConstants.USER_ID;

/**
 * Вспомогательный класс для добавления общих атрибутов в модель
 */
@Component
public class ModelAttributeHelper {

    private final UserService userService;

    public ModelAttributeHelper(UserService userService) {
        this.userService = userService;
    }

    /**
     * Добавляет стандартные атрибуты пользователя в модель (userId и role)
     *
     * @param model     модель для добавления атрибутов
     * @param principal текущий аутентифицированный пользователь
     */
    public void addUserAttributes(Model model, Principal principal) {
        if (principal != null) {
            model.addAttribute(USER_ID, userService.getUserId(principal));
            model.addAttribute(ROLE, userService.getUserRole(principal));
        }
    }

    /**
     * Добавляет общие атрибуты в модель (alias для addUserAttributes)
     *
     * @param model     модель для добавления атрибутов
     * @param principal текущий аутентифицированный пользователь
     */
    public void addCommonAttributes(Model model, Principal principal) {
        addUserAttributes(model, principal);
    }

    /**
     * Добавляет атрибут с сообщением об ошибке
     *
     * @param model   модель
     * @param message сообщение об ошибке
     */
    public void addErrorMessage(Model model, String message) {
        model.addAttribute(ERROR_MESSAGE, message);
    }

    /**
     * Добавляет атрибут с сообщением об успехе
     *
     * @param model   модель
     * @param message сообщение об успехе
     */
    public void addSuccessMessage(Model model, String message) {
        model.addAttribute(SUCCESS_MESSAGE, message);
    }
}
