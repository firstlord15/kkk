package com.example.project3.controller;

import com.example.officeapp.model.User;
import com.example.officeapp.service.PresenceService;
import com.example.officeapp.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    private final UserService userService;
    private final PresenceService presenceService;

    public DashboardController(UserService userService, PresenceService presenceService) {
        this.userService = userService;
        this.presenceService = presenceService;
    }

    @GetMapping
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String email = userDetails.getUsername();
        User user = userService.findByEmail(email).orElseThrow();
        presenceService.recordUserEntry(user);
        model.addAttribute("user", user);
        return "dashboard";
    }
}