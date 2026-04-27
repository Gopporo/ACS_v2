package org.example.acs_v2.models;

import lombok.Getter;
import lombok.Setter;
import org.example.acs_v2.models.enums.AccessLevel;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "temporary_access_requests",
        uniqueConstraints = @UniqueConstraint(columnNames = {"application_id", "requester_id"}))
@Getter
@Setter
public class TemporaryAccessRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @ManyToOne(optional = false)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @Enumerated(EnumType.STRING)
    @Column(name = "requested_level", nullable = false)
    private AccessLevel requestedLevel;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    private void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}

