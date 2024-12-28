package com.example.notemanagment.Models;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "modules")
public class Module {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String code;

    @ManyToMany
    @JoinTable(
            name = "module_field",
            joinColumns = @JoinColumn(name = "module_id"),
            inverseJoinColumns = @JoinColumn(name = "field_id")
    )
    private List<Field> fields;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Semester semester;

    public enum Semester {
        S1, S2, S3, S4, S5
    }

    // Getters and setters
}