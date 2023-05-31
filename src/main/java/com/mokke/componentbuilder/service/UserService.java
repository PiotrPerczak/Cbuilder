package com.mokke.componentbuilder.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.mokke.componentbuilder.model.ContextHistory;
import com.mokke.componentbuilder.model.Session;
import com.mokke.componentbuilder.model.User;
import com.mokke.componentbuilder.model.UserDto;

public interface UserService extends UserDetailsService {

	public User findByUsername(String userName);

	void save(UserDto webUser);

	Session saveFirstSession(String username, String id, String component);
	void saveSession(String username, String id, String component, String prompt, boolean saved);
	String loadSession(String id);
	boolean deleteSession(String id);
	List<Session> getAllSessions(String username);
	void deleteUnusedSessions(String id, String username);

	void saveContextHistory(String component, String prompt, String sessionId);
	List<ContextHistory> getContextHistoryList(String sessionID);

	String getPreviousPrompt (String username);
}
