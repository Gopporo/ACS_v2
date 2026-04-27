package org.example.acs_v2.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.acs_v2.constants.ModelAttributeConstants;
import org.example.acs_v2.constants.RedirectConstants;
import org.example.acs_v2.constants.ViewConstants;
import org.example.acs_v2.dto.DirectorReportStatsDto;
import org.example.acs_v2.exceptions.ResourceNotFoundException;
import org.example.acs_v2.models.*;
import org.example.acs_v2.models.enums.AccessLevel;
import org.example.acs_v2.services.*;
import org.example.acs_v2.utils.ModelAttributeHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayOutputStream;
import java.security.Principal;
import java.util.List;

/**
 * Контроллер для управления функциями директора
 * Обрабатывает операции с пользователями, заявками и отчетами
 */
@Controller
@Slf4j
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_DIRECTOR')")
public class DirectorController {

    private final UserService userService;
    private final DepartmentService departmentService;
    private final ApplicationService applicationService;
    private final ZoneService zoneService;
    private final ReportService reportService;
    private final TemporaryAccessService temporaryAccessService;
    private final ModelAttributeHelper modelAttributeHelper;

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
     * Отображает страницу управления пользователями для директора
     *
     * @param model     модель для передачи данных в представление
     * @param principal текущий аутентифицированный пользователь
     * @return имя представления для отображения списка пользователей
     */
    @GetMapping("/director/users")
    public String manageUsers(Model model, Principal principal) {
        log.debug("Director accessing users management page");
        List<User> users = userService.listForDirector(principal);
        model.addAttribute(ModelAttributeConstants.USERS, users);
        modelAttributeHelper.addCommonAttributes(model, principal);
        return ViewConstants.DIRECTOR_USERS;
    }

    /**
     * Фильтрует пользователей по уровню доступа
     *
     * @param userAccessLvl уровень доступа для фильтрации (-1 для всех пользователей)
     * @param model         модель для передачи данных в представление
     * @param principal     текущий аутентифицированный пользователь
     * @return имя представления для отображения отфильтрованного списка пользователей
     */
    @GetMapping("/director/getUsersByAccessLvl")
    public String getUsers(@RequestParam(required = false) String userAccessLvl, Model model, Principal principal) {
        log.debug("Filtering users by access level: {}", userAccessLvl);
        List<User> users;
        AccessLevel parsedLevel = parseAccessLevel(userAccessLvl);
        if (parsedLevel == null) {
            users = userService.listForDirector(principal);
        } else {
            users = userService.getUsersByAccessLvlForDirector(parsedLevel, principal);
        }
        model.addAttribute(ModelAttributeConstants.USERS, users);
        modelAttributeHelper.addCommonAttributes(model, principal);
        return ViewConstants.DIRECTOR_USERS;
    }

    /**
     * Фильтрует пользователей по имени
     *
     * @param name      имя для поиска (если пусто, возвращает всех пользователей)
     * @param model     модель для передачи данных в представление
     * @param principal текущий аутентифицированный пользователь
     * @return имя представления для отображения отфильтрованного списка пользователей
     */
    @GetMapping("/director/getUsersByName")
    public String getUser(@RequestParam(required = false) String name, Model model, Principal principal) {
        log.debug("Filtering users by name: {}", name);
        List<User> users;
        if (name != null && !name.isEmpty()) {
            users = userService.getUsersByNameForDirector(name, principal);
        } else {
            users = userService.listForDirector(principal);
        }
        model.addAttribute(ModelAttributeConstants.USERS, users);
        modelAttributeHelper.addCommonAttributes(model, principal);
        return ViewConstants.DIRECTOR_USERS;
    }

    /**
     * Отображает страницу редактирования пользователя
     *
     * @param userId    ID пользователя для редактирования
     * @param model     модель для передачи данных в представление
     * @param principal текущий аутентифицированный пользователь
     * @return имя представления для редактирования пользователя
     */
    @GetMapping("/director/editUser/{userId}")
    public String editUserPage(@PathVariable Long userId, Model model, Principal principal) {
        log.debug("Director accessing edit page for user ID: {}", userId);
        try {
            User user = userService.getById(userId);
            List<Department> departments = departmentService.list();
            model.addAttribute(ModelAttributeConstants.USER, user);
            model.addAttribute(ModelAttributeConstants.DEPARTMENTS, departments);
            modelAttributeHelper.addCommonAttributes(model, principal);
            return ViewConstants.DIRECTOR_EDIT_USER;
        } catch (Exception e) {
            log.error("Error accessing edit page for user ID: {}", userId, e);
            throw new ResourceNotFoundException("User", userId);
        }
    }

