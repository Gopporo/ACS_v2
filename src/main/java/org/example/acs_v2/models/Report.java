package org.example.acs_v2.models;

import javax.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Data
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "report_disc", nullable = false)
    private String reportDisc; // Содержание отчета

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; // Дата и время создания отчета

    @Transient
    private String createdAtFormatted; // Поле для форматированной даты

    @OneToOne
    @JoinColumn(name = "application_id", nullable = false, unique = true) // Один отчет связан с одной заявкой
    private Application application;

}

