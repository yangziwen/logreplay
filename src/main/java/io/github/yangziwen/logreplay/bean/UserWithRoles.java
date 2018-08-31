package io.github.yangziwen.logreplay.bean;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Transient;

public class UserWithRoles extends User {

	private static final long serialVersionUID = -5247123553983704528L;

	@Transient
	private List<Role> roles = new ArrayList<Role>();

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public void addRole(Role role) {
		roles.add(role);
	}

}
