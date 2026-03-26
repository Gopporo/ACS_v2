package org.example.acs_v2.validators;

import org.example.acs_v2.exceptions.AccessDeniedException;
import org.example.acs_v2.models.Application;
import org.example.acs_v2.models.User;
import org.springframework.stereotype.Component;

/**
 * Валидатор для проверки уровней доступа
 */
@Component
public class AccessLevelValidator {

    /**
     * Проверяет, может ли пользователь принять заявку на основе уровня доступа
     *
     * @param user        пользователь
     * @param application заявка
     * @throws AccessDeniedException если уровень доступа недостаточен
     */
    public void validateUserCanAcceptApplication(User user, Application application) {
        if (application.getAccessLevel() > user.getUserAccessLvl()) {
            throw new AccessDeniedException(
                    String.format("Уровень доступа заявки (%d) выше, чем у пользователя (%d). Принятие заявки невозможно.",
                            application.getAccessLevel(), user.getUserAccessLvl())
            );
        }
    }

    /**
     * Проверяет, имеет ли пользователь доступ к зоне
     *
     * @param userAccessLevel уровень доступа пользователя
     * @param zoneAccessLevel требуемый уровень доступа зоны
     * @return true если доступ разрешен
     */
    public boolean hasAccessToZone(int userAccessLevel, int zoneAccessLevel) {
        return userAccessLevel >= zoneAccessLevel;
    }
}