    /**
     * Обновляет данные пользователя (уровень доступа и отдел)
     *
     * @param userId       ID пользователя
     * @param accessLevel  новый уровень доступа
     * @param departmentId ID нового отдела
     * @return перенаправление на список пользователей
     */
    @PostMapping("/director/updateUser")
    public String updateUser(@RequestParam Long userId,
                             @RequestParam String accessLevel,
                             @RequestParam Long departmentId) {
        log.info("Updating user ID: {} with access level: {} and department ID: {}", userId, accessLevel, departmentId);
        try {
            User user = userService.getById(userId);
            Department department = departmentService.getDepartmentById(departmentId);
            user.setUserAccessLvl(parseAccessLevel(accessLevel));
            user.setDepartment(department);
            userService.saveUser(user);
            log.info("User ID: {} successfully updated", userId);
            return RedirectConstants.REDIRECT_DIRECTOR_USERS;
        } catch (Exception e) {
            log.error("Error updating user ID: {}", userId, e);
            throw new ResourceNotFoundException("User or Department", userId);
        }
    }


    /**
     * Отображает страницу управления заявками
     *
     * @param model     модель для передачи данных в представление
     * @param principal текущий аутентифицированный пользователь
     * @return имя представления для отображения списка заявок
     */
    @GetMapping("/director/applications")
    public String manageApplications(Model model, Principal principal) {
        log.debug("Director accessing applications management page");
        List<Application> applications = applicationService.listOfFreeApplications();
        model.addAttribute(ModelAttributeConstants.APPLICATIONS, applications);
        modelAttributeHelper.addCommonAttributes(model, principal);
        return ViewConstants.DIRECTOR_APPLICATIONS;
    }

    /**
     * Отображает страницу добавления новой заявки
     *
     * @param model     модель для передачи данных в представление
     * @param principal текущий аутентифицированный пользователь
     * @return имя представления для добавления заявки
     */
    @GetMapping("/director/addApplication")
    public String addApplication(Model model, Principal principal) {
        log.debug("Director accessing add application page");
        List<Zone> zones = zoneService.list();
        model.addAttribute(ModelAttributeConstants.ZONES, zones);
        modelAttributeHelper.addCommonAttributes(model, principal);
        return ViewConstants.ADD_APPLICATION;
    }

    /**
     * Создает новую заявку
     *
     * @param zone_id     ID зоны для заявки
     * @param application объект заявки
     * @param model       модель для передачи данных в представление
     * @return перенаправление на список заявок или страницу с ошибкой
     */
    @PostMapping("/director/addApplication")
    public String addApplication(@RequestParam Long zone_id, Application application, Model model) {
        log.info("Creating new application for zone ID: {}", zone_id);
        try {
            applicationService.createApplication(zone_id, application);
            log.info("Application created successfully for zone ID: {}", zone_id);
            return RedirectConstants.REDIRECT_DIRECTOR_APPLICATIONS;
        } catch (ResourceNotFoundException e) {
            log.error("Zone not found with ID: {}", zone_id, e);
            model.addAttribute(ModelAttributeConstants.ERROR_MESSAGE, "Зона не найдена");
            return ViewConstants.ADD_APPLICATION;
        } catch (Exception e) {
            log.error("Error creating application for zone ID: {}", zone_id, e);
            model.addAttribute(ModelAttributeConstants.ERROR_MESSAGE, "Ошибка при создании заявки");
            return ViewConstants.ADD_APPLICATION;
        }
    }

