package com.example.notemanagment.Models;


import java.util.List;

public class ModuleDto {
    private Long id;
    private String name;
    private String code;
    private List<Integer> fieldIds; // To represent the associated fields by their IDs
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

    public List<Integer> getFieldIds() {
        // Return an immutable copy of the fieldIds list to prevent external modification
        return fieldIds == null ? List.of() : List.copyOf(fieldIds);
    }

    public void setFieldIds(List<Integer> fieldIds) {
        // Ensure the fieldIds list is not null
        this.fieldIds = fieldIds == null ? List.of() : List.copyOf(fieldIds);
    }


    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }
}
