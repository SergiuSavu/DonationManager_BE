package de.msg.javatraining.donationmanager.controller.auth;

import de.msg.javatraining.donationmanager.persistence.model.Role;

import java.util.List;

public class SignInResponse {
	private String token;
	private String refreshToken;
	private String type = "Bearer";
	private Long id;
	private String username;
	private String email;
	private List<String> roles;
	private List<String> permission;
	private boolean firstLogin;

	public SignInResponse(String accessToken, String refreshToken, Long id, String username, String email, boolean firstLogin, List<String> roles) {
		this.token = accessToken;
		this.refreshToken = refreshToken;
		this.id = id;
		this.username = username;
		this.email = email;
		this.roles = roles;
		this.firstLogin = firstLogin;
	}

	public String getAccessToken() {
		return token;
	}

	public void setAccessToken(String accessToken) {
		this.token = accessToken;
	}

	public String getTokenType() {
		return type;
	}

	public void setTokenType(String tokenType) {
		this.type = tokenType;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<String> getRoles() {
		return roles;
	}

	public boolean isFirstLogin() {
		return firstLogin;
	}

	public void setFirstLogin(boolean firstLogin) {
		this.firstLogin = firstLogin;
	}
}
