package com.mokke.componentbuilder.core;

import java.util.HashMap;
import java.util.List;

public record CompletionRequest(String model,
		List<HashMap<String,String>> messages) {
	
	public static CompletionRequest defaultWith(List<HashMap<String,String>> messages) {
		//CompletionRequest("text-davinci-003", prompt, 0.7, 2000);
		return new CompletionRequest("gpt-3.5-turbo", messages);
	}
	
}