    /**
     * Отображает страницу редактирования заявки
     *
     * @param id        ID заявки для редактирования
     * @param model     модель для передачи данных в представление
     * @param principal текущий аутентифицированный пользователь
     * @return имя представления для редактирования заявки
     */
    @GetMapping("/director/editApplication/{id}")
    public String editApplication(@PathVariable Long id, Model model, Principal principal) {
        log.debug("Director accessing edit page for application ID: {}", id);
        try {
            Application application = applicationService.getById(id);
            List<Zone> zones = zoneService.list();
            model.addAttribute(ModelAttributeConstants.APPLICATION, application);
            model.addAttribute(ModelAttributeConstants.ZONES, zones);
            modelAttributeHelper.addCommonAttributes(model, principal);
            return ViewConstants.DIRECTOR_EDIT_APPLICATION;
        } catch (Exception e) {
            log.error("Error accessing edit page for application ID: {}", id, e);
            throw new ResourceNotFoundException("Application", id);
        }
    }


    /**
     * Обновляет данные заявки
     *
     * @param applicationId ID заявки
     * @param name          новое имя заявки
     * @param disc          новое описание заявки
     * @param zoneId        ID новой зоны
     * @return перенаправление на список заявок
     */
    @PostMapping("/director/updateApplication")
    public String updateApplication(@RequestParam Long applicationId,
                                    @RequestParam String name,
                                    @RequestParam String disc,
                                    @RequestParam Long zoneId) {
        log.info("Updating application ID: {} with zone ID: {}", applicationId, zoneId);
        try {
            Application application = applicationService.getById(applicationId);
            Zone zone = zoneService.getById(zoneId);
            application.setName(name);
            application.setDisc(disc);
            application.setZone(zone);
            applicationService.updateApplication(application);
            log.info("Application ID: {} successfully updated", applicationId);
            return RedirectConstants.REDIRECT_DIRECTOR_APPLICATIONS;
        } catch (Exception e) {
            log.error("Error updating application ID: {}", applicationId, e);
            throw new ResourceNotFoundException("Application or Zone", applicationId);
        }
    }

    /**
     * Фильтрует заявки по уровню доступа
     *
     * @param accessLvl уровень доступа для фильтрации (-1 для всех заявок)
     * @param model     модель для передачи данных в представление
     * @param principal текущий аутентифицированный пользователь
     * @return имя представления для отображения отфильтрованного списка заявок
     */
    @GetMapping("/director/getApplicationsByAccessLvl")
    public String getApplications(@RequestParam(required = false) String accessLvl, Model model, Principal principal) {
        log.debug("Filtering applications by access level: {}", accessLvl);
        List<Application> applications;
        AccessLevel parsedLevel = parseAccessLevel(accessLvl);
        if (parsedLevel == null) {
            applications = applicationService.listOfFreeApplications();
        } else {
            applications = applicationService.getApplicationsByAccessLvl(parsedLevel);
        }
        model.addAttribute(ModelAttributeConstants.APPLICATIONS, applications);
        modelAttributeHelper.addCommonAttributes(model, principal);
        return ViewConstants.DIRECTOR_APPLICATIONS;
    }

    /**
     * Фильтрует заявки по имени
     *
     * @param name      имя для поиска (если пусто, возвращает все заявки)
     * @param model     модель для передачи данных в представление
     * @param principal текущий аутентифицированный пользователь
     * @return имя представления для отображения отфильтрованного списка заявок
     */
    @GetMapping("/director/getApplicationsByName")
    public String getApplication(@RequestParam(required = false) String name, Model model, Principal principal) {
        log.debug("Filtering applications by name: {}", name);
        List<Application> applications;
        if (name != null && !name.isEmpty()) {
            applications = applicationService.getApplicationsByName(name);
        } else {
            applications = applicationService.listOfFreeApplications();
        }
        model.addAttribute(ModelAttributeConstants.APPLICATIONS, applications);
        modelAttributeHelper.addCommonAttributes(model, principal);
        return ViewConstants.DIRECTOR_APPLICATIONS;
    }

