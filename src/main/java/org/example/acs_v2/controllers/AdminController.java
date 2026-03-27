package org.example.acs_v2.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.acs_v2.exceptions.ResourceNotFoundException;
import org.example.acs_v2.models.Department;
import org.example.acs_v2.models.User;
import org.example.acs_v2.models.Zone;
import org.example.acs_v2.models.Role;
import org.example.acs_v2.models.enums.AccessLevel;
import org.example.acs_v2.repositories.UserRepository;
import org.example.acs_v2.services.DepartmentService;
import org.example.acs_v2.services.UserService;
import org.example.acs_v2.services.ZoneService;
import org.example.acs_v2.utils.ModelAttributeHelper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import static org.example.acs_v2.constants.ModelAttributeConstants.*;
import static org.example.acs_v2.constants.RedirectConstants.*;
import static org.example.acs_v2.constants.ViewConstants.*;

/**
 * Контроллер для функционала администратора
 */
@Controller
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@Slf4j
public class AdminController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final ZoneService zoneService;
    private final DepartmentService departmentService;
    private final ModelAttributeHelper modelAttributeHelper;

    private AccessLevel parseAccessLevel(String value) {
        if (value == null || value.isBlank() || "-1".equals(value)) {
            return null;
        }
        return AccessLevel.valueOf(value.toUpperCase());
    }

    /**
     * Список пользователей для администратора
     */
    @GetMapping("/admin/users")
    public String manageUsers(Model model, Principal principal) {
        List<User> users = userService.listForAdmin();
        model.addAttribute(USERS, users);
        modelAttributeHelper.addUserAttributes(model, principal);
        return ADMIN_USERS;
    }

    /**
     * Страница добавления пользователя
     */
    @GetMapping("/admin/addUser/{id}")
    public String addUserMethod(@PathVariable Long id, Principal principal, Model model) {

        User user = userService.getById(id);

        if (user == null) {
            log.warn("User with ID {} not found", id);
            return REDIRECT_ADMIN_USERS;
        }

        List<Department> departments = departmentService.list();
        model.addAttribute(USER, user);
        model.addAttribute(DEPARTMENTS, departments);
        modelAttributeHelper.addUserAttributes(model, principal);

        return ADD_USER;
    }

    /**
     * Создание/обновление пользователя
     */
    @PostMapping("/admin/addUser")
    public String createUser(@RequestParam String role,
                             @RequestParam Long department_id,
                             @RequestParam String position,
                             @RequestParam String userAccessLvl,
                             @RequestParam Long userId,
                             Model model) {
        try {
            User user = userService.findById(userId);

            if (user == null) {
                log.warn("User with ID {} not found", userId);
                model.addAttribute(ERROR_MESSAGE, "Пользователь с ID: " + userId + " не найден");
                return ADD_USER;
            }

            user.setPosition(position);
            user.setUserAccessLvl(parseAccessLevel(userAccessLvl));
            user.setApproved(true);

            userService.updateUser(user, role, department_id);
            log.info("User {} updated successfully by admin", userId);

            return REDIRECT_ADMIN_USERS;

        } catch (Exception e) {
            log.error("Error updating user {}: {}", userId, e.getMessage());
            model.addAttribute(ERROR_MESSAGE, "Ошибка при обновлении пользователя: " + e.getMessage());
            return ADD_USER;
        }
    }

    /**
     * Удаление пользователя
     */
    @GetMapping("/admin/deleteUser/{id}")
    public String deleteUser(@PathVariable Long id, Model model) {
        try {
            userService.deleteUserById(id);
            log.info("User {} deleted successfully", id);
            return REDIRECT_ADMIN_PREREGISTRATION;
        } catch (ResourceNotFoundException e) {
            log.error("Error deleting user {}: {}", id, e.getMessage());
            model.addAttribute(ERROR_MESSAGE, e.getMessage());
            return ERROR;
        }
    }

    /**
     * Фильтрация пользователей по уровню доступа
     */
    @GetMapping("/admin/getUsersByAccessLvl")
    public String getUsers(@RequestParam(required = false) String userAccessLvl, Model model, Principal principal) {
        AccessLevel parsedLevel = parseAccessLevel(userAccessLvl);
        List<User> users = (parsedLevel == null)
                ? userService.list()
                : userService.getUsersByAccessLvl(parsedLevel);

        model.addAttribute(USERS, users);
        modelAttributeHelper.addUserAttributes(model, principal);
        return ADMIN_USERS;
    }

    /**
     * Поиск пользователей по имени
     */
    @GetMapping("/admin/getUsersByName")
    public String getUser(@RequestParam(required = false) String name, Model model, Principal principal) {
        List<User> users = (name != null && !name.isEmpty())
                ? userService.getUsersByName(name)
                : userService.list();

        model.addAttribute(USERS, users);
        modelAttributeHelper.addUserAttributes(model, principal);
        return ADMIN_USERS;
    }

    /**
     * Блокировка/разблокировка пользователя
     */
    @GetMapping("/admin/userBlock/{id}")
    public String toggleUserBlock(@PathVariable Long id) {
        userService.toggleUserActiveStatus(id);
        log.info("User {} active status toggled", id);
        return REDIRECT_ADMIN_USERS;
    }

    /**
     * Список зон доступа
     */
    @GetMapping("/admin/zones")
    public String manageZones(Model model, Principal principal) {
        List<Zone> zones = zoneService.list();
        model.addAttribute(ZONES, zones);
        modelAttributeHelper.addUserAttributes(model, principal);
        return ADMIN_ZONES;
    }

    /**
     * Страница добавления зоны
     */
    @GetMapping("/admin/addZone")
    public String addZoneMethod(Principal principal, Model model) {
        log.debug("Displaying add zone form");
        modelAttributeHelper.addUserAttributes(model, principal);
        return ADD_ZONE;
    }

    /**
     * Создание новой зоны
     */
    @PostMapping("/admin/addZone")
    public String createZone(Zone zone, Model model) {
        try {
            zoneService.createZone(zone);
            log.info("Zone {} created successfully", zone.getName());
            return REDIRECT_ADMIN_ZONES;
        } catch (Exception e) {
            log.error("Error creating zone {}: {}", zone.getName(), e.getMessage());
            model.addAttribute(ERROR_MESSAGE, "Зона: " + zone.getName() + " уже существует");
            return ADD_ZONE;
        }
    }

    /**
     * Фильтрация зон по уровню доступа
     */
    @GetMapping("/getZonesByAccessLvl")
    public String getZones(@RequestParam(required = false) String zoneAccessLvl, Model model, Principal principal) {
        AccessLevel parsedLevel = parseAccessLevel(zoneAccessLvl);
        List<Zone> zones = (parsedLevel == null)
                ? zoneService.list()
                : zoneService.getZonesByAccessLvl(parsedLevel);

        model.addAttribute(ZONES, zones);
        modelAttributeHelper.addUserAttributes(model, principal);
        return ADMIN_ZONES;
    }

    /**
     * Поиск зон по имени
     */
    @GetMapping("/getZoneByName")
    public String getZone(@RequestParam(required = false) String name, Model model, Principal principal) {
        List<Zone> zones = (name != null && !name.isEmpty())
                ? zoneService.getZoneByName(name)
                : zoneService.list();

        model.addAttribute(ZONES, zones);
        modelAttributeHelper.addUserAttributes(model, principal);
        return ADMIN_ZONES;
    }

    /**
     * Список департаментов
     */
    @GetMapping("/admin/departments")
    public String manageDepartments(Model model, Principal principal) {
        List<Department> departments = departmentService.list();
        model.addAttribute(DEPARTMENTS, departments);
        modelAttributeHelper.addUserAttributes(model, principal);
        return ADMIN_DEPARTMENTS;
    }

    /**
     * Страница добавления департамента
     */
    @GetMapping("/admin/addDepartment")
    public String addDepartmentMethod(Principal principal, Model model) {
        log.debug("Displaying add department form");
        List<User> availableDirectors = userService.findDirectorsWithoutDepartment();
        model.addAttribute(AVAILABLE_DIRECTORS, availableDirectors);
        modelAttributeHelper.addUserAttributes(model, principal);
        return ADD_DEPARTMENT;
    }

    /**
     * Создание нового департамента
     */
    @PostMapping("/admin/addDepartment")
    public String createDepartment(@RequestParam Long headId,
                                   @ModelAttribute Department department,
                                   Model model) {
        try {
            if (headId == null) {
                log.warn("Head ID is null when creating department");
                model.addAttribute(ERROR_MESSAGE, "Не выбран руководитель!");
                return ADD_DEPARTMENT;
            }

            User head = userRepository.findById(headId).orElse(null);
            if (head == null) {
                log.warn("Head with ID {} not found", headId);
                model.addAttribute(ERROR_MESSAGE, "Руководитель с таким ID не найден!");
                return ADD_DEPARTMENT;
            }

            department.setHead(head);
            departmentService.createDepartment(department, headId);
            log.info("Department {} created successfully", department.getName());

            return REDIRECT_ADMIN_DEPARTMENTS;

        } catch (Exception e) {
            log.error("Error creating department {}: {}", department.getName(), e.getMessage());
            model.addAttribute(ERROR_MESSAGE, "Отдел: " + department.getName() + " уже существует");
            return ADD_DEPARTMENT;
        }
    }

    /**
     * Поиск департаментов по имени
     */
    @GetMapping("/getDepartmentByName")
    public String getDepartment(@RequestParam(required = false) String name, Model model, Principal principal) {
        List<Department> departments = (name != null && !name.isEmpty())
                ? departmentService.getDepartmentByName(name)
                : departmentService.list();

        model.addAttribute(DEPARTMENTS, departments);
        modelAttributeHelper.addUserAttributes(model, principal);
        return ADMIN_DEPARTMENTS;
    }

    /**
     * Страница изменения роли пользователя
     */
    @GetMapping("/admin/changeRole/{id}")
    public String changeRole(@PathVariable Long id, Model model, Principal principal) {
        User user = userService.getById(id);
        if (user == null) {
            log.warn("User with ID {} not found for role change", id);
            return REDIRECT_ADMIN_USERS;
        }

        model.addAttribute(USER, user);
        model.addAttribute(ROLES, Role.values());
        modelAttributeHelper.addUserAttributes(model, principal);
        return ADMIN_CHANGE_ROLE;
    }

    /**
     * Изменение роли пользователя
     */
    @PostMapping("/admin/changeRole/{id}")
    public String changeUserRole(@RequestParam("userId") Long userId,
                                 @RequestParam Map<String, String> form,
                                 RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getById(userId);
            if (user == null) {
                log.warn("User with ID {} not found", userId);
                return REDIRECT_ADMIN_USERS;
            }

            String roleName = form.get("role");
            Role role = Role.valueOf(roleName);

            user.getRoles().clear();
            user.getRoles().add(role);

            userService.updateUser(user);
            log.info("User {} role changed to {}", userId, roleName);

            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Роль пользователя обновлена!");
        } catch (Exception e) {
            log.error("Error changing user role: {}", e.getMessage());
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Ошибка при обновлении роли: " + e.getMessage());
        }

        return REDIRECT_ADMIN_USERS;
    }

    /**
     * Список пользователей, ожидающих одобрения
     */
    @GetMapping("/admin/preregistration")
    public String getPendingUsers(Model model, Principal principal) {
        List<User> pendingUsers = userService.getUsersWithApprovalStatus(false);
        model.addAttribute(USERS, pendingUsers);
        modelAttributeHelper.addUserAttributes(model, principal);
        return ADMIN_PREREGISTRATION;
    }
}
