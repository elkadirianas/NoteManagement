package com.example.notemanagment.Controllers;

import com.example.notemanagment.Models.Field;
import com.example.notemanagment.Models.FieldDto;
import com.example.notemanagment.Models.Module;
import com.example.notemanagment.Models.ModuleDto;
import com.example.notemanagment.Repository.FieldRepo;
import com.example.notemanagment.Repository.ModuleRepo;
import com.example.notemanagment.Repository.UserRepo;
import com.example.notemanagment.Services.FieldService;
import com.example.notemanagment.Services.ModuleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/Dashboard/admin")
public class FieldsController {
    @Autowired
    private FieldRepo fieldRepo;
    @Autowired
    private UserRepo userrepo ;
    @Autowired
    private FieldService fieldService;
    @Autowired
    private ModuleRepo moduleRepo ;

    @GetMapping({"/Managefields"})
    public String ShowFiedls(Model model) {
//        int userId =  (int) session.getAttribute("userId");
//        model.addAttribute("userId", userId);
        var fields = fieldRepo.findAll(Sort.by(Sort.Direction.ASC,"id"));
        model.addAttribute("fields",fields);
        return "Dashboard/admin/Managefields";
    }


    @GetMapping({"/createfield"})
    public String createField(Model model){
        FieldDto fieldDto = new FieldDto();
        model.addAttribute("fieldDto",fieldDto);
        return "Dashboard/admin/createfield";
    }
    @PostMapping("/createfield")
    public String createField(@Valid @ModelAttribute FieldDto fieldDto, BindingResult result){
        if(userrepo.findByUsername(fieldDto.getName())!=null){
            result.addError(
                    new FieldError("fieldDto","name",fieldDto.getName(),false,null,null,"name is already used ")
            );
        }
        if(result.hasErrors()){
            return "Dashboard/admin/createfield";
        }
        Field field = new Field();
        field.setName(fieldDto.getName());
        fieldRepo.save(field);
        return "redirect:/Dashboard/admin/Managefields";
    }
    @GetMapping("/deletefield")
    public String deletefield(@RequestParam int id){
        Field field = fieldRepo.findById(id).orElse(null);
        if(field!=null){
            fieldRepo.delete(field);
        }
        return "redirect:/Dashboard/admin/Managefields";
    }
    @GetMapping("/fieldModules/{fieldId}")
    public String getFieldModules(@PathVariable Integer fieldId, Model model) {
        Field field = fieldService.findById(fieldId);
        model.addAttribute("field", field);
        model.addAttribute("modules", field.getModules());
        return "Dashboard/admin/fieldModules"; // Create this view
    }

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




}
