package org.example.acs_v2.validators;

import org.example.acs_v2.exceptions.AccessDeniedException;
import org.example.acs_v2.models.Application;
import org.example.acs_v2.models.User;
import org.example.acs_v2.models.enums.AccessLevel;
import org.example.acs_v2.services.TemporaryAccessService;
import org.springframework.stereotype.Component;

/**
 * Валидатор для проверки уровней доступа
 */
@Component
public class AccessLevelValidator {

    private final TemporaryAccessService temporaryAccessService;

    public AccessLevelValidator(TemporaryAccessService temporaryAccessService) {
        this.temporaryAccessService = temporaryAccessService;
    }

    private int rank(AccessLevel level) {
        return level == null ? 0 : level.getRank();
    }

    private AccessLevel effectiveAccessLevel(User user) {
        AccessLevel base = user.getUserAccessLvl();
        AccessLevel temp = temporaryAccessService.getMaxActiveTemporaryLevel(user.getId());
        return rank(temp) > rank(base) ? temp : base;
    }

    /**
     * Проверяет, может ли пользователь принять заявку на основе уровня доступа
     *
     * @param user        пользователь
     * @param application заявка
     * @throws AccessDeniedException если уровень доступа недостаточен
     */
    public void validateUserCanAcceptApplication(User user, Application application) {
        AccessLevel effective = effectiveAccessLevel(user);
        if (rank(effective) < rank(application.getAccessLevel())) {
            throw new AccessDeniedException(
                    String.format("Уровень доступа заявки (%s) выше, чем у пользователя (%s). Принятие заявки невозможно.",
                            application.getAccessLevel(), effective)
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
    public boolean hasAccessToZone(AccessLevel userAccessLevel, AccessLevel zoneAccessLevel) {
        return userAccessLevel.getRank() >= zoneAccessLevel.getRank();
    }

    public boolean hasAccessToZone(User user, AccessLevel zoneAccessLevel) {
        return rank(effectiveAccessLevel(user)) >= rank(zoneAccessLevel);
    }
}
