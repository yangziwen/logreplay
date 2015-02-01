package com.sogou.map.logreplay.bean;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tag_param")
public class TagParam extends AbstractBean {

	@Id
	@Column
	private Long id;
	
	@Column(name = "tag_info_id")
	private Long tagInfoId;
	
	@Column
	private String comment;
	
	public TagParam() {}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTagInfoId() {
		return tagInfoId;
	}

	public void setTagInfoId(Long tagInfoId) {
		this.tagInfoId = tagInfoId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
}
