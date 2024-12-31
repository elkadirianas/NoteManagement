package com.example.notemanagment.Controllers;

import com.example.notemanagment.Models.User;
import com.example.notemanagment.Models.UserDto;
import com.example.notemanagment.Repository.UserRepo;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.FieldError;
@Controller
@RequestMapping("/Login")
public class Login_Controller {
    @Autowired
    private UserRepo userRepo;
    @GetMapping({""})
    public String Login(Model model){
        UserDto userDto = new UserDto();
        model.addAttribute("userDto",userDto);
        return "Login/index";
    }
    @PostMapping
    public String Login(@Valid @ModelAttribute UserDto userDto, BindingResult result, HttpSession session) {
        System.out.println("Attempting login for username: " + userDto.getUsername());

        // Fetch user from database
        User user = userRepo.findByUsername(userDto.getUsername().trim());
        if (user == null) {
            System.out.println("User not found in database.");
            result.addError(
                    new FieldError("userDto", "username", userDto.getUsername(), false, null, null, "Username not found")
            );
            return "Login/index";
        }

        // Check the role and password
        if ("admin".equals(user.getRole()) && userDto.getPassword().equals(user.getPassword())) {
            session.setAttribute("userId", user.getId()); // Store userId in session
            return "redirect:/Dashboard/admin";
        } else if ("prof".equals(user.getRole()) && userDto.getPassword().equals(user.getPassword())) {
            session.setAttribute("userId", user.getId()); // Store userId in session
            return "redirect:/Dashboard/prof";
        } else {
            result.addError(
                    new FieldError("userDto", "role", user.getRole(), false, null, null, "Invalid role")
            );
            return "Login/index";
        }
    }
}
