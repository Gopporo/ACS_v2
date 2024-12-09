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
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_USER')")
public class EmployeeController {

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


    @GetMapping("/employee/applications")
    public String manageApplications(Model model, Principal principal) {
        List<Application> applications = applicationService.listOfFreeApplications(); // Метод для получения списка пользователей
        model.addAttribute("applications", applications);
        model.addAttribute("role", userService.getUserRole(principal));
        model.addAttribute("userId", userService.getUserId(principal));

        return "employee-applications";
    }

    @GetMapping("/employee/getApplicationsByAccessLvl")
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
        return "employee-applications";
    }

    @GetMapping("/employee/getApplicationsByName")
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
        return "employee-applications";
    }

    @GetMapping("/employee/acceptApplication/{id}")
    public String acceptApplication(@PathVariable Long id, Principal principal, Model model) {
        User user = userService.getByEmail(principal.getName());
        Application application = applicationService.getById(id);

        if (application.getAccessLevel() > user.getUserAccessLvl()) {
            model.addAttribute("errorMessage", "Уровень доступа заявки выше, чем у вас. Принятие заявки невозможно.");
            model.addAttribute("applications", applicationService.listOfUserApplications(user.getId()));
            model.addAttribute("role", userService.getUserRole(principal));
            model.addAttribute("userId", userService.getUserId(principal));
            return "employee-applications"; // Остаемся на той же странице
        }

        applicationService.acceptApplication(id, user.getId());
        return "redirect:/employee/applications"; // Перенаправляем обратно на список заявок
    }




    @GetMapping("/employee/applications/my")
    public String manageMyApplications(Model model, Principal principal) {
        User user = userService.getByEmail(principal.getName());
        List<Application> applications = applicationService.listOfUserApplications(user.getId()); // Метод для получения списка заявок пользователя
        model.addAttribute("applications", applications);
        model.addAttribute("role", userService.getUserRole(principal));
        model.addAttribute("userId", userService.getUserId(principal));

        return "employee-my-applications";
    }

    @GetMapping("/employee/declineApplication/{id}")
    public String declineApplication(@PathVariable Long id) {
        applicationService.declineApplication(id);
        return "redirect:/employee/applications/my"; // Перенаправляем обратно на список заявок
    }


    @GetMapping("/employee/reports")
    public String manageReports(Model model, Principal principal) {
        User user = userService.getByEmail(principal.getName());
        List<Report> reports = reportService.listOfUserReports(user.getId()); // Метод для получения списка пользователей
        model.addAttribute("reports", reports);
        model.addAttribute("role", userService.getUserRole(principal));
        model.addAttribute("userId", userService.getUserId(principal));

        return "employee-reports";
    }

    @GetMapping("/employee/addReport/{id}")
    public String addReport(@PathVariable Long id, Model model, Principal principal) {
        Application application = applicationService.getById(id);
        model.addAttribute("application", application);
        model.addAttribute("role", userService.getUserRole(principal));
        model.addAttribute("userId", userService.getUserId(principal));
        return "employee-addReport";
    }

    @PostMapping("/employee/addReport")
    public String createReport(@RequestParam Long applicationId, Report report, Model model) {
        try {
            reportService.createReport(applicationId, report);
            return "redirect:/employee/reports";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Произошла ошибка при создании отчета. Пожалуйста, попробуйте еще раз.");
            return "employee-addReport";
        }
    }

    @GetMapping("/employee/report-info/{id}")
    public String getReportDetails(@PathVariable Long id, Model model, Principal principal) {
        Report report = reportService.getById(id); // Метод для получения отчета по его id
        model.addAttribute("report", report);
        model.addAttribute("role", userService.getUserRole(principal));
        model.addAttribute("userId", userService.getUserId(principal));
        return "employee-report-info"; // Имя шаблона для отображения подробной информации по отчету
    }


}
