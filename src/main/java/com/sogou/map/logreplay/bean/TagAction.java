package com.sogou.map.logreplay.bean;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import com.sogou.map.logreplay.bean.base.AbstractBean;

/**
 * 操作项关联的动作
 */
@Table(name = "tag_action")
public class TagAction extends AbstractBean {

	@Id
	@Column
	private Long id;
	
	@Column
	private String name;
	
	@Column
	private Boolean enabled;
	
	public TagAction() {}
	
	public TagAction(String name) {
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
