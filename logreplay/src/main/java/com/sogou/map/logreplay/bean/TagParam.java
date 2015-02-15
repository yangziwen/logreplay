package com.sogou.map.logreplay.bean;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Table(name = "tag_param")
public class TagParam extends AbstractBean {

	@Id
	@Column
	private Long id;
	
	@Column(name = "tag_info_id")
	private Long tagInfoId;
	
	@Transient
	private List<ParamInfo> paramInfoList;
	
	@Column
	private String comment;
	
	public TagParam() {}
	
	public TagParam(Long tagInfoId, String comment) {
		this.tagInfoId = tagInfoId;
		this.comment = comment;
	}

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
	
	public List<ParamInfo> getParamInfoList() {
		return paramInfoList;
	}

	public void setParamInfoList(List<ParamInfo> paramInfoList) {
		this.paramInfoList = paramInfoList;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
}
