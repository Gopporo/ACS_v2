package org.example.acs_v2.repositories;

import org.example.acs_v2.models.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByApplicationUserDepartmentId(Long departmentId);
}
