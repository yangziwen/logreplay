package com.sogou.map.logreplay.dto;

import java.util.ArrayList;
import java.util.List;

import com.sogou.map.logreplay.bean.OperationRecord;
import com.sogou.map.logreplay.bean.TagInfo;

public class OperationRecordDto {

	private Long id;
	private Long productId;
	private String ip;
	private String deviceId;
	private String uvid;
	private String os;
	private Long version;
	private Long timestamp;
	private Integer pageNo;
	private Long tagInfoId;
	private String pageName;
	private Integer tagNo;
	private String tagName;
	private Long actionId;
	private Long targetId;
	private String params;
	private Integer inspectStatus;
	private Integer devInspectStatus;

	private List<TagParamParsedResult> paramParsedResultList = new ArrayList<TagParamParsedResult>();

	public OperationRecordDto() {}

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

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public Integer getInspectStatus() {
		return inspectStatus;
	}

	public void setInspectStatus(Integer inspectStatus) {
		this.inspectStatus = inspectStatus;
	}

	public Integer getDevInspectStatus() {
		return devInspectStatus;
	}

	public void setDevInspectStatus(Integer devInspectStatus) {
		this.devInspectStatus = devInspectStatus;
	}

	public List<TagParamParsedResult> getParamParsedResultList() {
		return paramParsedResultList;
	}

	public void setParamParsedResultList(List<TagParamParsedResult> paramsParsedResultList) {
		this.paramParsedResultList = paramsParsedResultList;
	}

	public void addParamParsedResult(TagParamParsedResult result) {
		if(result != null) {
			paramParsedResultList.add(result);
		}
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
		dto.productId = record.getProductId();
		dto.ip = record.getIp();
		dto.deviceId = record.getDeviceId();
		dto.uvid = record.getUvid();
		dto.os = record.getOs();
		dto.version = record.getVersion();
		dto.timestamp = record.getTimestamp();
		dto.pageNo = record.getPageNo();
		dto.tagNo = record.getTagNo();
		dto.params = record.getParams();
		if(tagInfo != null) {
			dto.tagInfoId = tagInfo.getId();
			dto.pageName = tagInfo.getPageInfo().getName();
			dto.tagName = tagInfo.getName();
			dto.actionId = tagInfo.getActionId();
			dto.targetId = tagInfo.getTargetId();
			dto.inspectStatus = tagInfo.getInspectStatus();
			dto.devInspectStatus = tagInfo.getDevInspectStatus();
		}
		return dto;
	}

	public static class TagParamParsedResult {

		private boolean valid;

		private boolean required;

		private String paramName;

		private String paramValue;

		private String description;

		public boolean isValid() {
			return valid;
		}

		public boolean isRequired() {
			return required;
		}

		public String getParamName() {
			return paramName;
		}

		public String getParamValue() {
			return paramValue;
		}

		public String getDescription() {
			return description;
		}

		public TagParamParsedResult valid(boolean valid) {
			this.valid = valid;
			return this;
		}

		public TagParamParsedResult required(boolean required) {
			this.required = required;
			return this;
		}

		public TagParamParsedResult paramName(String paramName) {
			this.paramName = paramName;
			return this;
		}

		public TagParamParsedResult paramValue(String paramValue) {
			this.paramValue = paramValue;
			return this;
		}

		public TagParamParsedResult description(String description) {
			this.description = description;
			return this;
		}

	}
}
