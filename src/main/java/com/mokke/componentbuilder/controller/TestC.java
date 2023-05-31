package com.mokke.componentbuilder.controller;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mokke.componentbuilder.model.ChatRequest;
import com.mokke.componentbuilder.model.ChatResponse;
import com.mokke.componentbuilder.model.ContextHistory;
import com.mokke.componentbuilder.model.MessageDto;
import com.mokke.componentbuilder.model.PreviousPromptDto;
import com.mokke.componentbuilder.security.KeyConfig;
import com.mokke.componentbuilder.service.UserService;

@Component
public class TestC {
    private HttpClient client = HttpClient.newHttpClient();
    private ObjectMapper mapper = new ObjectMapper();
    private ArrayList<HashMap<String,String>> messagesMap = new ArrayList<>();
    private ChatRequest req = new ChatRequest();
    private String openaiApiKey;
    private String openaiUri = "https://api.openai.com/v1/chat/completions";
    private String chatRole = "assistant";
    private String userRole = "user";
    private UserService userService;
    
    public TestC() {
    }

    public TestC(UserService userService, String openaiApiKey) {
        this.userService = userService;
        this.openaiApiKey = openaiApiKey;
    }

    public String sendRequest(MessageDto dto, PreviousPromptDto prevPrompt, String id) throws IOException, InterruptedException {
        System.out.println(openaiApiKey);
        List<ContextHistory> history = userService.getContextHistoryList(id);
        int count = 0;
        for (ContextHistory ch: history) {
            HashMap<String, String> userPrompt = new HashMap<String, String>() {{
                put("role", userRole);
                put("content", ch.getPrompt()+ ", don't include description");
            }};
    
            HashMap<String, String> chatResponse = new HashMap<String, String>() {{
                put("role", chatRole);
                put("content", ch.getComponent());
            }};

            messagesMap.add(userPrompt);
            messagesMap.add(chatResponse);
            count++;
        }

        if (prevPrompt.getUserPrompt() != null) {
            HashMap<String, String> prevUserPrompt = new HashMap<String, String>() {{
                put("role", userRole);
                put("content", prevPrompt.getUserPrompt()+ ", don't include description");
            }};
    
            HashMap<String, String> prevChatResponse = new HashMap<String, String>() {{
                put("role", chatRole);
                put("content", prevPrompt.getChatRes());
            }};

            messagesMap.add(prevUserPrompt);
            messagesMap.add(prevChatResponse);
            count++;
        }
        System.out.println("History has -> " + count + " prompts");
        for (HashMap<String,String> map : messagesMap) {
            System.out.println(map.get("content"));
        }


        HashMap<String, String> messageMap = new HashMap<String, String>() {{
			put("role", dto.getRole());
			put("content", dto.getContent()+ ", don't include description");
		}};
        messagesMap.add(messageMap);
        
        req.setModel("gpt-3.5-turbo");
        req.setMessages(messagesMap);

        String json = mapper.writeValueAsString(req); 

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(openaiUri))
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .header("Authorization", "Bearer " + openaiApiKey)
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

        String response = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
		ChatResponse completionResponse = mapper.readValue(response, ChatResponse.class);

        return completionResponse.getChoices()[0].getMessage().getContent();
    }
    
}
