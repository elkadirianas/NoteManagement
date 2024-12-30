package com.example.notemanagment.Controllers;

import com.example.notemanagment.Models.*;
import com.example.notemanagment.Models.Module;
import com.example.notemanagment.Repository.FieldRepo;
import com.example.notemanagment.Repository.ModuleRepo;
import com.example.notemanagment.Repository.ProfRepo;
import com.example.notemanagment.Repository.UserRepo;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/Dashboard/admin")
public class AdminController {
    @Autowired
    private HttpSession session;
    @Autowired
    private UserRepo userrepo ;
    @Autowired
    private ProfRepo profRepo;
    @Autowired
    private FieldRepo fieldRepo;
    @Autowired
    private ModuleRepo moduleRepo;
    @GetMapping({"","/"})
    public String showAdminDashboard(Model model) {
        int userId =  (int) session.getAttribute("userId");
        model.addAttribute("userId", userId);
        var users = userrepo.findAll(Sort.by(Sort.Direction.ASC,"id"));
        model.addAttribute("users",users);
        return "Dashboard/admin/index";
    }
    @GetMapping({"/createuser"})
    public String createUser(Model model){
        UserDto userDto = new UserDto();
        model.addAttribute("userDto",userDto);
        return "Dashboard/admin/createuser";
    }
    @PostMapping("/createuser")
    public String createClient(@Valid @ModelAttribute UserDto userDto, BindingResult result){
        if(userrepo.findByUsername(userDto.getUsername())!=null){
            result.addError(
                    new FieldError("userDto","username",userDto.getUsername(),false,null,null,"username is already used ")
            );
        }
        if (!("admin".equals(userDto.getRole())|| "prof".equals(userDto.getRole()))) {
            result.addError(
                    new FieldError("userDto","role",userDto.getRole(),false,null,null,"role can either be admin or prof ")
            );
        }
        if(result.hasErrors()){
            return "Dashboard/admin/createuser";
        }
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        user.setRole(userDto.getRole());
        userrepo.save(user);
        return "redirect:/Dashboard/admin";
    }

    @GetMapping("/edit")
    public String editUser(Model model, @RequestParam int id) {
        User user = userrepo.findById(id).orElse(null);
        if (user == null) {
            return "redirect:/Dashboard/admin";
        }
        UserDto userDto = new UserDto();
        userDto.setUsername(user.getUsername());
        userDto.setPassword(user.getPassword());
        userDto.setRole(user.getRole());
        model.addAttribute("userDto", userDto);
        model.addAttribute("user", user); // Pass the user ID to the model for form submission
        return "Dashboard/admin/edit"; // Return the template name
    }

    @PostMapping("/edit")
    public String editUser(
            @RequestParam int id,
            @Valid @ModelAttribute UserDto userDto,
            BindingResult result,
            Model model) {
        User user = userrepo.findById(id).orElse(null);
        if (user == null) {
            return "redirect:/Dashboard/admin";
        }
        model.addAttribute("userId", id); // Keep the ID for reuse in the form

        if (result.hasErrors()) {
            return "Dashboard/admin/edit"; // Return to the edit page with errors
        }

        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        user.setRole(userDto.getRole());

        try {
            userrepo.save(user);
        } catch (Exception ex) {
            result.addError(new FieldError(
                    "userDto", "username", userDto.getUsername(), false, null, null, "Username already used"));
            return "Dashboard/admin/edit"; // Return to the edit page with error
        }

        return "redirect:/Dashboard/admin";
    }

    @GetMapping("/delete")
    public String deleteUser(@RequestParam int id){
        User user = userrepo.findById(id).orElse(null);
        if(user!=null){
            userrepo.delete(user);
        }
        return "redirect:/Dashboard/admin";
    }
    @GetMapping({"/ManageProfs"})
    public String ShowProfs(Model model) {
//        int userId =  (int) session.getAttribute("userId");
//        model.addAttribute("userId", userId);
        var profs = profRepo.findAll(Sort.by(Sort.Direction.ASC,"id"));
        model.addAttribute("profs",profs);
        return "Dashboard/admin/ManageProfs";
    }


    @GetMapping({"/AddProf"})
    public String AddProf(Model model,@RequestParam int id){
        User user = userrepo.findById(id).orElse(null);
        ProfessorDto profDto = new ProfessorDto();
        model.addAttribute("profDto",profDto);
        model.addAttribute("user", user);
        return "Dashboard/admin/AddProf";
    }

    @PostMapping("/AddProf")
    public String Addprof(
            @RequestParam int id,
            @Valid @ModelAttribute ProfessorDto profDto,
            BindingResult result,
            Model model) {
        // Check if the User exists
        User user = userrepo.findById(id).orElse(null);
        if (user == null) {
            return "redirect:/Dashboard/admin";
        }

        // Validate inputs
//        if (result.hasErrors()) {
//            model.addAttribute("user", user); // Pass the user back to the form
//            return "Dashboard/admin/AddProf";
//        }

        // Check if a Professor already exists for this ID
        Professor prof = profRepo.findById(id).orElse(new Professor());
        prof.setId(id); // Set the professor ID
        prof.setName(profDto.getName());
        prof.setSurname(profDto.getSurname());
        prof.setCode(profDto.getCode());
        prof.setSpecialty(profDto.getSpecialty());

        try {
            profRepo.save(prof); // Save the professor
        } catch (Exception ex) {
            result.addError(new FieldError(
                    "profDto", "code", profDto.getCode(), false, null, null, "Code already used"));
            model.addAttribute("user", user); // Pass the user back to the form
            return "Dashboard/admin/AddProf"; // Return to the form with errors
        }

        return "redirect:/Dashboard/admin";
    }
    @GetMapping("/deleteprof")
    public String deleteProf(@RequestParam int id){
        Professor prof = profRepo.findById(id).orElse(null);
        if(prof!=null){
            profRepo.delete(prof);
        }
        return "redirect:/Dashboard/admin/ManageProfs";
    }
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
    public String createClient(@Valid @ModelAttribute FieldDto fieldDto, BindingResult result){
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
