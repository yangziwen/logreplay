package com.sogou.map.logreplay.bean;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "param_info")
public class ParamInfo extends AbstractBean {

	@Id
	@Column
	private Long id;
	
	@Column(name = "tag_param_id")
	private Long tagParamId;
	
	@Column
	private String name;
	
	@Column
	private String value;
	
	@Column
	private String description;
	
	public ParamInfo() {}
	
	public ParamInfo(String name, String value, String description) {
		this.name = name;
		this.value = value;
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTagParamId() {
		return tagParamId;
	}

	public void setTagParamId(Long tagParamId) {
		this.tagParamId = tagParamId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
