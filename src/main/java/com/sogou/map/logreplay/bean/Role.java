package com.sogou.map.logreplay.bean;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import com.sogou.map.logreplay.bean.base.AbstractBean;

@Table(name = "role")
public class Role extends AbstractBean implements Cloneable {
	
	public static final String ADMIN = "admin";
	public static final String TEST = "test";
	public static final String DEV = "dev";
	public static final String VISITOR = "visitor";

	@Id
	@Column
	private Long id;
	
	@Column
	private String name;
	
	@Column(name = "display_name")
	private String displayName;
	
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

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	@Override
	public Role clone() {
		try {
			return (Role) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
