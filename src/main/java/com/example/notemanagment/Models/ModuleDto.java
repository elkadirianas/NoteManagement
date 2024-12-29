package com.example.notemanagment.Models;


import java.util.List;

public class ModuleDto {
    private Long id;
    private String name;
    private String code;
    private List<Long> fieldIds; // To represent the associated fields by their IDs
    private String semester;

    // Constructors

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<Long> getFieldIds() {
        return fieldIds;
    }

    public void setFieldIds(List<Long> fieldIds) {
        this.fieldIds = fieldIds;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public List<Field> getFields() {
        return List.of();
    }

    public void setFields(List<Field> fields) {
    }
}
