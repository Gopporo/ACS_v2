package org.example.acs_v2.models;

import org.example.acs_v2.models.enums.AccessLevel;

import javax.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "zones")
@Data
public class Zone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "disc")
    private String disc;

    @Enumerated(EnumType.STRING)
    @Column(name = "zone_access_lvl")
    private AccessLevel zoneAccessLvl;

    @OneToMany(mappedBy = "zone", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Application> applications;
}
