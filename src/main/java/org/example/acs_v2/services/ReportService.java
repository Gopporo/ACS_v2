package org.example.acs_v2.services;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.acs_v2.models.Application;
import org.example.acs_v2.models.Report;
import org.example.acs_v2.models.User;
import org.example.acs_v2.repositories.ApplicationRepository;
import org.example.acs_v2.repositories.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");

    public Report createReport(Long applicationId, Report report) {
        Application application = applicationRepository.findById(applicationId).orElseThrow(() ->
                new RuntimeException("Application with ID " + applicationId + " not found"));

        report.setCreatedAt(LocalDateTime.now());
        report.setApplication(application);

        application.setCompleted(true); // Обновляем статус заявки
        applicationRepository.save(application);

        return reportRepository.save(report);
    }

    public List<Report> list() {
        List<Report> reports = reportRepository.findAll();
        for (Report report : reports) {
            report.setCreatedAtFormatted(report.getCreatedAt().format(formatter));
        }
        return reports;
    }

    public List<Report> listOfUserReports(Long userId) {
        return reportRepository.findAll().stream()
                .filter(report -> report.getApplication().getUser().getId().equals(userId))
                .peek(report -> report.setCreatedAtFormatted(report.getCreatedAt().format(formatter)))
                .collect(Collectors.toList());
    }

    public Report getById(Long id) {
        Report report = reportRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Report with ID " + id + " not found"));
        report.setCreatedAtFormatted(report.getCreatedAt().format(formatter));
        return report;
    }

}