    /**
     * Отображает страницу управления отчетами
     *
     * @param model     модель для передачи данных в представление
     * @param principal текущий аутентифицированный пользователь
     * @return имя представления для отображения списка отчетов
     */
    @GetMapping("/director/reports")
    public String manageReports(Model model, Principal principal) {
        log.debug("Director accessing reports management page");
        User director = userService.getByEmail(principal.getName());
        if (director == null || director.getDepartment() == null) {
            model.addAttribute(ModelAttributeConstants.REPORTS, List.of());
            model.addAttribute("reportStats", new DirectorReportStatsDto(0, 0, 0, 0));
            modelAttributeHelper.addCommonAttributes(model, principal);
            return ViewConstants.DIRECTOR_REPORTS;
        }
        List<Report> reports = reportService.listOfDepartmentReports(director.getDepartment().getId());
        DirectorReportStatsDto stats = reportService.buildDepartmentStats(reports);
        model.addAttribute(ModelAttributeConstants.REPORTS, reports);
        model.addAttribute("reportStats", stats);
        modelAttributeHelper.addCommonAttributes(model, principal);
        return ViewConstants.DIRECTOR_REPORTS;
    }

    @GetMapping("/director/reports/export")
    public ResponseEntity<byte[]> exportDepartmentReports(Principal principal) {
        User director = userService.getByEmail(principal.getName());
        if (director == null || director.getDepartment() == null) {
            return ResponseEntity.badRequest().build();
        }
        List<Report> reports = reportService.listOfDepartmentReports(director.getDepartment().getId());
        DirectorReportStatsDto stats = reportService.buildDepartmentStats(reports);

        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            CellStyle labelStyle = workbook.createCellStyle();
            Font labelFont = workbook.createFont();
            labelFont.setBold(true);
            labelStyle.setFont(labelFont);
            labelStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            XSSFSheet statsSheet = workbook.createSheet("Статистика");
            int rowIndex = 0;
            Row header = statsSheet.createRow(rowIndex++);
            header.setHeightInPoints(18);
            Cell h0 = header.createCell(0);
            h0.setCellValue("Показатель");
            h0.setCellStyle(headerStyle);
            Cell h1 = header.createCell(1);
            h1.setCellValue("Значение");
            h1.setCellStyle(headerStyle);
            Row r1 = statsSheet.createRow(rowIndex++);
            Cell r10 = r1.createCell(0);
            r10.setCellValue("Всего отчетов");
            r10.setCellStyle(labelStyle);
            r1.createCell(1).setCellValue(stats.getTotalReports());
            Row r2 = statsSheet.createRow(rowIndex++);
            Cell r20 = r2.createCell(0);
            r20.setCellValue("Уникальных сотрудников");
            r20.setCellStyle(labelStyle);
            r2.createCell(1).setCellValue(stats.getUniqueEmployees());
            Row r3 = statsSheet.createRow(rowIndex++);
            Cell r30 = r3.createCell(0);
            r30.setCellValue("Выполненных заявок");
            r30.setCellStyle(labelStyle);
            r3.createCell(1).setCellValue(stats.getCompletedApplications());
            Row r4 = statsSheet.createRow(rowIndex++);
            Cell r40 = r4.createCell(0);
            r40.setCellValue("Среднее отчетов на сотрудника");
            r40.setCellStyle(labelStyle);
            r4.createCell(1).setCellValue(stats.getAvgReportsPerEmployee());

            statsSheet.autoSizeColumn(0);
            statsSheet.autoSizeColumn(1);
            statsSheet.createFreezePane(0, 1);

            XSSFSheet reportsSheet = workbook.createSheet("Отчеты");
            Row reportsHeader = reportsSheet.createRow(0);
            reportsHeader.setHeightInPoints(18);
            String[] cols = new String[]{"Дата", "Сотрудник", "Заявка", "Содержание отчета", "Статус заявки"};
            for (int i = 0; i < cols.length; i++) {
                Cell c = reportsHeader.createCell(i);
                c.setCellValue(cols[i]);
                c.setCellStyle(headerStyle);
            }

            int reportRowIndex = 1;
            for (Report report : reports) {
                Row row = reportsSheet.createRow(reportRowIndex++);
                row.createCell(0).setCellValue(report.getCreatedAtFormatted());
                row.createCell(1).setCellValue(report.getApplication().getUser().getName());
                row.createCell(2).setCellValue(report.getApplication().getName());
                row.createCell(3).setCellValue(report.getReportDisc());
                row.createCell(4).setCellValue(report.getApplication().isCompleted() ? "Выполнена" : "В работе");
            }

            for (int i = 0; i < cols.length; i++) {
                reportsSheet.autoSizeColumn(i);
                int current = reportsSheet.getColumnWidth(i);
                reportsSheet.setColumnWidth(i, Math.min(current + 1024, 15000));
            }
            reportsSheet.createFreezePane(0, 1);

            workbook.write(out);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=department-reports.xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(out.toByteArray());
        } catch (Exception e) {
            log.error("Error exporting reports to Excel", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Отображает детальную информацию об отчете
     *
     * @param id        ID отчета
     * @param model     модель для передачи данных в представление
     * @param principal текущий аутентифицированный пользователь
     * @return имя представления для отображения деталей отчета
     */
    @GetMapping("/director/report-info/{id}")
    public String getReportDetails(@PathVariable Long id, Model model, Principal principal) {
        log.debug("Director accessing report details for ID: {}", id);
        try {
            Report report = reportService.getById(id);
            model.addAttribute(ModelAttributeConstants.REPORT, report);
            modelAttributeHelper.addCommonAttributes(model, principal);
            return ViewConstants.DIRECTOR_REPORT_INFO;
        } catch (Exception e) {
            log.error("Error accessing report details for ID: {}", id, e);
            throw new ResourceNotFoundException("Report", id);
        }
    }


    /**
     * Удаляет заявку
     *
     * @param id ID заявки для удаления
     * @return перенаправление на список заявок
     */
    @GetMapping("/director/deleteApplication/{id}")
    public String deleteApplication(@PathVariable Long id) {
        log.info("Deleting application ID: {}", id);
        try {
            applicationService.deleteApplication(id);
            log.info("Application ID: {} successfully deleted", id);
            return RedirectConstants.REDIRECT_DIRECTOR_APPLICATIONS;
        } catch (Exception e) {
            log.error("Error deleting application ID: {}", id, e);
            throw new ResourceNotFoundException("Application", id);
        }
    }

    @GetMapping("/director/temp-access-requests")
    public String tempAccessRequests(Model model, Principal principal) {
        User director = userService.getByEmail(principal.getName());
        if (director == null || director.getDepartment() == null) {
            model.addAttribute("requests", List.of());
            modelAttributeHelper.addCommonAttributes(model, principal);
            return ViewConstants.DIRECTOR_TEMP_ACCESS_REQUESTS;
        }
        model.addAttribute("requests",
                temporaryAccessService.listRequestsForDirectorDepartment(director.getDepartment().getId()));
        modelAttributeHelper.addCommonAttributes(model, principal);
        return ViewConstants.DIRECTOR_TEMP_ACCESS_REQUESTS;
    }

    @GetMapping("/director/temp-access-requests/{id}")
    public String tempAccessRequestInfo(@PathVariable Long id, Model model, Principal principal) {
        model.addAttribute("request", temporaryAccessService.getRequest(id));
        modelAttributeHelper.addCommonAttributes(model, principal);
        return ViewConstants.DIRECTOR_TEMP_ACCESS_REQUEST_INFO;
    }

    @GetMapping("/director/temp-access-requests/{id}/approve")
    public String approveTempAccess(@PathVariable Long id, Principal principal, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        User director = userService.getByEmail(principal.getName());
        try {
            temporaryAccessService.approveRequest(id, director.getId());
            redirectAttributes.addFlashAttribute(ModelAttributeConstants.SUCCESS_MESSAGE, "Временный допуск одобрен");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ModelAttributeConstants.ERROR_MESSAGE, "Ошибка: " + e.getMessage());
        }
        return RedirectConstants.REDIRECT_DIRECTOR_TEMP_ACCESS_REQUESTS;
    }

    @GetMapping("/director/temp-access-requests/{id}/deny")
    public String denyTempAccess(@PathVariable Long id, Principal principal, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        User director = userService.getByEmail(principal.getName());
        try {
            temporaryAccessService.denyRequest(id, director.getId());
            redirectAttributes.addFlashAttribute(ModelAttributeConstants.SUCCESS_MESSAGE, "Запрос отклонён");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ModelAttributeConstants.ERROR_MESSAGE, "Ошибка: " + e.getMessage());
        }
        return RedirectConstants.REDIRECT_DIRECTOR_TEMP_ACCESS_REQUESTS;
    }

}
