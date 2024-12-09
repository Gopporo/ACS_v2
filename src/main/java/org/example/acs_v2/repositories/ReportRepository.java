package org.example.acs_v2.repositories;

import org.example.acs_v2.models.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
