package com.sogou.map.logreplay.bean;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tag_target")
public class TagTarget extends AbstractBean {

	@Id
	@Column
	private Long id;
	
	@Column
	private String name;
	
	@Column
	private Boolean enabled;
	
	public TagTarget() {}
	
	public TagTarget(String name) {
		this.name = name;
	}
	
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
	public Boolean getEnabled() {
		return enabled;
	}
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	
	
}
