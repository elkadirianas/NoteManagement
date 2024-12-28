package com.example.notemanagment.Models;


import jakarta.persistence.*;

@Entity
@Table(name = "module_elements")
public class ModuleElement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double coefficient;

    @ManyToOne
    @JoinColumn(name = "professor_id", nullable = false)
    private Professor professor;

    // Getters and setters
}