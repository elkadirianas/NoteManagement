package com.example.notemanagment.Controllers;

import com.example.notemanagment.Models.Field;
import com.example.notemanagment.Models.Module;
import com.example.notemanagment.Models.ModuleDto;
import com.example.notemanagment.Repository.FieldRepo;
import com.example.notemanagment.Repository.ModuleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        var modules = moduleRepo.findAll(Sort.by(Sort.Direction.ASC,"id"));
        model.addAttribute("modules", modules);
        return "/Dashboard/admin/Managemodules";
    }


    @GetMapping("/createModule")
    public String createModule(Model model) {
        ModuleDto moduleDto = new ModuleDto();
        model.addAttribute("moduleDto", moduleDto);

        // Fetch all fields to populate the dropdown
        var fields = fieldRepo.findAll(Sort.by(Sort.Direction.ASC, "id"));
        model.addAttribute("fields", fields);

        return "Dashboard/admin/createModule";
    }

    @PostMapping("/createModule")
    public String saveModule(
            @ModelAttribute("moduleDto") ModuleDto moduleDto,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        // Validate semester
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

        // Fetch the selected field
        Field selectedField = fieldRepo.findById(moduleDto.getFieldId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid field ID"));

        // Map DTO to Module entity
        Module module = new Module();
        module.setName(moduleDto.getName());
        module.setCode(moduleDto.getCode());
        module.setSemester(Module.Semester.valueOf(moduleDto.getSemester()));
        module.setField(selectedField); // Set the single field relationship

        // Save the module
        moduleRepo.save(module);

        // Add success message
        redirectAttributes.addFlashAttribute("successMessage", "Module created successfully!");
        return "redirect:/Dashboard/admin/Managemodules";
    }

    @GetMapping("/deleteModule")
    public String deleteModule(@RequestParam Long id) {
        Module module = moduleRepo.findById(Math.toIntExact(id)).orElse(null);

        if (module != null) {
            moduleRepo.delete(module);
        }

        return "redirect:/Dashboard/admin/Managemodules";
    }
}
