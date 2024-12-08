package org.example.acs_v2.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.acs_v2.models.User;
import org.example.acs_v2.models.enums.Role;
import org.example.acs_v2.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean createUser(User user, String role) {
        System.out.println("Сервис вызвал");
        String userEmail = user.getEmail();
        if (userRepository.findByEmail(userEmail) != null)
            return false;
        System.out.println("Почту проверил");
        user.setActive(true);
        if (role.equals("ROLE_USER")) {
            user.getRoles().add(Role.ROLE_USER);
        } else user.getRoles().add(Role.ROLE_DIRECTOR);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        System.out.println("Пароль зашифровал");
        log.info("Saving new User with email: {}", userEmail);
        userRepository.save(user);
        System.out.println("Пользователя сохранил");
        return true;
    }

    public List<User> list() {
        return userRepository.findAll();
    }

    public void banUser(Long id) {
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
    }

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

    public User getUserById(Long id) {

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
}