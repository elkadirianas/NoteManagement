package com.example.notemanagment.Controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/Dashboard/prof")
public class ProfController {

    @Autowired
    private HttpSession session;

    @GetMapping
    public String showProfDashboard(Model model) {
        int userId =  (int) session.getAttribute("userId");
        // Use userId to fetch user-specific data, e.g.,
        // User user = userRepository.findById(userId).orElse(null);
        model.addAttribute("userId", userId);
        return "Dashboard/prof";
    }
}