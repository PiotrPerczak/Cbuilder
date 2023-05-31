package com.mokke.componentbuilder.controller;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mokke.componentbuilder.model.FormInputDto;
import com.mokke.componentbuilder.model.Session;
import com.mokke.componentbuilder.model.SessionIdDto;
import com.mokke.componentbuilder.repository.SessionRepository;
import com.mokke.componentbuilder.service.UserService;

@RestController
public class ChatRest {

    @Autowired private SessionRepository sessionRepository;
	@Autowired private UserService userService;
    
    @PostMapping(path = "/chat/save-session")
	public void chatSave(@RequestBody SessionIdDto req, @ModelAttribute FormInputDto dto, Authentication authentication) {
		String username = authentication.getName();
        String uniqueID = UUID.randomUUID().toString();
		try {
			//Saving session to database
			Optional<Session> sess = sessionRepository.findById(req.getSessionId());
			if (sess.isPresent()) {
				Session session = sess.get();
				userService.saveSession(username, uniqueID, session.getComponent(), session.getPrompt(), true);
				System.out.println("[info]: Session saved successfully!");
			} else {
				System.out.println("[error]: Session cannot be found.");
			}
			
		} catch (Exception e) {
			System.out.println("[error]: Error while saving session.");
		}
		
	}
}
