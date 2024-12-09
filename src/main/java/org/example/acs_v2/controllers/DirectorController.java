package org.example.acs_v2.controllers;

import lombok.RequiredArgsConstructor;
import org.example.acs_v2.models.*;
import org.example.acs_v2.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_DIRECTOR')")
public class DirectorController {

    @Autowired
    UserService userService;
    @Autowired
    DepartmentService departmentService;
    @Autowired
    ApplicationService applicationService;
    @Autowired
    private ZoneService zoneService;
    @Autowired
    ReportService reportService;

    @GetMapping("/director/users")
    public String manageUsers(Model model, Principal principal) {
        List<User> users = userService.listForDirector(principal); // Метод для получения списка пользователей
        model.addAttribute("users", users);
        model.addAttribute("role", userService.getUserRole(principal));
        model.addAttribute("userId", userService.getUserId(principal));

        return "director-users";
    }

    @GetMapping("/director/getUsersByAccessLvl")
    public String getUsers(@RequestParam(required = false) int userAccessLvl, Model model, Principal principal) {
        List<User> users;
        if (userAccessLvl == -1) {
            users = userService.listForDirector(principal);
        } else {
            users = userService.getUsersByAccessLvlForDirector(userAccessLvl, principal);
        }
        model.addAttribute("users", users);
        model.addAttribute("role", userService.getUserRole(principal));
        model.addAttribute("userId", userService.getUserId(principal));
        return "director-users";
    }

    @GetMapping("/director/getUsersByName")
    public String getUser(@RequestParam(required = false) String name, Model model, Principal principal) {
        List<User> users;
        if (name != null && !name.isEmpty()) {
            users = userService.getUsersByNameForDirector(name, principal);
        } else {
            users = userService.listForDirector(principal);
        }
        model.addAttribute("users", users);
        model.addAttribute("role", userService.getUserRole(principal));
        model.addAttribute("userId", userService.getUserId(principal));
        return "director-users";
    }

    @GetMapping("/director/editUser/{userId}")
    public String editUserPage(@PathVariable Long userId, Model model,Principal principal) {
        User user = userService.getById(userId);
        List<Department> departments = departmentService.list();
        model.addAttribute("user", user);
        model.addAttribute("departments", departments);
        model.addAttribute("role", userService.getUserRole(principal));
        model.addAttribute("userId", userService.getUserId(principal));
        return "director-edit-user"; // Имя вашего шаблона
    }

    @PostMapping("/director/updateUser")
    public String updateUser(@RequestParam Long userId,
                             @RequestParam int accessLevel,
                             @RequestParam Long departmentId) {
        // Получение пользователя и обновление данных
        System.out.println("Все хорошо");
        User user = userService.getById(userId);
        Department department = departmentService.getDepartmentById(departmentId);
        user.setUserAccessLvl(accessLevel);
        user.setDepartment(department);
        userService.saveUser(user); // Сохранение изменений
        return "redirect:/director/users"; // Перенаправление на список пользователей
    }


    @GetMapping("/director/applications")
    public String manageApplications(Model model, Principal principal) {
        List<Application> applications = applicationService.listOfFreeApplications(); // Метод для получения списка пользователей
        model.addAttribute("applications", applications);
        model.addAttribute("role", userService.getUserRole(principal));
        model.addAttribute("userId", userService.getUserId(principal));

        return "director-applications";
    }

    @GetMapping("/director/addApplication")
    public String addApplication(Model model, Principal principal) {

        List<Zone> zones = zoneService.list();
        model.addAttribute("zones", zones);
        model.addAttribute("role", userService.getUserRole(principal));
        model.addAttribute("userId", userService.getUserId(principal));
        return "addApplication";
    }

    @PostMapping("/director/addApplication")
    public String addApplication(@RequestParam Long zone_id, Application application, Model model) {

        if (!applicationService.createApplication(zone_id, application)) {
            model.addAttribute("errorMessage", "Зона: " + application.getName() + " уже существует");
            return "addZone";
        }

        return "redirect:/director/applications";
    }

    @GetMapping("/director/editApplication/{id}")
    public String editApplication(@PathVariable Long id, Model model, Principal principal) {
        Application application = applicationService.getById(id);
        List<Zone> zones = zoneService.list();
        model.addAttribute("application", application);
        model.addAttribute("zones", zones);
        model.addAttribute("role", userService.getUserRole(principal));
        model.addAttribute("userId", userService.getUserId(principal));
        return "director-edit-application"; // Имя вашего шаблона
    }


    @PostMapping("/director/updateApplication")
    public String updateApplication(@RequestParam Long applicationId,
                                    @RequestParam String name,
                                    @RequestParam String disc,
                                    @RequestParam Long zoneId) {
        // Получение заявки и обновление данных
        Application application = applicationService.getById(applicationId);
        Zone zone = zoneService.getById(zoneId);
        application.setName(name);
        application.setDisc(disc);
        application.setZone(zone);
        applicationService.updateApplication(application); // Сохранение изменений
        return "redirect:/director/applications"; // Перенаправление на список заявок
    }

    @GetMapping("/director/getApplicationsByAccessLvl")
    public String getApplications(@RequestParam(required = false) int accessLvl, Model model, Principal principal) {
        List<Application> applications;
        if (accessLvl == -1) {
            applications = applicationService.listOfFreeApplications();
        } else {
            applications = applicationService.getApplicationsByAccessLvl(accessLvl);
        }
        model.addAttribute("applications", applications);
        model.addAttribute("role", userService.getUserRole(principal));
        model.addAttribute("userId", userService.getUserId(principal));
        return "director-applications";
    }

    @GetMapping("/director/getApplicationsByName")
    public String getApplication(@RequestParam(required = false) String name, Model model, Principal principal) {
        List<Application> applications;
        if (name != null && !name.isEmpty()) {
            applications = applicationService.getApplicationsByName(name);
        } else {
            applications = applicationService.listOfFreeApplications();
        }
        model.addAttribute("applications", applications);
        model.addAttribute("role", userService.getUserRole(principal));
        model.addAttribute("userId", userService.getUserId(principal));
        return "director-applications";
    }

    @GetMapping("/director/reports")
    public String manageReports(Model model, Principal principal) {
        List<Report> reports = reportService.list();// Метод для получения списка пользователей
        model.addAttribute("reports", reports);
        model.addAttribute("role", userService.getUserRole(principal));
        model.addAttribute("userId", userService.getUserId(principal));

        return "director-reports";
    }

    @GetMapping("/director/report-info/{id}")
    public String getReportDetails(@PathVariable Long id, Model model, Principal principal) {
        Report report = reportService.getById(id); // Метод для получения отчета по его id
        model.addAttribute("report", report);
        model.addAttribute("role", userService.getUserRole(principal));
        model.addAttribute("userId", userService.getUserId(principal));
        return "director-report-info"; // Имя шаблона для отображения подробной информации по отчету
    }


    @GetMapping("/director/deleteApplication/{id}")
    public String deleteApplication(@PathVariable Long id) {
        applicationService.deleteApplication(id);
        return "redirect:/director/applications";
    }

}
