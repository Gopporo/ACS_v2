package org.example.acs_v2.models;

import lombok.Data;
import org.example.acs_v2.models.enums.AccessLevel;

import javax.persistence.*;

@Entity
@Table(name = "doors")
@Data
public class Door {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    private AccessLevel accessLevel;
}

