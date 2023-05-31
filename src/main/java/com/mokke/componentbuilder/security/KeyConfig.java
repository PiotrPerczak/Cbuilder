package com.mokke.componentbuilder.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeyConfig {

    @Value("${openai.api_key}")
    private String openaiApiKey;

    public String getOpenaiApiKey() {
        return openaiApiKey;
    }
    
}
