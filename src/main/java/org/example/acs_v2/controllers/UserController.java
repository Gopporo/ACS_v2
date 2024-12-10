package org.example.acs_v2.controllers;

import lombok.RequiredArgsConstructor;
import org.example.acs_v2.models.User;
import org.example.acs_v2.services.UserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/login")
    public String login(Model model, String error, String logout) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            System.out.println("Перенаправляю на главную страницу");
            return "redirect:/index";
        }
        if (error != null) {
            model.addAttribute("errorMessage", "Your username and password are invalid.");
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully.");
        }
        return "login";
    }

    @GetMapping("/registration")
    public String registration() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            System.out.println("Перенаправляю на главную страницу");
            return "redirect:/index";
        }
        return "registration";
    }

    @PostMapping("/registration")
    public String createUser(User user, Model model) {
        if (!userService.preRegistrationUser(user)) {
            model.addAttribute("errorMessage", "Пользователь с email: " + user.getEmail() + " уже существует");
            return "registration";
        }
        return "redirect:/login";
    }

    @GetMapping("/user/{user}")
    public String userInfo(@PathVariable("user") User user, Model model, Principal principal) {
        model.addAttribute("user", user);
        model.addAttribute("userId", userService.getUserId(principal));
        model.addAttribute("role", userService.getUserRole(principal));
        return "user-info";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return "redirect:/login?logout";
    }

    @GetMapping("/user/profile/{user}")
    public String userProfileInfo(@PathVariable("user") User user, Model model, Principal principal) {
        model.addAttribute("user", user);
        model.addAttribute("userId", userService.getUserId(principal));
        model.addAttribute("role", userService.getUserRole(principal));
        return "profile";
    }

    @GetMapping("/user/edit/{id}")
    public String getUserEditPage(@PathVariable Long id, Model model, Principal principal) {
        User user = userService.getById(id);
        model.addAttribute("user", user);
        model.addAttribute("userId", userService.getUserId(principal));
        model.addAttribute("role", userService.getUserRole(principal));
        return "profile-edit";
    }

    @PostMapping("/user/edit/{id}")
    public String editUser(@PathVariable Long id, @ModelAttribute User user, RedirectAttributes redirectAttributes) {
        try {
            userService.updateUser(id, user);
            redirectAttributes.addFlashAttribute("successMessage", "Данные успешно обновлены!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при обновлении данных: " + e.getMessage());
        }
        return "redirect:/logout";
    }

}
