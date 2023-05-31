package com.mokke.componentbuilder.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatResponse {
    @JsonProperty("id")
    String id;

    @JsonProperty("choices")
    private ChoicesResponse[] choices;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ChoicesResponse[] getChoices() {
        return choices;
    }

    public void setChoices(ChoicesResponse[] choices) {
        this.choices = choices;
    }
}
