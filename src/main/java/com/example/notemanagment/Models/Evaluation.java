package com.example.notemanagment.Models;

import jakarta.persistence.*;

@Entity
@Table(name = "evaluations")
public class Evaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "module_element_id", nullable = false)
    private ModuleElement moduleElement;
    @Column(nullable = false)
    private String evaluationMode;

    @Column(nullable = false)
    private Double coefficient;

    // Getters and setters
}