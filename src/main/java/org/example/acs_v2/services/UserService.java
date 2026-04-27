package org.example.acs_v2.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.acs_v2.exceptions.ResourceNotFoundException;
import org.example.acs_v2.exceptions.UserAlreadyExistsException;
import org.example.acs_v2.models.Department;
import org.example.acs_v2.models.User;
import org.example.acs_v2.models.Role;
import org.example.acs_v2.models.enums.AccessLevel;
import org.example.acs_v2.repositories.DepartmentRepository;
import org.example.acs_v2.repositories.UserRepository;
import org.example.acs_v2.utils.UserFilterHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для управления пользователями
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserFilterHelper userFilterHelper;

    /**
     * Получает пользователя по email
     *
     * @param email email пользователя
     * @return пользователь или null
     */
    public User getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Сохраняет пользователя
     *
     * @param user пользователь для сохранения
     */
    public void saveUser(User user) {
        log.debug("Saving user with ID: {}", user.getId());
        userRepository.save(user);
    }

    /**
     * Предварительная регистрация пользователя (без активации)
     *
     * @param user пользователь для регистрации
     * @throws UserAlreadyExistsException если пользователь с таким email уже существует
     */
    public void preRegistrationUser(User user) {
        log.debug("Pre-registering user with email: {}", user.getEmail());

        if (userRepository.findByEmail(user.getEmail()) != null) {
            log.warn("User with email {} already exists", user.getEmail());
            throw new UserAlreadyExistsException(user.getEmail());
        }

        user.setActive(false);
        user.setApproved(false);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        log.info("Saving new user with email: {}", user.getEmail());
        userRepository.save(user);
        log.info("Pre-registration completed successfully for user: {}", user.getEmail());
    }

    /**
     * Обновляет пользователя с назначением роли и департамента
     *
     * @param user         пользователь для обновления
     * @param role         роль для назначения
     * @param departmentId ID департамента
     * @throws ResourceNotFoundException если департамент не найден
     */
    public void updateUser(User user, String role, Long departmentId) {
        log.debug("Updating user with ID: {}", user.getId());

        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("User or user ID cannot be null");
        }

        // Активируем и одобряем пользователя
        user.setActive(true);
        user.setApproved(true);

        // Обновляем роль пользователя
        updateUserRole(user, role);

        // Назначаем отдел
        if (departmentId != null) {
            Department department = departmentRepository.findById(departmentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Department", departmentId));
            user.setDepartment(department);
        }

        log.info("Updating user with ID: {}", user.getId());
        userRepository.save(user);
        log.info("User updated successfully");
    }

    /**
     * Обновляет роль пользователя
     *
     * @param user пользователь
     * @param role название роли
     */
    private void updateUserRole(User user, String role) {
        user.getRoles().clear();
        try {
            Role roleEnum = Role.valueOf(role);
            user.getRoles().add(roleEnum);
        } catch (IllegalArgumentException e) {
            log.error("Invalid role: {}", role);
            throw new IllegalArgumentException("Invalid role: " + role);
        }
    }

    /**
     * Получает список всех одобренных пользователей
     *
     * @return список пользователей
     */
    public List<User> list() {
        return userRepository.findByApproved(true);
    }

    /**
     * Получает список пользователей для администратора (исключая других администраторов)
     *
     * @return список пользователей
     */
    public List<User> listForAdmin() {
        List<User> approvedUsers = userRepository.findByApproved(true);
        return userFilterHelper.excludeAdmins(approvedUsers);
    }

    /**
     * Получает список пользователей для директора (только из его департамента)
     *
     * @param principal текущий пользователь
     * @return список пользователей
     * @throws ResourceNotFoundException если пользователь не найден
     */
    public List<User> listForDirector(Principal principal) {
        User director = getByEmail(principal.getName());
        if (director == null) {
            throw new ResourceNotFoundException("Пользователь", "email", principal.getName());
        }

        Department directorDepartment = director.getDepartment();
        if (directorDepartment == null) {
            log.warn("Director {} has no department assigned", director.getEmail());
            return List.of();
        }

        List<User> approvedUsers = userRepository.findByApproved(true);
        return userFilterHelper.filterForDirector(approvedUsers, director, directorDepartment);
    }

    /**
     * Изменяет роль пользователя (устаревший метод, используйте updateUserRole)
     *
     * @param user пользователь
     * @param role название роли
     * @deprecated используйте {@link #updateUserRole(User, String)}
     */
    @Deprecated
    public void changeUserRole(User user, String role) {
        updateUserRole(user, role);
        userRepository.save(user);
    }

    /**
     * Получает роль текущего пользователя
     *
     * @param principal текущий пользователь
     * @return название роли или null
     */
    public String getUserRole(Principal principal) {
        if (principal == null) {
            log.warn("Principal is null, user is not authenticated");
            return null;
        }

        User user = getByEmail(principal.getName());
        if (user == null) {
            log.warn("User not found with email: {}", principal.getName());
            return null;
        }

        if (user.getRoles().isEmpty()) {
            log.warn("User with email: {} has no roles assigned", user.getEmail());
            return null;
        }

        String role = user.getRoles().iterator().next().name();
        log.debug("User with email: {} has role: {}", user.getEmail(), role);
        return role;
    }

    /**
     * Получает пользователя по ID
     *
     * @param id ID пользователя
     * @return пользователь или null
     */
    public User getById(Long id) {

        return userRepository.findById(id).orElse(null);

    }

    /**
     * Получает ID текущего пользователя
     *
     * @param principal текущий пользователь
     * @return ID пользователя или null
     */
    public Long getUserId(Principal principal) {
        if (principal == null) {
            log.warn("Principal is null, user is not authenticated");
            return null;
        }

        User user = getByEmail(principal.getName());
        if (user == null) {
            log.warn("User not found with email: {}", principal.getName());
            return null;
        }

        log.debug("User with email: {} has ID: {}", user.getEmail(), user.getId());
        return user.getId();
    }

    /**
     * Получает пользователей по уровню доступа
     *
     * @param userAccessLvl уровень доступа
     * @return список пользователей
     */
    public List<User> getUsersByAccessLvl(AccessLevel userAccessLvl) {
        List<User> users = userRepository.findUsersByUserAccessLvl(userAccessLvl);
        return userFilterHelper.filterApproved(users);
    }

    /**
     * Получает пользователей по имени
     *
     * @param name имя пользователя
     * @return список пользователей
     */
    public List<User> getUsersByName(String name) {
        List<User> users = userRepository.findUsersByName(name);
        return userFilterHelper.filterApproved(users);
    }

    /**
     * Получает пользователей по уровню доступа для директора
     *
     * @param userAccessLvl уровень доступа
     * @param principal     текущий пользователь (директор)
     * @return список пользователей
     */
    public List<User> getUsersByAccessLvlForDirector(AccessLevel userAccessLvl, Principal principal) {
        User director = getByEmail(principal.getName());
        if (director == null) {
            throw new ResourceNotFoundException("Пользователь", "email", principal.getName());
        }

        Department directorDepartment = director.getDepartment();
        if (directorDepartment == null) {
            log.warn("Director {} has no department assigned", director.getEmail());
            return List.of();
        }

        List<User> users = userRepository.findUsersByUserAccessLvl(userAccessLvl);
        return userFilterHelper.filterForDirector(users, director, directorDepartment);
    }

    /**
     * Получает пользователей по имени для директора
     *
     * @param name      имя пользователя
     * @param principal текущий пользователь (директор)
     * @return список пользователей
     */
    public List<User> getUsersByNameForDirector(String name, Principal principal) {
        User director = getByEmail(principal.getName());
        if (director == null) {
            throw new ResourceNotFoundException("Пользователь", "email", principal.getName());
        }

        Department directorDepartment = director.getDepartment();
        if (directorDepartment == null) {
            log.warn("Director {} has no department assigned", director.getEmail());
            return List.of();
        }

        List<User> users = userRepository.findUsersByName(name);
        return userFilterHelper.filterForDirector(users, director, directorDepartment);
    }

    /**
     * Находит директоров без назначенного департамента
     *
     * @return список директоров
     */
    public List<User> findDirectorsWithoutDepartment() {
        return userRepository.findDirectorsWithoutDepartment();
    }

    public List<User> findApprovedUsersWithoutDepartment() {
        return userRepository.findByWithoutDepartmentAndApprovedTrue();
    }

    public List<User> findApprovedUsersByDepartmentId(Long departmentId) {
        return userRepository.findByDepartmentIdAndApprovedTrue(departmentId);
    }

    /**
     * Переключает статус активности пользователя
     *
     * @param userId ID пользователя
     * @throws ResourceNotFoundException если пользователь не найден
     */
    public void toggleUserActiveStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь", userId));

        user.setActive(!user.isActive());
        userRepository.save(user);
        log.info("User {} active status toggled to: {}", userId, user.isActive());
    }

    /**
     * Обновляет данные пользователя (имя, email, пароль)
     *
     * @param id          ID пользователя
     * @param updatedUser обновленные данные
     * @throws ResourceNotFoundException если пользователь не найден
     */
    public void updateUser(Long id, User updatedUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь", id));

        user.setName(updatedUser.getName());
        user.setEmail(updatedUser.getEmail());

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        userRepository.save(user);
        log.info("User {} updated successfully", id);
    }

    /**
     * Обновляет пользователя (общий метод)
     *
     * @param user пользователь для обновления
     */
    public void updateUser(User user) {
        userRepository.save(user);
        log.debug("User {} saved", user.getId());
    }

    /**
     * Получает пользователей по статусу одобрения
     *
     * @param approved статус одобрения
     * @return список пользователей
     */
    public List<User> getUsersWithApprovalStatus(boolean approved) {
        return userRepository.findByApproved(approved);
    }

    /**
     * Находит пользователя по ID
     *
     * @param id ID пользователя
     * @return пользователь или null
     */
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    /**
     * Удаляет пользователя по ID
     *
     * @param id ID пользователя
     * @throws ResourceNotFoundException если пользователь не найден
     */
    public void deleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Пользователь", id);
        }
        userRepository.deleteById(id);
        log.info("User {} deleted successfully", id);
    }

    public void deletePendingUserWithoutFingerprint(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return;
        }
        boolean pending = !user.isApproved() && !user.isActive();
        boolean noFingerprint = user.getFingerprintHash() == null || user.getFingerprintHash().isBlank();
        if (pending && noFingerprint) {
            userRepository.delete(user);
            log.info("Pending user {} deleted after fingerprint registration cancellation", id);
        }
    }
}
