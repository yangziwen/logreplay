package io.github.yangziwen.logreplay.bean;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import io.github.yangziwen.logreplay.bean.base.AbstractBean;

/**
 * 操作项与参数的关联
 * 一个操作项(TagInfo)可关联多个参数信息(ParamInfo)
 */
@Table(name = "tag_param")
public class TagParam extends AbstractBean {

	@Id
	@Column
	private Long id;
	
	/** 操作项id **/
	@Column(name = "tag_info_id")
	private Long tagInfoId;
	
	/** 关联的操作信息列表 **/
	@Transient
	private List<ParamInfo> paramInfoList = new ArrayList<ParamInfo>();
	
	/** 相关的注释 **/
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
	
	public void addParamInfo(ParamInfo info) {
		paramInfoList.add(info);
	}
	
}
