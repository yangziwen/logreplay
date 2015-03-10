package com.sogou.map.logreplay.dto;

import java.sql.Timestamp;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.sogou.map.logreplay.bean.InspectionRecord;
import com.sogou.map.logreplay.dao.base.Page;

public class InspectionRecordDto {

	private Long id;
	private Long pageInfoId;
	private Long tagInfoId;
	private Integer pageNo;
	private Integer tagNo;
	private Boolean valid;
	private Boolean solved;
	private Long submitterId;
	private String submitterUsername;
	private String submitterScreenName;
	private Long solverId;
	private String solverUsername;
	private String solverScreenName;
	private Timestamp createTime;
	private Timestamp updateTime;
	private String comment;
	
	public InspectionRecordDto() {}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Integer getPageNo() {
		return pageNo;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}

	public Integer getTagNo() {
		return tagNo;
	}

	public void setTagNo(Integer tagNo) {
		this.tagNo = tagNo;
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

	public String getSubmitterUsername() {
		return submitterUsername;
	}

	public void setSubmitterUsername(String submitterUsername) {
		this.submitterUsername = submitterUsername;
	}

	public String getSubmitterScreenName() {
		return submitterScreenName;
	}

	public void setSubmitterScreenName(String submitterScreenName) {
		this.submitterScreenName = submitterScreenName;
	}

	public Long getSolverId() {
		return solverId;
	}

	public void setSolverId(Long solverId) {
		this.solverId = solverId;
	}

	public String getSolverUsername() {
		return solverUsername;
	}

	public void setSolverUsername(String solverUsername) {
		this.solverUsername = solverUsername;
	}

	public String getSolverScreenName() {
		return solverScreenName;
	}

	public void setSolverScreenName(String solverScreenName) {
		this.solverScreenName = solverScreenName;
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
	
	public InspectionRecordDto from(InspectionRecord record) {
		this.setId(record.getId());
		this.setPageInfoId(record.getPageInfoId());
		if(record.getPageInfo() != null) {
			this.setPageNo(record.getPageInfo().getPageNo());
		}
		this.setTagInfoId(record.getTagInfoId());
		if(record.getTagInfo() != null) {
			this.setTagNo(record.getTagInfo().getTagNo());
		}
		this.setValid(record.getValid());
		this.setSolved(record.getSolved());
		this.setSubmitterId(record.getSubmitterId());
		if(record.getSubmitter() != null) {
			this.setSubmitterUsername(record.getSubmitter().getUsername());
			this.setSubmitterScreenName(record.getSubmitter().getScreenName());
		}
		this.setSolverId(record.getSolverId());
		if(record.getSolver() != null) {
			this.setSolverUsername(record.getSolver().getUsername());
			this.setSolverScreenName(record.getSolver().getScreenName());
		}
		this.setCreateTime(record.getCreateTime());
		this.setUpdateTime(record.getUpdateTime());
		return this;
	}
	
	public static List<InspectionRecordDto> from(List<InspectionRecord> recordList) {
		return Lists.transform(recordList, new Function<InspectionRecord, InspectionRecordDto>() {
			@Override
			public InspectionRecordDto apply(InspectionRecord record) {
				return new InspectionRecordDto().from(record);
			}
		});
	}
	
	public static Page<InspectionRecordDto> from(Page<InspectionRecord> page) {
		return new Page<InspectionRecordDto>(
			page.getStart(), 
			page.getLimit(), 
			page.getCount(), 
			from(page.getList()))
		;
	}
	
}
