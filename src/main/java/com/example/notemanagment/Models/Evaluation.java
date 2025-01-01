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

    public ModuleElement getModuleElement() {
        return moduleElement;
    }

    public void setModuleElement(ModuleElement moduleElement) {
        this.moduleElement = moduleElement;
    }

    public String getEvaluationMode() {
        return evaluationMode;
    }

    public void setEvaluationMode(String evaluationMode) {
        this.evaluationMode = evaluationMode;
    }

    public Double getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(Double coefficient) {
        this.coefficient = coefficient;
    }

    // Getters and setters
}