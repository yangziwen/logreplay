package com.sogou.map.logreplay.bean;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.alibaba.fastjson.JSON;
import com.sogou.map.logreplay.bean.base.AbstractBean;

/**
 * 操作记录
 * 即日志回放过程中的每一个操作动作
 */
@Table(name = "operation_record")
public class OperationRecord extends AbstractBean {
	
	@Id
	@Column
	private Long id;
	
	@Column(name = "product_id")
	private Long productId;
	
	@Column
	private String ip;
	
	/**
	 * 设备编号
	 * android为deviceId
	 * ios为kd
	 */
	@Column(name = "device_id")
	private String deviceId;

	/** 用户编号 **/
	@Column
	private String uvid;
	
	/** 设备机型 **/
	@Column
	private String os;
	
	/** 当前操作的app的版本 **/
	@Column
	private Long version;
	
	@Column
	private Long timestamp;
	
	/** 页面编号
	 * 如果是公共操作项，则此编号为空
	 */
	@Column(name = "page_no")
	private Integer pageNo;
	
	/** 操作项编号 **/
	@Column(name = "tag_no")
	private Integer tagNo;
	
	/** json格式的参数信息 **/
	@Column(name = "params")
	private String params;
	
	/** 操作项信息的id **/
	@Transient
	private Long tagInfoId;
	
	public OperationRecord() {}
	
	private OperationRecord(
			String ip,
			Long productId,
			String deviceId,
			String uvid,
			String os,
			Long version,
			Long timestamp,
			Integer pageNo,
			Integer tagNo,
			String params) {
		
		this.ip = ip;
		this.productId = productId;
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
		Long productId;
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
		
		public Builder productId(Long productId) {
			this.productId = productId;
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
			return new OperationRecord(ip, productId, deviceId, 
					uvid, os, version, timestamp, pageNo, tagNo,
					MapUtils.isNotEmpty(params)? JSON.toJSONString(params): null);
		}
		
	}
}
