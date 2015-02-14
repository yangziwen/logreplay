package com.sogou.map.logreplay.bean;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "operation_record")
public class OperationRecord extends AbstractBean {
	
	@Id
	@Column
	private Long id;
	
	@Column
	private String ip;
	
	@Column(name = "device_id")
	private String deviceId;

	@Column
	private String uvid;
	
	@Column
	private String os;
	
	@Column
	private String version;
	
	@Column
	private Timestamp timestamp;
	
	@Column(name = "page_no")
	private Integer pageNo;
	
	@Column(name = "tag_no")
	private Integer tagNo;
	
	@Column(name = "params_json")
	private String params;
	
	private Long tagInfoId;
	
	public OperationRecord() {}

	@Override
	public Long getId() {
		return id;
	}

	@Override
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

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
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

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public Long getTagInfoId() {
		return tagInfoId;
	}

	public void setTagInfoId(Long tagInfoId) {
		this.tagInfoId = tagInfoId;
	}
	
}
