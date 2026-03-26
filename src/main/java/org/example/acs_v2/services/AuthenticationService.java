package org.example.acs_v2.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.acs_v2.exceptions.ResourceNotFoundException;
import org.example.acs_v2.models.User;
import org.example.acs_v2.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.security.Principal;

/**
 * Сервис для работы с аутентификацией и получением данных текущего пользователя
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;

    /**
     * Получает текущего аутентифицированного пользователя
     *
     * @param principal объект Principal
     * @return пользователь
     * @throws ResourceNotFoundException если пользователь не найден
     */
    public User getCurrentUser(Principal principal) {
        if (principal == null) {
            log.warn("Principal is null, user is not authenticated");
            throw new ResourceNotFoundException("Пользователь не аутентифицирован");
        }

        String email = principal.getName();
        User user = userRepository.findByEmail(email);

        if (user == null) {
            log.warn("User not found with email: {}", email);
            throw new ResourceNotFoundException("Пользователь", "email", email);
        }

        return user;
    }

    /**
     * Получает ID текущего пользователя
     *
     * @param principal объект Principal
     * @return ID пользователя
     */
    public Long getCurrentUserId(Principal principal) {
        User user = getCurrentUser(principal);
        log.debug("User with email: {} has ID: {}", user.getEmail(), user.getId());
        return user.getId();
    }

    /**
     * Получает роль текущего пользователя
     *
     * @param principal объект Principal
     * @return название роли
     */
    public String getCurrentUserRole(Principal principal) {
        User user = getCurrentUser(principal);

        if (user.getRoles().isEmpty()) {
            log.warn("User with email: {} has no roles assigned", user.getEmail());
            return null;
        }

        String role = user.getRoles().iterator().next().name();
        log.debug("User with email: {} has role: {}", user.getEmail(), role);
        return role;
    }

    /**
     * Проверяет, является ли текущий пользователь администратором
     *
     * @param principal объект Principal
     * @return true если пользователь - администратор
     */
    public boolean isCurrentUserAdmin(Principal principal) {
        User user = getCurrentUser(principal);
        return user.isAdmin();
    }

    /**
     * Проверяет, является ли текущий пользователь директором
     *
     * @param principal объект Principal
     * @return true если пользователь - директор
     */
    public boolean isCurrentUserDirector(Principal principal) {
        User user = getCurrentUser(principal);
        return user.isDirector();
    }
}
