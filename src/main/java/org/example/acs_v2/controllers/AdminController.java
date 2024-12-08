package org.example.acs_v2.controllers;

import lombok.RequiredArgsConstructor;
import org.example.acs_v2.models.Department;
import org.example.acs_v2.models.User;
import org.example.acs_v2.models.Zone;
import org.example.acs_v2.repositories.UserRepository;
import org.example.acs_v2.services.DepartmentService;
import org.example.acs_v2.services.UserService;
import org.example.acs_v2.services.ZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

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
        List<User> users = userService.list(); // Метод для получения списка пользователей
        model.addAttribute("users", users);
        model.addAttribute("role", userService.getUserRole(principal));
        model.addAttribute("userId", userService.getUserId(principal));

        return "admin-users";
    }

    @GetMapping("/admin/addUser")
    public String addUserMethod(Principal principal, Model model) {
        System.out.println("Страничку вывел");
        List<Department> departments = departmentService.list();
        model.addAttribute("departments", departments);
        model.addAttribute("role", userService.getUserRole(principal));
        model.addAttribute("userId", userService.getUserId(principal));

        return "addUser";
    }

    @PostMapping("/admin/addUser")
    public String createUser(@RequestParam String role,@RequestParam Long department_id, User user, Model model) {
        System.out.println("Данные отправил");
        if (!userService.createUser(user, role, department_id)) {
            model.addAttribute("errorMessage", "Пользователь с email: " + user.getEmail() + " уже существует");
            return "addUser";
        }
        System.out.println("Буду переключать страничку на всех пользователей");
        return "redirect:/admin/users";
    }

    @GetMapping("/getUsersByAccessLvl")
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

    @GetMapping("/getUsersByName")
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
}
