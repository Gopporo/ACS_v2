package org.example.acs_v2.services;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.acs_v2.dto.DirectorReportStatsDto;
import org.example.acs_v2.exceptions.ResourceNotFoundException;
import org.example.acs_v2.models.Application;
import org.example.acs_v2.models.Report;
import org.example.acs_v2.repositories.ApplicationRepository;
import org.example.acs_v2.repositories.ReportRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Сервис для управления отчетами
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final ApplicationRepository applicationRepository;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");

    /**
     * Создает новый отчет для заявки
     *
     * @param applicationId ID заявки
     * @param report        отчет для создания
     * @return созданный отчет
     */
    public Report createReport(Long applicationId, Report report) {
        log.debug("Creating report for application ID: {}", applicationId);
        Application application = applicationRepository.findById(applicationId).orElseThrow(() ->
                new ResourceNotFoundException("Application", applicationId));

        report.setCreatedAt(LocalDateTime.now());
        report.setApplication(application);

        application.setCompleted(true); // Обновляем статус заявки
        applicationRepository.save(application);

        Report savedReport = reportRepository.save(report);
        log.info("Report created for application ID: {}", applicationId);
        return savedReport;
    }

    /**
     * Получает список всех отчетов с форматированной датой
     *
     * @return список отчетов
     */
    public List<Report> list() {
        log.debug("Getting all reports");
        List<Report> reports = reportRepository.findAll();
        for (Report report : reports) {
            report.setCreatedAtFormatted(report.getCreatedAt().format(formatter));
        }
        return reports;
    }

    /**
     * Получает список отчетов конкретного пользователя
     *
     * @param userId ID пользователя
     * @return список отчетов пользователя
     */
    public List<Report> listOfUserReports(Long userId) {
        log.debug("Getting reports for user ID: {}", userId);
        return reportRepository.findAll().stream()
                .filter(report -> report.getApplication().getUser().getId().equals(userId))
                .peek(report -> report.setCreatedAtFormatted(report.getCreatedAt().format(formatter)))
                .collect(Collectors.toList());
    }

    /**
     * Получает отчет по ID
     *
     * @param id ID отчета
     * @return отчет с форматированной датой
     */
    public Report getById(Long id) {
        log.debug("Getting report by ID: {}", id);
        Report report = reportRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Report", id));
        report.setCreatedAtFormatted(report.getCreatedAt().format(formatter));
        return report;
    }

    public List<Report> listOfDepartmentReports(Long departmentId) {
        List<Report> reports = reportRepository.findByApplicationUserDepartmentId(departmentId);
        for (Report report : reports) {
            report.setCreatedAtFormatted(report.getCreatedAt().format(formatter));
        }
        return reports;
    }

    public DirectorReportStatsDto buildDepartmentStats(List<Report> reports) {
        long totalReports = reports.size();
        long uniqueEmployees = reports.stream()
                .map(report -> report.getApplication() != null ? report.getApplication().getUser() : null)
                .filter(Objects::nonNull)
                .map(user -> user.getId())
                .distinct()
                .count();
        long completedApplications = reports.stream()
                .map(Report::getApplication)
                .filter(Objects::nonNull)
                .filter(Application::isCompleted)
                .count();
        long avgReportsPerEmployee = uniqueEmployees == 0 ? 0 : Math.round((double) totalReports / uniqueEmployees);
        return new DirectorReportStatsDto(totalReports, uniqueEmployees, completedApplications, avgReportsPerEmployee);
    }

}

