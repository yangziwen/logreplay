package com.sogou.map.logreplay.bean;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.alibaba.fastjson.JSON;

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
	private Long version;
	
	@Column
	private Long timestamp;
	
	@Column(name = "page_no")
	private Integer pageNo;
	
	@Column(name = "tag_no")
	private Integer tagNo;
	
	@Column(name = "params_json")
	private String params;
	
	@Transient
	private Long tagInfoId;
	
	public OperationRecord() {}
	
	private OperationRecord(
			String ip,
			String deviceId,
			String uvid,
			String os,
			Long version,
			Long timestamp,
			Integer pageNo,
			Integer tagNo,
			String params) {
		
		this.ip = ip;
		this.deviceId = deviceId;
		this.uvid = uvid;
		this.os = os;
		this.version = version;
		this.timestamp = timestamp;
		this.pageNo = pageNo;
		this.tagNo = tagNo;
		this.params = params;
	}

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
	
	@Override
	public String toString() {
		return new StringBuilder("OperationRecord [")
			.append("id=").append(id)
			.append(", ")
			.append("ip=").append(ip)
			.append(", ")
			.append("deviceId=").append(deviceId)
			.append(", ")
			.append("uvid=").append(uvid)
			.append(", ")
			.append("os=").append(os)
			.append(", ")
			.append("version=").append(version)
			.append(", ")
			.append("timestamp=").append(timestamp)
			.append(", ")
			.append("pageNo=").append(pageNo)
			.append(", ")
			.append("tagNo=").append(tagNo)
			.append(", ")
			.append("params=").append(params)
			.append("]")
			.toString();
	}
	
	public static class Builder {
		String ip;
		String deviceId;
		String uvid;
		String os;
		Long version;
		Long timestamp;
		Integer pageNo;
		Integer tagNo;
		Map<String, Object> params;
		
		public Builder ip(String ip) {
			this.ip = ip;
			return this;
		}
		
		public Builder deviceId(String deviceId) {
			this.deviceId = deviceId;
			return this;
		}
		
		public Builder uvid(String uvid) {
			this.uvid = uvid;
			return this;
		}
		
		public Builder os(String os) {
			this.os = os;
			return this;
		}
		
		public Builder version(Long version) {
			this.version = version;
			return this;
		}
		
		public Builder version(String version) {
			this.version = NumberUtils.toLong(version);
			return this;
		}
		
		public Builder timestamp(Long timestamp) {
			this.timestamp = timestamp;
			return this;
		}
		
		public Builder pageNo(Integer pageNo) {
			this.pageNo = pageNo;
			return this;
		}
		
		public Builder tagNo(Integer tagNo) {
			this.tagNo = tagNo;
			return this;
		}
		
		public Builder params(Map<String, Object> params) {
			this.params = params;
			return this;
		}
		
		public OperationRecord build() {
			return new OperationRecord(ip, deviceId, uvid, os, version, timestamp, pageNo, tagNo, 
					MapUtils.isNotEmpty(params)? JSON.toJSONString(params): null);
		}
		
	}
}
