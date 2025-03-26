package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final RoleRepository roleRepository;

    @Autowired
    public AdminController(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @GetMapping("/users")
    public String getAllUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", roleRepository.findAll());
        return "admin";
    }

    @PostMapping("/users/add")
    public String addUser(@ModelAttribute("user") User user,
                          @RequestParam List<Long> roleIds,
                          @RequestParam(required = false) String password,
                          Model model) {
        try {
            Set<Role> roles = roleIds.stream()
                    .map(roleId -> roleRepository.findById(roleId).orElseThrow())
                    .collect(Collectors.toSet());
            user.setRoles(roles);
            userService.addUser(user, password);
            return "redirect:/admin/users";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("users", userService.getAllUsers());
            model.addAttribute("allRoles", roleRepository.findAll());
            return "admin";
        }
    }

    @PostMapping("/users/update")
    public String updateUser(@RequestParam Long id,
                             @RequestParam String name,
                             @RequestParam String email,
                             @RequestParam List<Long> roleIds,
                             @RequestParam(required = false) String newPassword) {
        User user = userService.getUserById(id);
        user.setName(name);
        user.setEmail(email);

        Set<Role> roles = roleIds.stream()
                .map(roleId -> roleRepository.findById(roleId).orElseThrow())
                .collect(Collectors.toSet());
        user.setRoles(roles);

        userService.updateUser(user, newPassword);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/delete")
    public String deleteUser(@RequestParam Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }
}