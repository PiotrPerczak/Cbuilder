package com.mokke.componentbuilder.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ChoicesResponse {
    @JsonProperty("index")
    private int index;

    @JsonProperty("message")
    private MessageDto message;

    @JsonProperty("finish_reason")
    private String finish_reason;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getFinish_reason() {
        return finish_reason;
    }

    public void setFinish_reason(String finish_reason) {
        this.finish_reason = finish_reason;
    }

    public MessageDto getMessage() {
        return message;
    }

    public void setMessage(MessageDto message) {
        this.message = message;
    }
}
