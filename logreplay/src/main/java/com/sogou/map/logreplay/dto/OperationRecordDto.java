package com.sogou.map.logreplay.dto;

import com.sogou.map.logreplay.bean.OperationRecord;
import com.sogou.map.logreplay.bean.TagInfo;

public class OperationRecordDto {

	private Long id;
	private String ip;
	private String deviceId;
	private String uvid;
	private String os;
	private Long version;
	private Long timestamp;
	private Integer pageNo;
	private String pageName;
	private Integer tagNo;
	private String tagName;
	private Long actionId;
	private Long targetId;
	
	public OperationRecordDto() {}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getUvid() {
		return uvid;
	}

	public void setUvid(String uvid) {
		this.uvid = uvid;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public Integer getPageNo() {
		return pageNo;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}

	public String getPageName() {
		return pageName;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	public Integer getTagNo() {
		return tagNo;
	}

	public void setTagNo(Integer tagNo) {
		this.tagNo = tagNo;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public Long getActionId() {
		return actionId;
	}

	public void setActionId(Long actionId) {
		this.actionId = actionId;
	}

	public Long getTargetId() {
		return targetId;
	}

	public void setTargetId(Long targetId) {
		this.targetId = targetId;
	}

	public static OperationRecordDto from(OperationRecord record, TagInfo tagInfo) {
		if(record == null) {
			throw new IllegalArgumentException("OperationRecord should not be null!");
		}
		if(tagInfo != null) {
			if(!record.getPageNo().equals(tagInfo.getPageNo()) || !record.getTagNo().equals(tagInfo.getTagNo())) {
				throw new IllegalStateException("Arguments are not consistent!");
			}
		}
		OperationRecordDto dto = new OperationRecordDto();
		dto.id = record.getId();
		dto.ip = record.getIp();
		dto.deviceId = record.getDeviceId();
		dto.uvid = record.getUvid();
		dto.os = record.getOs();
		dto.version = record.getVersion();
		dto.timestamp = record.getTimestamp();
		dto.pageNo = record.getPageNo();
		dto.tagNo = record.getTagNo();
		if(tagInfo != null) {
			dto.pageName = tagInfo.getPageInfo().getName();
			dto.tagName = tagInfo.getName();
			dto.actionId = tagInfo.getActionId();
			dto.targetId = tagInfo.getTargetId();
		}
		return dto;
	}
}
