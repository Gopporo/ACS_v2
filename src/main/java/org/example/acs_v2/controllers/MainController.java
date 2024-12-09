package org.example.acs_v2.controllers;

import lombok.RequiredArgsConstructor;
import org.example.acs_v2.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class MainController {
    @Autowired
    UserService userService;

    @GetMapping("/index")
    public String product(Principal principal, Model model) {
        if (principal == null) {
            System.out.println("Пиздец, он(principal) 0");
        }
        model.addAttribute("userId", userService.getUserId(principal));
        model.addAttribute("role", userService.getUserRole(principal));
        return "index";
    }

}
