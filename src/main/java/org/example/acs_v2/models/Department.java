package org.example.acs_v2.models;

import javax.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "departments")
@Data
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> users;

    @OneToOne
    @JoinColumn(name = "head_id", unique = true, nullable = false) // Связь с таблицей User для руководителя
    private User head;
}
