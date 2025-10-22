package com.martinis.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.martinis.service.UserService;

/**
 * Controller for admin user management and approval
 */
@Controller
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping("/admin/users")
    public String listPendingUsers(Model model) {
        List<Map<String, Object>> pendingUsers = userService.getPendingUsers();
        model.addAttribute("pendingUsers", pendingUsers);
        return "admin/users";
    }

    @PostMapping("/admin/users/approve")
    public String approveUser(@RequestParam("username") String username, RedirectAttributes redirectAttributes) {
        try {
            userService.approveUser(username);
            redirectAttributes.addFlashAttribute("successMessage", "User '" + username + "' has been approved.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to approve user: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/admin/users/reject")
    public String rejectUser(@RequestParam("username") String username, RedirectAttributes redirectAttributes) {
        try {
            userService.rejectUser(username);
            redirectAttributes.addFlashAttribute("successMessage", "User '" + username + "' has been rejected and deleted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to reject user: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

}
