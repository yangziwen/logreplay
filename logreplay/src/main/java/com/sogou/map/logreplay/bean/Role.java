package com.sogou.map.logreplay.bean;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "role")
public class Role extends AbstractBean {
	
	public static final String ADMIN = "admin";
	public static final String TEST = "test";
	public static final String DEV = "dev";

	@Id
	@Column
	private Long id;
	
	@Column
	private String name;
	
	@Column
	private String comment;
	
	public Role() {}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
}
