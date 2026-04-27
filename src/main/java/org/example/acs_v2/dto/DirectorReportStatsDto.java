package org.example.acs_v2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DirectorReportStatsDto {
    private long totalReports;
    private long uniqueEmployees;
    private long completedApplications;
    private long avgReportsPerEmployee;
}
