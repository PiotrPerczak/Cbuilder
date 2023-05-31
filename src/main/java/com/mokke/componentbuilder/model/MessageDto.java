package com.mokke.componentbuilder.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageDto {
    @JsonProperty("role")
    private String role;

    @JsonProperty("content")
    private String content;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
