package org.example.acs_v2.models;

import javax.persistence.*;
import lombok.Data;


@Entity
@Table(name = "applications")
@Data
public class Application{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "access_lvl")
    private int accessLevel;

    @Column(name = "disc")
    private String disc;

    @ManyToOne
    @JoinColumn(name = "zone_id")
    private Zone zone;

    @ManyToOne
    @JoinColumn(name = "user_id") // Связь с таблицей пользователей
    private User user; // Пользователь, который принял заявку

    @OneToOne(mappedBy = "application") // Обратная связь с Report
    private Report report;

}
