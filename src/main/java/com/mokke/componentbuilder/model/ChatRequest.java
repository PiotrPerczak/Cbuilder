package com.mokke.componentbuilder.model;

import java.util.HashMap;
import java.util.List;

public class ChatRequest {
    private String model;  
    private List<HashMap<String,String>> messages;
    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }
    public List<HashMap<String, String>> getMessages() {
        return messages;
    }
    public void setMessages(List<HashMap<String, String>> messages) {
        this.messages = messages;
    }
}
