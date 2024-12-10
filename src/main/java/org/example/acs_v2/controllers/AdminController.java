package org.example.acs_v2.controllers;

import lombok.RequiredArgsConstructor;
import org.example.acs_v2.models.Department;
import org.example.acs_v2.models.User;
import org.example.acs_v2.models.Zone;
import org.example.acs_v2.models.enums.Role;
import org.example.acs_v2.repositories.UserRepository;
import org.example.acs_v2.services.DepartmentService;
import org.example.acs_v2.services.UserService;
import org.example.acs_v2.services.ZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;
    @Autowired
    ZoneService zoneService;
    @Autowired
    DepartmentService departmentService;


    @GetMapping("/admin/users")
    public String manageUsers(Model model, Principal principal) {
        List<User> users = userService.listForAdmin(); // Метод для получения списка пользователей
        model.addAttribute("users", users);
        model.addAttribute("role", userService.getUserRole(principal));
        model.addAttribute("userId", userService.getUserId(principal));

        return "admin-users";
    }

    @GetMapping("/admin/addUser/{id}")
    public String addUserMethod(@PathVariable Long id, Principal principal, Model model) {

        User user = userService.getById(id);

        if (user == null) {
            return "redirect:/admin/users"; // Если пользователь не найден, перенаправляем на страницу администратора
        }

        List<Department> departments = departmentService.list();
        model.addAttribute("user", user);
        model.addAttribute("departments", departments);
        model.addAttribute("role", userService.getUserRole(principal));
        model.addAttribute("userId", userService.getUserId(principal));

        return "addUser";
    }


    @PostMapping("/admin/addUser")
    public String createUser(@RequestParam String role,
                             @RequestParam Long department_id,
                             @RequestParam String position,
                             @RequestParam int userAccessLvl,
                             @RequestParam Long userId,
                             Model model) {
        User user = userService.findById(userId);

        if (user == null) {
            model.addAttribute("errorMessage", "Пользователь с ID: " + userId + " не найден");
            return "addUser";
        }

        user.setPosition(position);
        user.setUserAccessLvl(userAccessLvl);
        user.setApproved(true); // Одобрение пользователя администратором

        if (!userService.updateUser(user, role, department_id)) {
            model.addAttribute("errorMessage", "Ошибка при обновлении пользователя с email: " + user.getEmail());
            return "addUser";
        }

        return "redirect:/admin/users";
    }

    @GetMapping("/admin/deleteUser/{id}")
    public String deleteUser(@PathVariable Long id, Model model) {
        if (!userService.deleteUserById(id)) {
            model.addAttribute("errorMessage", "Ошибка при удалении пользователя с ID: " + id);
            return "error"; // Вернуть на страницу ошибки
            }
        return "redirect:/admin/preregistration";
    }

    @GetMapping("/admin/getUsersByAccessLvl")
    public String getUsers(@RequestParam(required = false) int userAccessLvl, Model model, Principal principal) {
        List<User> users;
        if (userAccessLvl == -1) {
            users = userService.list();
        } else {
            users = userService.getUsersByAccessLvl(userAccessLvl);
        }
        model.addAttribute("users", users);
        model.addAttribute("role", userService.getUserRole(principal));
        model.addAttribute("userId", userService.getUserId(principal));
        return "admin-users";
    }

    @GetMapping("/admin/getUsersByName")
    public String getUser(@RequestParam(required = false) String name, Model model, Principal principal) {
        List<User> users;
        if (name != null && !name.isEmpty()) {
            users = userService.getUsersByName(name);
        } else {
            users = userService.list();
        }
        model.addAttribute("users", users);
        model.addAttribute("role", userService.getUserRole(principal));
        model.addAttribute("userId", userService.getUserId(principal));
        return "admin-users";
    }

    @GetMapping("/admin/userBlock/{id}")
    public String toggleUserBlock(@PathVariable Long id) {
        userService.toggleUserActiveStatus(id);
        return "redirect:/admin/users"; // Перенаправляем обратно на список пользователей
    }

    @GetMapping("/admin/zones")
    public String manageZones(Model model, Principal principal) {
        List<Zone> zones = zoneService.list();
        model.addAttribute("zones", zones);
        model.addAttribute("role", userService.getUserRole(principal));
        model.addAttribute("userId", userService.getUserId(principal));

        return "admin-zones";
    }

    @GetMapping("/admin/addZone")
    public String addZoneMethod(Principal principal, Model model) {
        System.out.println("Форму для создания зоны вывел");
        model.addAttribute("role", userService.getUserRole(principal));
        model.addAttribute("userId", userService.getUserId(principal));

        return "addZone";
    }

    @PostMapping("/admin/addZone")
    public String createZone(Zone zone, Model model) {
        System.out.println("Данные для создания зоны отправил");
        if (!zoneService.createZone(zone)) {
            model.addAttribute("errorMessage", "Зона: " + zone.getName() + " уже существует");
            return "addZone";
        }
        System.out.println("Буду переключать страничку на все зоны");
        return "redirect:/admin/zones";
    }

    @GetMapping("/getZonesByAccessLvl")
    public String getZones(@RequestParam(required = false) int zoneAccessLvl, Model model, Principal principal) {
        List<Zone> zones;
        if (zoneAccessLvl == -1) {
            zones = zoneService.list();
        } else {
            zones = zoneService.getZonesByAccessLvl(zoneAccessLvl);
        }
        model.addAttribute("zones", zones);
        model.addAttribute("role", userService.getUserRole(principal));
        model.addAttribute("userId", userService.getUserId(principal));
        return "admin-zones";
    }

    @GetMapping("/getZoneByName")
    public String getZone(@RequestParam(required = false) String name, Model model, Principal principal) {
        List<Zone> zones;
        if (name != null && !name.isEmpty()) {
            zones = zoneService.getZoneByName(name);
        } else {
            zones = zoneService.list();
        }
        model.addAttribute("zones", zones);
        model.addAttribute("role", userService.getUserRole(principal));
        model.addAttribute("userId", userService.getUserId(principal));
        return "admin-zones";
    }

    @GetMapping("/admin/departments")
    public String manageDepartments(Model model, Principal principal) {
        List<Department> departments = departmentService.list();
        model.addAttribute("departments", departments);
        model.addAttribute("role", userService.getUserRole(principal));
        model.addAttribute("userId", userService.getUserId(principal));

        return "admin-departments";
    }

    @GetMapping("/admin/addDepartment")
    public String addDepartmentMethod(Principal principal, Model model) {
        System.out.println("Форму для создания зоны вывел");
        List<User> availableDirectors = userService.findDirectorsWithoutDepartment();
        model.addAttribute("availableDirectors", availableDirectors);
        model.addAttribute("role", userService.getUserRole(principal));
        model.addAttribute("userId", userService.getUserId(principal));

        return "addDepartment";
    }

    @PostMapping("/admin/addDepartment")
    public String createDepartment(@RequestParam Long headId,
                                   @ModelAttribute Department department,
                                   Model model) {
        System.out.println("Данные для создания отдела отправил");
        if (headId == null) {
            model.addAttribute("errorMessage", "Не выбран руководитель!");
            return "addDepartment";
        }

        User head = userRepository.findById(headId).orElse(null);
        if (head == null) {
            model.addAttribute("errorMessage", "Руководитель с таким ID не найден!");
            return "addDepartment";
        }

        department.setHead(head);  // Устанавливаем руководителя

        if (!departmentService.createDepartment(department, headId)) {
            model.addAttribute("errorMessage", "Отдел: " + department.getName() + " уже существует");
            return "addDepartment";
        }

        return "redirect:/admin/departments";
    }

    @GetMapping("/getDepartmentByName")
    public String getDepartment(@RequestParam(required = false) String name, Model model, Principal principal) {
        List<Department> departments;
        if (name != null && !name.isEmpty()) {
            departments = departmentService.getDepartmentByName(name);
        } else {
            departments = departmentService.list();
        }
        model.addAttribute("departments", departments);
        model.addAttribute("role", userService.getUserRole(principal));
        model.addAttribute("userId", userService.getUserId(principal));
        return "admin-departments";
    }

    @GetMapping("/admin/changeRole/{id}")
    public String changeRole(@PathVariable Long id, Model model, Principal principal) {
        // Получаем пользователя по ID
        User user = userService.getById(id);
        if (user == null) {
            return "redirect:/admin/users"; // Если пользователь не найден, перенаправляем на страницу администратора
        }

        // Передаем роли как список значений из Enum
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        model.addAttribute("userId", userService.getUserId(principal));
        model.addAttribute("role", userService.getUserRole(principal));// Список всех ролей из Enum
        return "admin-changeRole";
    }


    @PostMapping("/admin/changeRole/{id}")
    public String changeUserRole(@RequestParam("userId") Long userId, @RequestParam Map<String, String> form, RedirectAttributes redirectAttributes) {
        User user = userService.getById(userId);
        if (user == null) {
            return "redirect:/admin/users"; // Если пользователь не найден, перенаправляем на страницу администратора
        }

        try {
            // Изменяем роль пользователя
            String roleName = form.get("role"); // Получаем роль, выбранную в форме
            Role role = Role.valueOf(roleName); // Преобразуем строку в Enum

            // Очистим текущие роли пользователя и добавим новую
            user.getRoles().clear();
            user.getRoles().add(role); // Устанавливаем новую роль

            // Сохраняем изменения
            userService.updateUser(user);

            redirectAttributes.addFlashAttribute("successMessage", "Роль пользователя обновлена!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при обновлении роли: " + e.getMessage());
        }

        return "redirect:/admin/users"; // Перенаправляем на страницу администратора
    }

    @GetMapping("/admin/preregistration")
    public String getPendingUsers(Model model,Principal principal) {
        List<User> pendingUsers = userService.getUsersWithApprovalStatus(false);
        model.addAttribute("users", pendingUsers);
        model.addAttribute("userId", userService.getUserId(principal));
        model.addAttribute("role", userService.getUserRole(principal));
        return "admin-preregistration"; // Имя шаблона, который вы указали
    }
}
