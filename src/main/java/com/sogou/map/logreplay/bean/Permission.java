package com.sogou.map.logreplay.bean;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import com.sogou.map.logreplay.bean.base.AbstractBean;

@Table(name = "permission")
public class Permission extends AbstractBean {
	
	@Id
	@Column
	private Long id;
	
	@Column
	private String target;
	
	@Column
	private String action;
	
	public Permission() {}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
	
}
