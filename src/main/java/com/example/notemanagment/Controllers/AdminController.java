package com.example.notemanagment.Controllers;

import com.example.notemanagment.Models.*;
import com.example.notemanagment.Models.Module;
import com.example.notemanagment.Repository.FieldRepo;
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
    @GetMapping({"/Managemodules"})
    public String ShowModules(Model model) {
//        int userId =  (int) session.getAttribute("userId");
//        model.addAttribute("userId", userId);
        var users = userrepo.findAll(Sort.by(Sort.Direction.ASC,"id"));
        model.addAttribute("users",users);
        return "Dashboard/admin/Managemodules";
    }


    @GetMapping({"/createModule"})
    public String createModule(Model model) {
        ModuleDto moduleDto = new ModuleDto();
        model.addAttribute("moduleDto", moduleDto);
        var fields = fieldRepo.findAll(Sort.by(Sort.Direction.ASC, "id"));
        model.addAttribute("fields", fields);
        return "Dashboard/admin/createModule";
    }

    @PostMapping("/createModule")
    public String createModule(@Valid @ModelAttribute ModuleDto moduleDto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            var fields = fieldRepo.findAll(Sort.by(Sort.Direction.ASC, "id"));
            model.addAttribute("fields", fields);
            return "Dashboard/admin/createModule";
        }

        // Create Module object
        Module module = new Module();
        module.setName(moduleDto.getName());
        module.setCode(moduleDto.getCode());
        module.setSemester(moduleDto.getSemester());

        // Extract and validate module elements
        String[] elementNames = moduleDto.getElementNames();
        Double[] elementCoefficients = moduleDto.getElementCoefficients();
        String[] professorNames = moduleDto.getProfessorNames();

        if (elementNames.length != elementCoefficients.length || elementNames.length != professorNames.length) {
            result.addError(new FieldError("moduleDto", "elementNames", null, false,
                    null, null, "Inconsistent number of elements provided"));
            var fields = fieldRepo.findAll(Sort.by(Sort.Direction.ASC, "id"));
            model.addAttribute("fields", fields);
            return "Dashboard/admin/createModule";
        }

        for (int i = 0; i < elementNames.length; i++) {
            if (elementNames[i].isEmpty() || professorNames[i].isEmpty()) {
                result.addError(new FieldError("moduleDto", "elementNames", null, false,
                        null, null, "Element name and professor name cannot be empty"));
                var fields = fieldRepo.findAll(Sort.by(Sort.Direction.ASC, "id"));
                model.addAttribute("fields", fields);
                return "Dashboard/admin/createModule";
            }

            // Validate coefficient (optional, adjust as needed)
            if (elementCoefficients[i] < 0 || elementCoefficients[i] > 1) {
                result.addError(new FieldError("moduleDto", "elementCoefficients", null, false,
                        null, null, "Coefficient must be between 0 and 1"));
                var fields = fieldRepo.findAll(Sort.by(Sort.Direction.ASC, "id"));
                model.addAttribute("fields", fields);
                return "Dashboard/admin/createModule";
            }
        }

        // Save module and associated elements
        try {
            module = userrepo.save(module); // Assuming UserRepo also saves Module entities
            for (int i = 0; i < elementNames.length; i++) {
                ModuleElement element = new ModuleElement();
                element.setModule(module);
                element.setName(elementNames[i]);
                element.setCoefficient(elementCoefficients[i]);
                // Find professor by name (implement logic to handle potential duplicates)
                Professor professor = profRepo.findByNameAndSurname(professorNames[i]);
                if (professor == null) {
                    professor = new Professor();
                    professor.setName(professorNames[i]); // Extract surname if available
                    professor = profRepo.save(professor);
                }
                element.setProfessor(professor);
                element = userrepo.save(element); // Assuming UserRepo also saves ModuleElement entities
            }
        } catch (Exception ex) {
            // Handle potential exceptions (e.g., database errors)
            result.addError(new FieldError("moduleDto", "general", null, false,
                    null, null, "An error occurred while saving the module and elements."));
            var fields = fieldRepo.findAll(Sort.by(Sort.Direction.ASC, "id"));
            model.addAttribute("fields", fields);
            return "Dashboard/admin/createModule";
        }

        return "redirect:/Dashboard/admin/Managemodules"; // Redirect to module management page
    }

    // ... other controller methods ...
        }
}
