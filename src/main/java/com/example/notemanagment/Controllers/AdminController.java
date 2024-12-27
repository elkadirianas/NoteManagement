package com.example.notemanagment.Controllers;

import com.example.notemanagment.Models.User;
import com.example.notemanagment.Models.UserDto;
import com.example.notemanagment.Repository.UserRepo;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/Dashboard/admin")
public class AdminController {
    @Autowired
    private HttpSession session;
    @Autowired
    private UserRepo userrepo ;
    @GetMapping({"","/"})
    public String showAdminDashboard(Model model) {
        int userId =  (int) session.getAttribute("userId");
        model.addAttribute("userId", userId);
        var users = userrepo.findAll(Sort.by(Sort.Direction.DESC,"id"));
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
}
