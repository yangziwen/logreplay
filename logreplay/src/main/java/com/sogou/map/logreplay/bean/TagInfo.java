package com.sogou.map.logreplay.bean;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tag_info")
public class TagInfo extends AbstractBean {

	@Id
	@Column
	private Long id;
	
	@Column(name = "tag_no")
	private Integer tagNo;

	@Column
	private String name;
	
	@Column(name = "page_info_id")
	private Long pageInfoId;
	
	private PageInfo pageInfo;
	
	@Column(name = "action_id")
	private Long actionId;
	
	private String action;
	
	@Column(name = "target_id")
	private Long targetId;
	
	private String target;
	
	@Column(name = "comment")
	private String comment;
	
	@Column(name = "create_time")
	private Timestamp createTime;
	
	@Column(name = "update_time")
	private Timestamp updateTime;
	
	public TagInfo() {}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getTagNo() {
		return tagNo;
	}

	public void setTagNo(Integer tagNo) {
		this.tagNo = tagNo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Long getPageInfoId() {
		return pageInfoId;
	}

	public void setPageInfoId(Long pageInfoId) {
		this.pageInfoId = pageInfoId;
	}

	public PageInfo getPageInfo() {
		return pageInfo;
	}

	public void setPageInfo(PageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}

	public Long getActionId() {
		return actionId;
	}

	public void setActionId(Long actionId) {
		this.actionId = actionId;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Long getTargetId() {
		return targetId;
	}

	public void setTargetId(Long targetId) {
		this.targetId = targetId;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}
	
}
