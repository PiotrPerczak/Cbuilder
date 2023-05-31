package com.mokke.componentbuilder.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mokke.componentbuilder.model.User;
import com.mokke.componentbuilder.model.UserDto;
import com.mokke.componentbuilder.service.UserService;

@Controller
@RequestMapping("/register")
public class RegistrationController {

	@Autowired
    private UserService userService;

	@Autowired
    ModelMapper modelMapper;

	public RegistrationController(UserService userService) {
		this.userService = userService;
	}

	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {
		
		StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
		
		dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
	}	
	
	@GetMapping("/registration-form")
	public String showMyLoginPage(Model model) {
		
		model.addAttribute("user", new UserDto());
		
		return "register/registration-form";
	}

	@PostMapping("/process-registration-form")
	public String processRegistrationForm(
			@Valid @ModelAttribute("user") UserDto user,
			HttpSession session, BindingResult theBindingResult,
			Model model) {
		
		String username = user.getUsername();
		
		// form validation
		 if (theBindingResult.hasErrors()){
			 return "register/registration-form";
	        }

		// check the database if user already exists
        User existing = userService.findByUsername(username);
        if (existing != null){
        	model.addAttribute("user", new UserDto());
			model.addAttribute("registrationError", "User name already exists.");

        	return "register/registration-form";
        }
        
        // create user account and store in the databse
        userService.save(user);

		// place user in the web http session for later use
		session.setAttribute("user", user);

        return "register/registration-confirmation";
	}
}
