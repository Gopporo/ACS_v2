package org.example.acs_v2.models;

import lombok.Getter;
import lombok.Setter;
import org.example.acs_v2.models.enums.AccessLevel;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "temporary_access_grants",
        uniqueConstraints = @UniqueConstraint(columnNames = {"application_id", "user_id"}))
@Getter
@Setter
public class TemporaryAccessGrant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "access_level", nullable = false)
    private AccessLevel accessLevel;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    private void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}

