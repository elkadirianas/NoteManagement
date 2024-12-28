package com.example.notemanagment.Models;

import jakarta.persistence.*;

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

    @ManyToOne
    @JoinColumn(name = "field_id", nullable = false)
    private Field field;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Semester semester;

    public enum Semester {
        S1, S2, S3, S4, S5
    }

    // Getters and setters
}