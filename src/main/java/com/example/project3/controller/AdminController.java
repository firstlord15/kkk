package com.example.project3.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.project3.model.PresenceRecord;
import com.example.project3.model.User;
import com.example.project3.service.PresenceService;
import com.example.project3.service.UserService;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final PresenceService presenceService;

    public AdminController(UserService userService, PresenceService presenceService) {
        this.userService = userService;
        this.presenceService = presenceService;
    }

    @GetMapping
    public String adminDashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String email = userDetails.getUsername();
        User admin = userService.findByEmail(email).orElseThrow();
        model.addAttribute("admin", admin);
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/users";
    }

    @GetMapping("/reports")
    public String showReports(@RequestParam(required = false) String period, Model model) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = calculateStartDate(period, now);
        List<PresenceRecord> records = presenceService.getPresenceRecordsBetween(startDate, now);
        model.addAttribute("records", records);
        model.addAttribute("period", period != null ? period : "day");
        return "admin/reports";
    }

    @PostMapping("/reports/update/{id}")
    public String updateRecord(@PathVariable Long id,
                               @RequestParam String entryTime,
                               @RequestParam(required = false) String exitTime,
                               RedirectAttributes redirectAttributes) {
        try {
            PresenceRecord record = presenceService.getPresenceRecordById(id).orElseThrow();

            // Установка времени входа
            if (entryTime != null && !entryTime.isEmpty()) {
                record.setEntryTime(LocalDateTime.parse(entryTime));
            }

            // Установка времени выхода
            if (exitTime != null && !exitTime.isEmpty()) {
                record.setExitTime(LocalDateTime.parse(exitTime));
            } else {
                record.setExitTime(null);
            }

            presenceService.updatePresenceRecord(record);
            redirectAttributes.addFlashAttribute("success", "Запись успешно обновлена");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка обновления записи: " + e.getMessage());
        }

        return "redirect:/admin/reports";
    }

    private LocalDateTime calculateStartDate(String period, LocalDateTime now) {
        if (period == null) {
            period = "day";
        }

        return switch (period) {
            case "week" -> now.minusWeeks(1);
            case "month" -> now.minusMonths(1);
            default -> now.withHour(0).withMinute(0).withSecond(0); // today
        };
    }
}