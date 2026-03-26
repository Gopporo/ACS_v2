package org.example.acs_v2.models;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "access_attempts")
@Data
public class AccessAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timestamp;

    @ManyToOne
    private Worker worker;

    @ManyToOne
    private Door door;

    private boolean success;
}

