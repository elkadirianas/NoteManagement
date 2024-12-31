package com.example.notemanagment.Controllers;

import com.example.notemanagment.Models.Field;
import com.example.notemanagment.Models.Module;
import com.example.notemanagment.Models.ModuleDto;
import com.example.notemanagment.Models.ModuleFieldCombination;
import com.example.notemanagment.Repository.FieldRepo;
import com.example.notemanagment.Repository.ModuleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/Dashboard/admin")
public class ModulesController {
    @Autowired
    private FieldRepo fieldRepo;
    @Autowired
    private ModuleRepo moduleRepo;
    @GetMapping("/Managemodules")
    public String manageModules(Model model) {
        List<Module> modules = moduleRepo.findAll();
        List<ModuleFieldCombination> moduleFieldCombinations = new ArrayList<>();

        for (Module module : modules) {
            if (module.getFields().isEmpty()) {
                moduleFieldCombinations.add(new ModuleFieldCombination(module, null));  // No fields associated
            } else {
                for (Field field : module.getFields()) {
                    moduleFieldCombinations.add(new ModuleFieldCombination(module, field));
                }
            }
        }

        model.addAttribute("moduleFieldCombinations", moduleFieldCombinations);
        return "Dashboard/admin/ManageModules";  // Thymeleaf template name
    }



    @GetMapping({"/createModule"})
    public String createModule(Model model){
        ModuleDto moduleDto = new ModuleDto();

        model.addAttribute("moduleDto",moduleDto);
        var fields = fieldRepo.findAll(Sort.by(Sort.Direction.ASC,"id"));
        model.addAttribute("fields",fields);
        return "Dashboard/admin/createModule";
    }
    // Delete Module from Field

    @GetMapping("/deleteModuleField")
    public String deleteModuleField(@RequestParam Long moduleId, @RequestParam(required = false) Integer fieldId) {
        Module module = moduleRepo.findById(Math.toIntExact(moduleId)).orElse(null);

        if (module != null) {
            if (fieldId != null) {
                // If fieldId is provided, remove the field from the module's fields list
                Field field = fieldRepo.findById(fieldId).orElse(null);
                if (field != null) {
                    module.getFields().remove(field);
                }
            }

            // Check if the module has any fields left
            if (module.getFields().isEmpty()) {
                // If no fields are associated, delete the module entirely
                moduleRepo.delete(module);
            } else {
                // Save the updated module (with field removed)
                moduleRepo.save(module);
            }
        }

        return "redirect:/Dashboard/admin/Managemodules";
    }


    // Delete Module entirely if it has only one associated field
    @GetMapping("/deleteModule")
    public String deleteModule(@RequestParam int id) {
        Module module = moduleRepo.findById(id).orElse(null);
        if (module != null && module.getFields().isEmpty()) {
            // If module is not associated with any fields, delete it
            moduleRepo.delete(module);
        } else {
            // If the module is associated with multiple fields, remove the module-field relationship
            for (Field field : module.getFields()) {
                field.getModules().remove(module);
            }

            // Save the module to update the join table
            moduleRepo.save(module);

            // Delete the module if it's no longer needed
            moduleRepo.delete(module);
        }

        return "redirect:/Dashboard/admin/Managemodules";
    }


    @PostMapping("/createModule")
    public String saveModule(
            @ModelAttribute("moduleDto") ModuleDto moduleDto,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        // Validate the semester input
        try {
            Module.Semester selectedSemester = Module.Semester.valueOf(moduleDto.getSemester());
        } catch (IllegalArgumentException e) {
            result.rejectValue("semester", "Invalid.semester", "Invalid semester selected.");
            model.addAttribute("fields", fieldRepo.findAll(Sort.by(Sort.Direction.ASC, "id")));
            return "Dashboard/admin/createModule";
        }

        if (result.hasErrors()) {
            model.addAttribute("fields", fieldRepo.findAll(Sort.by(Sort.Direction.ASC, "id")));
            return "Dashboard/admin/createModule";
        }

        // Map DTO to Module entity
        Module module = new Module();
        module.setName(moduleDto.getName());
        module.setCode(moduleDto.getCode());
        module.setSemester(Module.Semester.valueOf(moduleDto.getSemester()));

        // Fetch and set fields
        List<Field> selectedFields = fieldRepo.findAllById(moduleDto.getFieldIds());
        module.setFields(selectedFields);

        // Save the module
        moduleRepo.save(module);

        // Add success message
        redirectAttributes.addFlashAttribute("successMessage", "Module created successfully!");
        return "redirect:/Dashboard/admin/Managemodules";
    }

}
