package com.mokke.componentbuilder.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.mokke.componentbuilder.model.ContextHistory;
import com.mokke.componentbuilder.model.Role;
import com.mokke.componentbuilder.model.Session;
import com.mokke.componentbuilder.model.User;
import com.mokke.componentbuilder.model.UserDto;
import com.mokke.componentbuilder.repository.ContextHistoryRepository;
import com.mokke.componentbuilder.repository.RoleRepository;
import com.mokke.componentbuilder.repository.SessionRepository;
import com.mokke.componentbuilder.repository.UserRepository;
import com.mokke.componentbuilder.utils.FileSwap;

import jakarta.transaction.Transactional;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	UserRepository userRepository;

	@Autowired
	SessionRepository sessionRepository;

	@Autowired
	ContextHistoryRepository contextHistoryRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired private FileSwap fsp = new FileSwap();

	public UserServiceImpl(BCryptPasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public User findByUsername(String username) {
		User user = userRepository.findByUsername(username);
		if (user != null) {
			return user;
		} else {
			return null;
		}
	}

	@Override
	@Transactional
	public void save(UserDto webUser) {
		User user = new User();

		if (roleRepository.findByName("ROLE_USER") == null) {
			System.out.println("CREATE ROLE");
			Role role = new Role();
      		role.setName("ROLE_USER");
      		roleRepository.save(role);
		}

		// assign user details to the user object
		user.setUsername(webUser.getUsername());
		user.setPassword(passwordEncoder.encode(webUser.getPassword()));
		user.setEnabled(true);

		// give user default role of "USER"
		user.setRoles(Arrays.asList(roleRepository.findByName("ROLE_USER")));

		// save user in the database
		userRepository.save(user);
	}

	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(userName);

		if (user == null) {
			throw new UsernameNotFoundException("Invalid username or password.");
		}

		Collection<SimpleGrantedAuthority> authorities = mapRolesToAuthorities(user.getRoles());

		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
				authorities);
	}

	private Collection<SimpleGrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
		Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

		for (Role tempRole : roles) {
			SimpleGrantedAuthority tempAuthority = new SimpleGrantedAuthority(tempRole.getName());
			authorities.add(tempAuthority);
		}

		return authorities;
	}

	public Role findRoleByName(String roleName) {

		Role role = roleRepository.findByName(roleName);

		if (role != null) {
			return role;
		} else {
			return null;
		}
	}

	@Override
	public Session saveFirstSession(String username, String id, String component){
		User currentUser = this.findByUsername(username);
		Session session = new Session();
		session.setId(id);
		session.setComponent(component);
		session.setUser(currentUser);
		session.setSaved(false);
		sessionRepository.save(session);
		System.out.println("[Info]: First session saved");
		return session;
	}

	@Override
	public void saveSession(String username, String id, String component, String prompt, boolean saved){
		User currentUser = this.findByUsername(username);
		
		Optional<Session> currentSession = sessionRepository.findById(id);
		currentSession.ifPresent(session -> this.saveContextHistory(session.getComponent(), session.getPrompt(), session.getId()));

		Session session = new Session();
		session.setId(id);
		session.setComponent(component);
		session.setPrompt(prompt);
		session.setUser(currentUser);
		session.setSaved(saved);
		sessionRepository.save(session);

		System.out.println("[Info]: Session saved");
	}

	@Override
	public String loadSession(String id){
		Optional<Session> session = sessionRepository.findById(id);
		if (session.isPresent()) {
			System.out.println("Session loaded becasue it's PRESETN");
			return session.get().getComponent();
		} else {
			System.out.println("LOAD SESSION ERROR it's not presetn");
			return "<p>Server error</p>";
		}
	}

	@Override
	public boolean deleteSession(String id){
		sessionRepository.deleteById(id);
		return true;
	}

	@Override
	public void deleteUnusedSessions(String id, String username){
		User currentUser = this.findByUsername(username);
		List<Session> sessionList = sessionRepository.findByUser(currentUser);
		for (Session session: sessionList) {
			if (session.getId().equals(id)) {
				System.out.println("[info]: Continuing deletion.. (current session)");
				continue;
			}
			if (session.isSaved() == false) {
				System.out.println("[info]: Unused session deleted.");
				sessionRepository.deleteById(session.getId());
			}
		}
		
	}

	@Override
	public List<Session> getAllSessions(String username){
		User currentUser = this.findByUsername(username);
		List<Session> sessions = sessionRepository.findByUser(currentUser);
		return sessions;
	}

	@Override
	public List<ContextHistory> getContextHistoryList(String sessionId) {
		System.out.println("SHOULD WORK HERE");
		Optional<Session> currentSessionOptional = sessionRepository.findById(sessionId);
		System.out.println("WORKS");
		List<ContextHistory> contextHistoryList;
		System.out.println("WORKS");
		if (currentSessionOptional.isPresent()) {
			Session currentSession = currentSessionOptional.get();
			contextHistoryList = contextHistoryRepository.findBySessionid(currentSession);
			System.out.println("WORKS");
		} else {
			contextHistoryList = new ArrayList<>();
			System.out.println("[Error]: Session doesn't exist.");
		}

		System.out.println("CONTEXT RETRIEVED");
		System.out.println(contextHistoryList);
		return contextHistoryList;
	}

	@Override
	public void saveContextHistory(String component, String prompt, String sessionId){
		Optional<Session> currentSessionOptional = sessionRepository.findById(sessionId);

		currentSessionOptional.ifPresent(session -> {
			ContextHistory contextHistory = new ContextHistory(session.getComponent(), session.getPrompt(), session);
			contextHistoryRepository.save(contextHistory);
			System.out.println("[Info]: ContextHistory saved");
		});		
	}

	@Override
	public String getPreviousPrompt (String id){
		Optional<Session> session = sessionRepository.findById(id);
		return session.get().getPrompt();
	}
}
