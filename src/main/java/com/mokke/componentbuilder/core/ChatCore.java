package com.mokke.componentbuilder.core;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.mokke.componentbuilder.controller.TestC;
import com.mokke.componentbuilder.model.MessageDto;
import com.mokke.componentbuilder.model.PreviousPromptDto;
import com.mokke.componentbuilder.service.UserService;

@Component
public class ChatCore {

    //@Autowired 
    //Environment environment;

    //private String openaiApiKey = environment.getProperty("openai.api_key");

    @Autowired private UserService userService;
  

    public ChatCore() {}

    public synchronized String createRequest(MessageDto message, PreviousPromptDto prevPrompt ,String id, String openaiApiKey) throws Exception, InterruptedException {
		  TestC ts = new TestC(userService, openaiApiKey);
		  String res = ts.sendRequest(message, prevPrompt, id);
      String component = createComponent(res);
		  return component;

	}

  public String createComponent(String data) {

    StringBuilder sb = new StringBuilder();
    //sb.append("<div id=\"copy\" class=\""+"generated-component"+"\">");
    sb.append(data);
    //sb.append("</div");
    String completedHtmlComponent = sb.toString();

    return completedHtmlComponent;
  }

  public String createBaseComponent() {

    StringBuilder sb = new StringBuilder();
    sb.append("<div id=\"copy\" class=\""+"base-component text-zinc-400"+"\">");
    sb.append("<p>Component...</p>");
    sb.append("</div>");
    String completedHtmlComponent = sb.toString();

    return completedHtmlComponent;
  }
}
