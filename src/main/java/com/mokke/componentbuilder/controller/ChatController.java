package com.mokke.componentbuilder.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.hibernate.mapping.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.WebContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mokke.componentbuilder.core.ChatCore;
import com.mokke.componentbuilder.core.CompletionRequest;
import com.mokke.componentbuilder.core.CompletionResponse;
import com.mokke.componentbuilder.core.OpenAiApiClient;
import com.mokke.componentbuilder.core.OpenAiApiClient.OpenAiService;
import com.mokke.componentbuilder.model.ContextHistory;
import com.mokke.componentbuilder.model.FormInputDto;
import com.mokke.componentbuilder.model.MessageDto;
import com.mokke.componentbuilder.model.PreviousPromptDto;
import com.mokke.componentbuilder.model.Session;
import com.mokke.componentbuilder.model.SessionIdDto;
import com.mokke.componentbuilder.model.User;
import com.mokke.componentbuilder.repository.ContextHistoryRepository;
import com.mokke.componentbuilder.repository.SessionRepository;
import com.mokke.componentbuilder.repository.UserRepository;
import com.mokke.componentbuilder.service.UserService;
import com.mokke.componentbuilder.utils.FileSwap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class ChatController {
	
	private String openaiApiKey = "";
	
	@Autowired private ContextHistoryRepository contextHistoryRepository;
	@Autowired private SessionRepository sessionRepository;
	@Autowired private UserService userService;
	@Autowired private ChatCore chatCore;
	
	@GetMapping(path = "/chat")
	public String index(Model model, @RequestParam(value="id",required=true) String id, Authentication authentication, RedirectAttributes redirAttrs) throws IOException, InterruptedException {

		String username = authentication.getName();
		List<Session> sessions = userService.getAllSessions(username);
		String sessionId = id;
		System.out.println("PARAMETER ID");
		System.out.println(sessionId);

		userService.deleteUnusedSessions(sessionId, username);
		//CLEAR ALL SESSIONS WITHOUT SAVED MARK EXCEPT WHEN ID == PARAMETER_ID

		//Create new session and display previously saved sessions..
		Optional<Session> currentSession = sessionRepository.findById(sessionId);
		if (currentSession.isPresent()) {
			System.out.println("[info]: Loading session...");
			Session currSession = currentSession.get();
			String component = chatCore.createComponent(currSession.getComponent());
		 	model.addAttribute("sessionFragmentTest", component);
		} else {
			System.out.println("[info]: Creating new session.");
			String baseComponent = chatCore.createBaseComponent();
			Session firstSession = userService.saveFirstSession(username, id, baseComponent);
			model.addAttribute("sessionFragmentTest", baseComponent);
		}
		// if (sessions.isEmpty()) {
		// } else {
		// 	System.out.println("[info]: loading session prop");
		// 	Session lastSession = sessions.get(sessions.size() - 1);
		// 	sessionId = lastSession.getId();
		// 	String component = chatCore.createComponent(lastSession.getComponent());
		// 	model.addAttribute("sessionFragmentTest", component);
		// }


		//model.addAttribute("sessionFragment", id);
		model.addAttribute("sessionId", sessionId);
		model.addAttribute("sessionsList", sessions);

		return "chat";
	}
	
	@PostMapping(path = "/chat")
	public String chat(Model model, @ModelAttribute FormInputDto dto, @RequestParam(value="id",required=true) String id, Authentication authentication, RedirectAttributes redirAttrs) {
		long startTime = System.currentTimeMillis();
		String username = authentication.getName();
		try {
			model.addAttribute("request", dto.getPrompt());
			MessageDto mdto = new MessageDto();
			mdto.setRole("user");
			mdto.setContent(dto.getPrompt());
			//if nosuchelement exception (handle this error later..) try catch here
			List<Session> sessions = userService.getAllSessions(username);
			Session lastSession = sessions.get(sessions.size() - 1);
			PreviousPromptDto prevPromptObject = new PreviousPromptDto();
			prevPromptObject.setChatRes(lastSession.getComponent());
			prevPromptObject.setUserPrompt(lastSession.getPrompt());
			String chatResponse = chatCore.createRequest(mdto, prevPromptObject, id, openaiApiKey);

			Optional<Session> sess = sessionRepository.findById(id);
			if (sess.isPresent()) {
				List<ContextHistory> contextHistories = userService.getContextHistoryList(id);
				System.out.println("[info]: List of previous contexts..");
				System.out.println(contextHistories);
				System.out.println("-----------------------------------------");
			}
			

			redirAttrs.addFlashAttribute("sessionFragmentTest", chatResponse);

			long endTime = System.currentTimeMillis();
			long millis = endTime - startTime;
			System.out.println(millis);
			//Saving session
			userService.saveSession(username, id, chatResponse, dto.getPrompt(), false);

			return "redirect:/chat?id="+id;
		} catch (Exception e) {
			model.addAttribute("response", "Error in communication with OpenAI ChatGPT API.");
			return "chat";
		}
		
	}

	@PostMapping(path = "/chat/new-session")
	public String newSession(@RequestBody SessionIdDto req, Model model,@ModelAttribute FormInputDto dto, Authentication authentication, RedirectAttributes redirAttrs) {
		String username = authentication.getName();
		//String uniqueID = UUID.randomUUID().toString();
		//redirAttrs.addFlashAttribute("sessionId",uniqueID);
        model.addAttribute("sessionId",req.getSessionId());
		try {
			Optional<Session> currentSession = sessionRepository.findById(req.getSessionId());
			if (currentSession.isPresent()) {
				System.out.println("[info]: Deleting current session.");
				Session currSession = currentSession.get();
				userService.deleteSession(currSession.getId());
			}
			System.out.println("[info]: Creating new session.");
			String baseComponent = chatCore.createBaseComponent();
			Session firstSession = userService.saveFirstSession(username, req.getSessionId(), baseComponent);
			model.addAttribute("sessionFragmentTest", baseComponent);
			return "redirect:/chat?id="+req.getSessionId();
		} catch (Exception e) {
			return "chat";
		}
	}

	@PostMapping(path = "/chat/change-session")
	public String changeSession(@RequestBody SessionIdDto req, Model model,@ModelAttribute FormInputDto dto, Authentication authentication, RedirectAttributes redirAttrs) {
		String username = authentication.getName();
		try {
			Optional<Session> clickedSession = sessionRepository.findById(req.getSessionId());
			if (clickedSession.isPresent()) {
				System.out.println("[info]: Changing session...");
				return "redirect:/chat?id="+req.getSessionId();
			} else {
				return "chat";
			}
		} catch (Exception e) {
			return "chat";
		}
	}

	@PostMapping(path = "/chat/clean")
    public String getds (@RequestBody SessionIdDto req, Model model) throws IOException {
		//fsp.deleteSessionComponent(req.getSessionId());
		System.out.println("GOT REQ	");
		return "main-page";
    }

	@PostMapping(path = "/chat/delete-session")
    public String deleteSession (@RequestBody SessionIdDto req, Model model, Authentication authentication) throws IOException {
		userService.deleteSession(req.getSessionId());
		String username = authentication.getName();
		List<Session> sessions = userService.getAllSessions(username);
		if (sessions.isEmpty()) {
			String uniqueID = UUID.randomUUID().toString();
			return "redirect:/chat?id="+uniqueID;
		} else {
			Session lastSession = sessions.get(sessions.size() - 1);
			System.out.println("deleting session");
			return "redirect:/chat?id="+lastSession.getId();
		}
		
    }
	
}