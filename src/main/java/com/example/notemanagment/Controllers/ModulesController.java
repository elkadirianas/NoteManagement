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
    @GetMapping("/createModule/{fieldId}")
    public String createModule(@PathVariable Integer fieldId, Model model) {
        ModuleDto moduleDto = new ModuleDto();
        moduleDto.setFieldId(fieldId); // Pre-set the fieldId in the DTO
        model.addAttribute("moduleDto", moduleDto);

        return "Dashboard/admin/createModule";
    }

    @PostMapping("/createModule/{fieldId}")
    public String saveModule(
            @PathVariable Integer fieldId,
            @ModelAttribute("moduleDto") ModuleDto moduleDto,
            BindingResult result,
            RedirectAttributes redirectAttributes
    ) {
        // Validate semester
        try {
            Module.Semester selectedSemester = Module.Semester.valueOf(moduleDto.getSemester());
        } catch (IllegalArgumentException e) {
            result.rejectValue("semester", "Invalid.semester", "Invalid semester selected.");
            return "Dashboard/admin/createModule";
        }

        if (result.hasErrors()) {
            return "Dashboard/admin/createModule";
        }

        // Fetch the field by fieldId
        Field selectedField = fieldRepo.findById(fieldId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid field ID"));

        // Map DTO to Module entity
        Module module = new Module();
        module.setName(moduleDto.getName());
        module.setCode(moduleDto.getCode());
        module.setSemester(Module.Semester.valueOf(moduleDto.getSemester()));
        module.setField(selectedField); // Link the module to the field

        // Save the module
        moduleRepo.save(module);

        // Add success message
        redirectAttributes.addFlashAttribute("successMessage", "Module created successfully!");
        return "redirect:/Dashboard/admin/Managefields";
    }

    @PostMapping("/deleteModule/{moduleId}")
    public String deleteModule(@PathVariable Long moduleId) {
        moduleRepo.deleteById(Math.toIntExact(moduleId));
        return "redirect:/Dashboard/admin/Managefields";
    }




//    @GetMapping("/deleteModule")
//    public String deleteModule(@RequestParam Long id) {
//        Module module = moduleRepo.findById(Math.toIntExact(id)).orElse(null);
//
//        if (module != null) {
//            moduleRepo.delete(module);
//        }
//
//        return "redirect:/Dashboard/admin/Managemodules";
//    }
}
