package org.example.acs_v2.utils;

import org.example.acs_v2.models.Department;
import org.example.acs_v2.models.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Вспомогательный класс для фильтрации пользователей
 */
@Component
public class UserFilterHelper {

    /**
     * Исключает администраторов из списка пользователей
     */
    public List<User> excludeAdmins(List<User> users) {
        return users.stream()
                .filter(user -> user.getRoles().stream()
                        .noneMatch(role -> role.getAuthority().equals("ROLE_ADMIN")))
                .collect(Collectors.toList());
    }

    /**
     * Фильтрует пользователей по департаменту
     */
    public List<User> filterByDepartment(List<User> users, Department department) {
        return users.stream()
                .filter(user -> user.getDepartment() != null && user.getDepartment().equals(department))
                .collect(Collectors.toList());
    }

    /**
     * Исключает конкретного пользователя из списка
     */
    public List<User> excludeUser(List<User> users, User userToExclude) {
        return users.stream()
                .filter(user -> !user.getId().equals(userToExclude.getId()))
                .collect(Collectors.toList());
    }

    /**
     * Фильтрует только одобренных пользователей
     */
    public List<User> filterApproved(List<User> users) {
        return users.stream()
                .filter(User::isApproved)
                .collect(Collectors.toList());
    }

    /**
     * Комплексная фильтрация для директора:
     * - исключает администраторов
     * - фильтрует по департаменту
     * - исключает самого директора
     */
    public List<User> filterForDirector(List<User> users, User director, Department department) {
        return users.stream()
                .filter(User::isApproved)
                .filter(user -> user.getRoles().stream()
                        .noneMatch(role -> role.getAuthority().equals("ROLE_ADMIN")))
                .filter(user -> !user.getId().equals(director.getId()))
                .filter(user -> user.getDepartment() != null && user.getDepartment().equals(department))
                .collect(Collectors.toList());
    }
}
