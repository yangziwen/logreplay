package com.sogou.map.logreplay.bean;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 操作日志的校验记录
 */
@Table(name = "inspection_record")
public class InspectionRecord extends AbstractBean {

	@Id
	@Column
	private Long id;
	
	@Column(name = "product_id")
	private Long productId;
	
	/** 页面信息的id **/
	@Column(name = "page_info_id")
	private Long pageInfoId;
	
	/** 操作项信息的id **/
	@Column(name = "tag_info_id")
	private Long tagInfoId;
	
	/** 校验结果是否正确 **/
	@Column
	private Boolean valid;
	
	/** 是否已解决 **/
	@Column
	private Boolean solved;
	
	/** 校验结果提交人id **/
	@Column(name = "submitter_id")
	private Long submitterId;
	
	/** 校验结果解决人id **/
	@Column(name = "solver_id")
	private Long solverId;
	
	@Column(name = "create_time")
	private Timestamp createTime;
	
	@Column(name = "update_time")
	private Timestamp updateTime;
	
	@Column
	private String comment;
	
	@Transient
	private PageInfo pageInfo;
	
	@Transient
	private TagInfo tagInfo;
	
	@Transient
	private User submitter;
	
	@Transient
	private User solver;
	
	public InspectionRecord() {}
	
	public InspectionRecord(
			Long pageInfoId, 
			Long tagInfoId, 
			Long submitterId, 
			Boolean valid, 
			String comment) {
		this.pageInfoId = pageInfoId;
		this.tagInfoId = tagInfoId;
		this.submitterId = submitterId;
		this.valid = valid;
		this.comment = comment;
		this.solved = false;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Long getPageInfoId() {
		return pageInfoId;
	}

	public void setPageInfoId(Long pageInfoId) {
		this.pageInfoId = pageInfoId;
	}

	public Long getTagInfoId() {
		return tagInfoId;
	}

	public void setTagInfoId(Long tagInfoId) {
		this.tagInfoId = tagInfoId;
	}

	public Boolean getValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}

	public Boolean getSolved() {
		return solved;
	}

	public void setSolved(Boolean solved) {
		this.solved = solved;
	}

	public Long getSubmitterId() {
		return submitterId;
	}

	public void setSubmitterId(Long submitterId) {
		this.submitterId = submitterId;
	}

	public Long getSolverId() {
		return solverId;
	}

	public void setSolverId(Long solverId) {
		this.solverId = solverId;
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

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public PageInfo getPageInfo() {
		return pageInfo;
	}

	public void setPageInfo(PageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}

	public TagInfo getTagInfo() {
		return tagInfo;
	}

	public void setTagInfo(TagInfo tagInfo) {
		this.tagInfo = tagInfo;
	}

	public User getSubmitter() {
		return submitter;
	}

	public void setSubmitter(User submitter) {
		this.submitter = submitter;
	}

	public User getSolver() {
		return solver;
	}

	public void setSolver(User solver) {
		this.solver = solver;
	}
	
}
