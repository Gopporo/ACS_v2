package org.example.acs_v2.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.acs_v2.models.*;
import org.example.acs_v2.models.enums.AccessLevel;
import org.example.acs_v2.services.*;
import org.example.acs_v2.utils.ModelAttributeHelper;
import org.example.acs_v2.validators.AccessLevelValidator;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

import static org.example.acs_v2.constants.ModelAttributeConstants.*;
import static org.example.acs_v2.constants.RedirectConstants.*;
import static org.example.acs_v2.constants.ViewConstants.*;

/**
 * Контроллер для функционала сотрудников
 */
@Controller
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_USER')")
@Slf4j
public class EmployeeController {

    private final UserService userService;
    private final ApplicationService applicationService;
    private final ReportService reportService;
    private final ModelAttributeHelper modelAttributeHelper;
    private final AccessLevelValidator accessLevelValidator;

    private AccessLevel parseAccessLevel(String value) {
        if (value == null || value.isBlank() || "-1".equals(value)) {
            return null;
        }
        if (value.matches("\\d+")) {
            return AccessLevel.valueOf("LEVEL_" + value);
        }
        return AccessLevel.valueOf(value.toUpperCase());
    }
    /**
     * Список доступных заявок для сотрудника
     */
    @GetMapping("/employee/applications")
    public String manageApplications(Model model, Principal principal) {
        List<Application> applications = applicationService.listOfFreeApplications();
        model.addAttribute(APPLICATIONS, applications);
        modelAttributeHelper.addUserAttributes(model, principal);
        return EMPLOYEE_APPLICATIONS;
    }

    /**
     * Фильтрация заявок по уровню доступа
     */
    @GetMapping("/employee/getApplicationsByAccessLvl")
    public String getApplications(@RequestParam(required = false) String accessLvl, Model model, Principal principal) {
        AccessLevel parsedLevel = parseAccessLevel(accessLvl);
        List<Application> applications = (parsedLevel == null)
                ? applicationService.listOfFreeApplications()
                : applicationService.getApplicationsByAccessLvl(parsedLevel);

        model.addAttribute(APPLICATIONS, applications);
        modelAttributeHelper.addUserAttributes(model, principal);
        return EMPLOYEE_APPLICATIONS;
    }

    /**
     * Поиск заявок по имени
     */
    @GetMapping("/employee/getApplicationsByName")
    public String getApplication(@RequestParam(required = false) String name, Model model, Principal principal) {
        List<Application> applications = (name != null && !name.isEmpty())
                ? applicationService.getApplicationsByName(name)
                : applicationService.listOfFreeApplications();

        model.addAttribute(APPLICATIONS, applications);
        modelAttributeHelper.addUserAttributes(model, principal);
        return EMPLOYEE_APPLICATIONS;
    }

    /**
     * Принятие заявки сотрудником
     */
    @GetMapping("/employee/acceptApplication/{id}")
    public String acceptApplication(@PathVariable Long id, Principal principal, Model model) {
        User user = userService.getByEmail(principal.getName());
        Application application = applicationService.getById(id);

        try {
            accessLevelValidator.validateUserCanAcceptApplication(user, application);
            applicationService.acceptApplication(id, user.getId());
            log.info("User {} accepted application {}", user.getId(), id);
            return REDIRECT_EMPLOYEE_APPLICATIONS;
        } catch (org.example.acs_v2.exceptions.AccessDeniedException e) {
            log.warn("User {} cannot accept application {}: {}", user.getId(), id, e.getMessage());
            model.addAttribute(ERROR_MESSAGE, e.getMessage());
            model.addAttribute(APPLICATIONS, applicationService.listOfFreeApplications());
            modelAttributeHelper.addUserAttributes(model, principal);
            return EMPLOYEE_APPLICATIONS;
        }
    }

    /**
     * Список заявок текущего сотрудника
     */
    @GetMapping("/employee/applications/my")
    public String manageMyApplications(Model model, Principal principal) {
        User user = userService.getByEmail(principal.getName());
        List<Application> applications = applicationService.listOfUserApplications(user.getId());
        model.addAttribute(APPLICATIONS, applications);
        modelAttributeHelper.addUserAttributes(model, principal);
        return EMPLOYEE_MY_APPLICATIONS;
    }

    /**
     * Отклонение заявки
     */
    @GetMapping("/employee/declineApplication/{id}")
    public String declineApplication(@PathVariable Long id) {
        applicationService.declineApplication(id);
        log.info("Application {} declined", id);
        return REDIRECT_EMPLOYEE_MY_APPLICATIONS;
    }

    /**
     * Список отчетов сотрудника
     */
    @GetMapping("/employee/reports")
    public String manageReports(Model model, Principal principal) {
        User user = userService.getByEmail(principal.getName());
        List<Report> reports = reportService.listOfUserReports(user.getId());
        model.addAttribute(REPORTS, reports);
        modelAttributeHelper.addUserAttributes(model, principal);
        return EMPLOYEE_REPORTS;
    }

    /**
     * Страница добавления отчета
     */
    @GetMapping("/employee/addReport/{id}")
    public String addReport(@PathVariable Long id, Model model, Principal principal) {
        Application application = applicationService.getById(id);
        model.addAttribute(APPLICATION, application);
        modelAttributeHelper.addUserAttributes(model, principal);
        return EMPLOYEE_ADD_REPORT;
    }

    /**
     * Создание отчета
     */
    @PostMapping("/employee/addReport")
    public String createReport(@RequestParam Long applicationId, Report report, Model model, Principal principal) {
        try {
            reportService.createReport(applicationId, report);
            log.info("Report created for application {}", applicationId);
            return REDIRECT_EMPLOYEE_REPORTS;
        } catch (Exception e) {
            log.error("Error creating report for application {}: {}", applicationId, e.getMessage());
            model.addAttribute(ERROR_MESSAGE, "Произошла ошибка при создании отчета. Пожалуйста, попробуйте еще раз.");
            modelAttributeHelper.addUserAttributes(model, principal);
            return EMPLOYEE_ADD_REPORT;
        }
    }

    /**
     * Детальная информация об отчете
     */
    @GetMapping("/employee/report-info/{id}")
    public String getReportDetails(@PathVariable Long id, Model model, Principal principal) {
        Report report = reportService.getById(id);
        model.addAttribute(REPORT, report);
        modelAttributeHelper.addUserAttributes(model, principal);
        return EMPLOYEE_REPORT_INFO;
    }


}
