package org.example.acs_v2.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.acs_v2.models.Department;
import org.example.acs_v2.models.User;
import org.example.acs_v2.models.enums.Role;
import org.example.acs_v2.repositories.DepartmentRepository;
import org.example.acs_v2.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    @Autowired
    DepartmentRepository departmentRepository;
    @Autowired
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User getByEmail(String email) { return userRepository.findByEmail(email); }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public boolean createUser(User user, String role, Long departmentId) {
        System.out.println("Сервис вызвал");
        String userEmail = user.getEmail();
        if (userRepository.findByEmail(userEmail) != null)
            return false;
        System.out.println("Почту проверил");
        user.setActive(true);
        if (role.equals("ROLE_USER")) {
            user.getRoles().add(Role.ROLE_USER);
            if (departmentId != null) {
                Department department = departmentRepository.findById(departmentId).orElseThrow(() ->
                        new RuntimeException("Department with ID " + departmentId + " not found"));

                user.setDepartment(department);
            }
        } else user.getRoles().add(Role.ROLE_DIRECTOR);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        System.out.println(user.getUserAccessLvl());
        System.out.println("Пароль зашифровал");
        log.info("Saving new User with email: {}", userEmail);

        // Назначаем главу отдела
        if (departmentId != null) {
            Department department = departmentRepository.findById(departmentId).orElseThrow(() ->
                    new RuntimeException("Department with ID " + departmentId + " not found"));

            user.setDepartment(department);
        }


        userRepository.save(user);
        System.out.println("Пользователя сохранил");
        return true;
    }

    public List<User> list() {
        return userRepository.findAll();
    }

    public List<User> listForAdmin() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRoles().stream()
                        .noneMatch(role -> role.getAuthority().equals("ROLE_ADMIN")))
                .collect(Collectors.toList());
    }

    public List<User> listForDirector(Principal principal) {
        // Получаем отдел, в котором работает начальник
        User user1 = userRepository.findByEmail(principal.getName());
        Department adminDepartment = user1.getDepartment();

        return userRepository.findAll().stream()
                .filter(user -> user.getRoles().stream()
                        .noneMatch(role -> role.getAuthority().equals("ROLE_ADMIN"))) // Исключаем администраторов
                .filter(user -> user.getDepartment().equals(adminDepartment)) // Оставляем только сотрудников из того же отдела
                .filter(user -> !user.getId().equals(user1.getId())) // Исключаем самого начальника
                .collect(Collectors.toList());
    }



    /*public void banUser(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            if (user.isActive()) {
                user.setActive(false);
                log.info("Ban user with id = {}; email: {}", user.getId(), user.getEmail());
            } else {
                user.setActive(true);
                log.info("Unban user with id = {}; email: {}", user.getId(), user.getEmail());
            }
        }
        userRepository.save(user);
    }*/

    public void changeUserRoles(User user, Map<String, String> form) {
        // Extract the new role from the form
        String newRole = form.get("role");

        // Check if the new role is different from the current role
        if (user.getRoles().stream().noneMatch(role -> role.name().equals(newRole))) {
            // Clear existing roles and set the new role
            user.getRoles().clear();
            user.getRoles().add(Role.valueOf(newRole));
            userRepository.save(user);
        }
    }

    public String getUserRole(Principal principal) {
        if (principal == null) {
            log.warn("Principal is null, user is not authenticated");
            return null;
        }

        String email = principal.getName();
        User user = userRepository.findByEmail(email);

        if (user == null) {
            log.warn("User not found with email: {}", email);
            return null;
        }

        if (user.getRoles().isEmpty()) {
            log.warn("User with email: {} has no roles assigned", email);
            return null;
        }

        String role = user.getRoles().iterator().next().name();
        log.info("User with email: {} has role: {}", email, role);
        return role;
    }

    public User getById(Long id) {

        return userRepository.findById(id).orElse(null);

    }

    public Long getUserId(Principal principal) {
        if (principal == null) {
            log.warn("Principal is null, user is not authenticated");
            return null;
        }

        String email = principal.getName();
        User user = userRepository.findByEmail(email);

        if (user == null) {
            log.warn("User not found with email: {}", email);
            return null;
        }

        Long userId = user.getId();
        log.info("User with email: {} has ID: {}", email, userId);
        return userId;
    }

    public List<User> getUsersByAccessLvl(int userAccessLvl) {
        return userRepository.findUsersByUserAccessLvl(userAccessLvl);
    }

    public List<User> getUsersByName(String name) {
        return userRepository.findUsersByName(name);
    }

    public List<User> getUsersByAccessLvlForDirector(int userAccessLvl, Principal principal) {
        User director = userRepository.findByEmail(principal.getName());
        Department directorDepartment = director.getDepartment();

        return userRepository.findUsersByUserAccessLvl(userAccessLvl).stream()
                .filter(user -> !user.getRoles().stream()
                        .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"))) // Исключаем администраторов
                .filter(user -> !user.getId().equals(director.getId())) // Исключаем самого директора
                .filter(user -> user.getDepartment().equals(directorDepartment)) // Оставляем только сотрудников из того же отдела
                .collect(Collectors.toList());
    }


    public List<User> getUsersByNameForDirector(String name, Principal principal) {
        User director = userRepository.findByEmail(principal.getName());
        Department directorDepartment = director.getDepartment();

        return userRepository.findUsersByName(name).stream()
                .filter(user -> !user.getRoles().stream()
                        .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"))) // Исключаем администраторов
                .filter(user -> !user.getId().equals(director.getId())) // Исключаем самого директора
                .filter(user -> user.getDepartment().equals(directorDepartment)) // Оставляем только сотрудников из того же отдела
                .collect(Collectors.toList());
    }


    public List<User> findDirectorsWithoutDepartment(){
        return userRepository.findDirectorsWithoutDepartment();
    }

    public void toggleUserActiveStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с ID " + userId + " не найден"));

        user.setActive(!user.isActive());
        userRepository.save(user);
    }

    public void updateUser(Long id, User user1) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        user.setName(user1.getName());
        user.setEmail(user1.getEmail());
        if (user1.getPassword() != null && !user1.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user1.getPassword())); // Хеширование пароля
        }
        userRepository.save(user);
    }

    public void updateUser(User user) {
        userRepository.save(user); // Сохраняем пользователя с обновленными ролями
    }
}