package com.example.notemanagment.Controllers;


import com.example.notemanagment.Models.Module;
import com.example.notemanagment.Models.ModuleElement;
import com.example.notemanagment.Models.Professor;
import com.example.notemanagment.Repository.ModuleRepo;
import com.example.notemanagment.Repository.ModuleElementRepository;
import com.example.notemanagment.Repository.ProfRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/Dashboard/admin")
public class ModuleElementController {

    @Autowired
    private ModuleRepo moduleRepository;

    @Autowired
    private ModuleElementRepository moduleElementRepository;
    @Autowired
    private ProfRepo professorRepository;

    // Show elements in a module
    @GetMapping("/ShowElements/{moduleId}")
    public String showElements(@PathVariable Long moduleId, Model model) {
        // Find the module by ID
        Module module = moduleRepository.findById(Math.toIntExact(moduleId))
                .orElseThrow(() -> new IllegalArgumentException("Invalid module ID: " + moduleId));

        // Find all elements associated with the module
        List<ModuleElement> elements = moduleElementRepository.findByModuleId(moduleId);

        // Pass data to the Thymeleaf view
        model.addAttribute("module", module);
        model.addAttribute("elements", elements);

        return "Dashboard/admin/showElements"; // The HTML file created above
    }
    @GetMapping("/createElement/{moduleId}")
    public String showCreateElementForm(@PathVariable Long moduleId, Model model) {
        // Retrieve the module by ID
        Module module = moduleRepository.findById(Math.toIntExact(moduleId))
                .orElseThrow(() -> new IllegalArgumentException("Invalid module ID: " + moduleId));

        // Retrieve all professors
        List<Professor> professors = professorRepository.findAll();

        // Pass data to the view
        model.addAttribute("module", module);
        model.addAttribute("professors", professors);

        return "Dashboard/admin/createElement"; // The HTML file created above
    }

    // Handle the creation of a new module element
    @PostMapping("/createElement")
    public String createElement(
            @RequestParam Long moduleId,
            @RequestParam String name,
            @RequestParam Double coefficient,
            @RequestParam Long professorId) {

        // Find the module and professor by ID
        Module module = moduleRepository.findById(Math.toIntExact(moduleId))
                .orElseThrow(() -> new IllegalArgumentException("Invalid module ID: " + moduleId));
        Professor professor = professorRepository.findById(Math.toIntExact(professorId))
                .orElseThrow(() -> new IllegalArgumentException("Invalid professor ID: " + professorId));

        // Create and save the new module element
        ModuleElement moduleElement = new ModuleElement();
        moduleElement.setModule(module);
        moduleElement.setName(name);
        moduleElement.setCoefficient(coefficient);
        moduleElement.setProfessor(professor);

        moduleElementRepository.save(moduleElement);

        // Redirect to the ShowElements page for the module
        return "redirect:/Dashboard/admin/ShowElements/" + moduleId;
    }
}