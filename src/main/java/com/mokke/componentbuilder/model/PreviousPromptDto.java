package com.mokke.componentbuilder.model;

public class PreviousPromptDto {

    private String userPrompt;

    private String chatRes;

    public PreviousPromptDto(String chatRes, String userPrompt) {
        this.chatRes = chatRes;
        this.userPrompt = userPrompt;
    }

    public PreviousPromptDto() {}

    public String getUserPrompt() {
        return userPrompt;
    }

    public void setUserPrompt(String userPrompt) {
        this.userPrompt = userPrompt;
    }

    public String getChatRes() {
        return chatRes;
    }

    public void setChatRes(String chatRes) {
        this.chatRes = chatRes;
    }



}
