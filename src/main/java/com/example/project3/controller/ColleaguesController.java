package com.example.project3.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.project3.service.UserService;

@Controller
@RequestMapping("/colleagues")
public class ColleaguesController {
    private final UserService userService;

    public ColleaguesController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String showColleagues(Model model) {
        model.addAttribute("userService", userService);
        return "colleagues";
    }
}