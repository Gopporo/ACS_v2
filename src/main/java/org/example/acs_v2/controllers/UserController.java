package org.example.acs_v2.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.acs_v2.exceptions.UserAlreadyExistsException;
import org.example.acs_v2.dto.UserRegistrationDto;
import org.example.acs_v2.dto.UserUpdateDto;
import org.example.acs_v2.models.User;
import org.example.acs_v2.services.UserService;
import org.example.acs_v2.utils.ModelAttributeHelper;
import org.example.acs_v2.tcp.TcpFingerprintServer;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.validation.BindingResult;

import javax.validation.Valid;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.security.Principal;

import static org.example.acs_v2.constants.ModelAttributeConstants.*;
import static org.example.acs_v2.constants.RedirectConstants.*;
import static org.example.acs_v2.constants.ViewConstants.*;

/**
 * Контроллер для управления пользователями (регистрация, логин, профиль)
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final ModelAttributeHelper modelAttributeHelper;
    private final TcpFingerprintServer tcpFingerprintServer;

    /**
     * Проверяет, аутентифицирован ли пользователь
     */
    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }

    /**
     * Страница входа
     */
    @GetMapping("/login")
    public String login(Model model, String error, String logout) {
        if (isAuthenticated()) {
            log.debug("User already authenticated, redirecting to index");
            return REDIRECT_INDEX;
        }
        if (error != null) {
            model.addAttribute(ERROR_MESSAGE, "Неверный email или пароль.");
        }
        if (logout != null) {
            model.addAttribute(MESSAGE, "Вы успешно вышли из системы.");
        }

        return LOGIN;
    }

    /**
     * Страница регистрации
     */
    @GetMapping("/registration")
    public String registration() {
        if (isAuthenticated()) {
            log.debug("User already authenticated, redirecting to index");
            return REDIRECT_INDEX;
        }
        return REGISTRATION;
    }

    /**
     * Обработка регистрации нового пользователя
     */
    @PostMapping("/registration")
    public String createUser(@Valid @ModelAttribute UserRegistrationDto dto,
                               BindingResult bindingResult,
                               Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(ERROR_MESSAGE, bindingResult.getAllErrors().get(0).getDefaultMessage());
            return REGISTRATION;
        }

        try {
            User user = new User();
            user.setEmail(dto.getEmail());
            user.setPassword(dto.getPassword());
            user.setName(dto.getName());
            user.setNumber_phone(dto.getNumber_phone());

            userService.preRegistrationUser(user);
            log.info("User pre-registered successfully: {}", user.getEmail());

            // Стартуем регистрацию отпечатка на TCP-сервере.
            User savedUser = userService.getByEmail(user.getEmail());
            if (savedUser != null) {
                tcpFingerprintServer.enableRegisterMode(savedUser);
                return "redirect:/registration/fingerprint?userId=" + savedUser.getId();
            }

            return REDIRECT_LOGIN;
        } catch (UserAlreadyExistsException e) {
            log.warn("Registration failed: {}", e.getMessage());
            model.addAttribute(ERROR_MESSAGE, e.getMessage());
            return REGISTRATION;
        }
    }

    @GetMapping("/registration/fingerprint")
    public String registrationFingerprintPage(@RequestParam Long userId, Model model) {
        model.addAttribute("userId", userId);
        return "registration-fingerprint";
    }

    @GetMapping(value = "/registration/fingerprint/status", produces = "application/json")
    @ResponseBody
    public Map<String, Object> registrationFingerprintStatus(@RequestParam Long userId) {
        User user = userService.getById(userId);
        TcpFingerprintServer.RegistrationState state = tcpFingerprintServer.getRegistrationState(userId);

        if (state == TcpFingerprintServer.RegistrationState.IDLE) {
            boolean hasFingerprint = user != null
                    && user.getFingerprintHash() != null
                    && !user.getFingerprintHash().isBlank();
            if (hasFingerprint) {
                return Map.of(
                        "state", TcpFingerprintServer.RegistrationState.SUCCESS.name(),
                        "done", true,
                        "ok", true,
                        "message", "Отпечаток сохранён"
                );
            }
        }

        boolean done = state == TcpFingerprintServer.RegistrationState.SUCCESS
                || state == TcpFingerprintServer.RegistrationState.ERROR
                || state == TcpFingerprintServer.RegistrationState.CANCELLED;
        boolean ok = state == TcpFingerprintServer.RegistrationState.SUCCESS;
        String message = tcpFingerprintServer.getRegistrationMessage(userId);

        return Map.of(
                "state", state.name(),
                "done", done,
                "ok", ok,
                "message", message
        );
    }

    @PostMapping(value = "/registration/fingerprint/retry", produces = "application/json")
    @ResponseBody
    public Map<String, Object> retryFingerprintRegistration(@RequestParam Long userId) {
        User user = userService.getById(userId);
        if (user == null) {
            return Map.of("ok", false, "message", "Пользователь не найден");
        }
        tcpFingerprintServer.enableRegisterMode(user);
        return Map.of("ok", true, "message", "Повторное сканирование запущено");
    }

    @PostMapping("/registration/fingerprint/cancel")
    public String cancelFingerprintRegistration(@RequestParam Long userId) {
        tcpFingerprintServer.cancelRegisterMode();
        userService.deletePendingUserWithoutFingerprint(userId);
        return "redirect:/registration";
    }

    /**
     * Информация о пользователе
     */
    @GetMapping("/user/{user}")
    public String userInfo(@PathVariable("user") User user, Model model, Principal principal) {
        model.addAttribute(USER, user);
        modelAttributeHelper.addUserAttributes(model, principal);
        return USER_INFO;
    }

    /**
     * Выход из системы
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
            log.info("User logged out: {}", authentication.getName());
        }
        return REDIRECT_LOGIN_LOGOUT;
    }

    /**
     * Профиль пользователя
     */
    @GetMapping("/user/profile/{user}")
    public String userProfileInfo(@PathVariable("user") User user, Model model, Principal principal) {
        model.addAttribute(USER, user);
        modelAttributeHelper.addUserAttributes(model, principal);
        return PROFILE;
    }

    /**
     * Страница редактирования профиля
     */
    @GetMapping("/user/edit/{id}")
    public String getUserEditPage(@PathVariable Long id, Model model, Principal principal) {
        User user = userService.getById(id);
        model.addAttribute(USER, user);
        modelAttributeHelper.addUserAttributes(model, principal);
        return PROFILE_EDIT;
    }

    /**
     * Обработка редактирования профиля
     */
    @PostMapping("/user/edit/{id}")
    public String editUser(@PathVariable Long id,
                            @Valid @ModelAttribute UserUpdateDto dto,
                            BindingResult bindingResult,
                            Principal principal,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            User user = userService.getById(id);
            if (user == null) {
                // пусть дальше отработает глобальный обработчик
                throw new org.example.acs_v2.exceptions.ResourceNotFoundException("Пользователь", id);
            }
            model.addAttribute(USER, user);
            modelAttributeHelper.addUserAttributes(model, principal);
            model.addAttribute(ERROR_MESSAGE, bindingResult.getAllErrors().get(0).getDefaultMessage());
            return PROFILE_EDIT;
        }

        User user = userService.getById(id);
        if (user == null) {
            throw new org.example.acs_v2.exceptions.ResourceNotFoundException("Пользователь", id);
        }

        try {
            // Пароль может быть пустым (не меняем) — это уже учитывает UserService.updateUser(...)
            user.setName(dto.getName());
            user.setEmail(dto.getEmail());
            user.setPassword(dto.getPassword());

            userService.updateUser(id, user);
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Данные успешно обновлены!");
            log.info("User {} profile updated successfully", id);
        } catch (Exception e) {
            log.error("Error updating user profile: {}", e.getMessage());
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Ошибка при обновлении данных: " + e.getMessage());
        }
        return REDIRECT_LOGOUT;
    }

}
