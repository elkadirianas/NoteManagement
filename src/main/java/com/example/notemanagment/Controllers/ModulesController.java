package com.example.notemanagment.Controllers;


import com.example.notemanagment.Models.Module;
import com.example.notemanagment.Models.ModuleDto;
import com.example.notemanagment.Models.ModuleElement;
import com.example.notemanagment.Repository.FieldRepo;
import com.example.notemanagment.Repository.ModuleElementRepo;
import com.example.notemanagment.Repository.ModuleRepo;
import com.example.notemanagment.Repository.ProfRepo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/Dashboard/admin/Managemodules")
public class ModulesController {

    @Autowired
    private ModuleRepo moduleRepo;



    @Autowired
    private ProfRepo profRepo;

    @Autowired
    private FieldRepo fieldRepo;

    @GetMapping({"/Managemodules"})
    public String showModules(Model model) {
        var modules = moduleRepo.findAll(Sort.by(Sort.Direction.ASC, "id"));
        model.addAttribute("modules", modules);
        return "Dashboard/admin/Managemodules";
    }

    @GetMapping("/createModule")
    public String createModule(Model model) {
        ModuleDto moduleDto = new ModuleDto();
        model.addAttribute("moduleDto", moduleDto);
        var professors = profRepo.findAll(Sort.by(Sort.Direction.ASC, "id"));
        model.addAttribute("professors", professors);
        var fields = fieldRepo.findAll(Sort.by(Sort.Direction.ASC, "id"));
        model.addAttribute("fields", fields);
        return "Dashboard/admin/createModule";
    }


}
