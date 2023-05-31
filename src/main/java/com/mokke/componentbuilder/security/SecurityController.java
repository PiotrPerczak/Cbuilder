package com.mokke.componentbuilder.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.mokke.componentbuilder.repository.RoleRepository;
import com.mokke.componentbuilder.utils.htmlParser;

@Controller
public class SecurityController {

    @Autowired
    RoleRepository roleRepository;

    @GetMapping("/login-page")
    public String loginPage (Model model) {
        return "login-page";
    }

    @GetMapping("/access-denied")
    public String accessDenied (Model model) {
        return "access-denied";
    }

    @GetMapping("/admin")
    public String adminPage(Model model) {
        return "admin";
    }

}
