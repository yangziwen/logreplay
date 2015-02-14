package com.sogou.map.logreplay.logprocess.log;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

public class OperationLog {
	
	private String ip;
	private String deviceId;
	private String uvid;
	private String os;
	private String version;
	private String timestamp;
	private List<Map<String, Object>> operationList;
	
	private OperationLog(
			String ip, 
			String deviceId, 
			String uvid, 
			String os, 
			String version,
			String timestamp,
			List<Map<String, Object>> operationList) {
		this.ip = ip;
		this.deviceId = deviceId;
		this.uvid = uvid;
		this.os = os;
		this.version = version;
		this.timestamp = timestamp;
		this.operationList = operationList;
	}
	
	public String getIp() {
		return ip;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public String getUvid() {
		return uvid;
	}
	public String getOs() {
		return os;
	}
	public String getVersion() {
		return version;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public List<Map<String, Object>> getOperationList() {
		return operationList;
	}
	
	@Override
	public String toString() {
		return new StringBuilder("OperationInfo [")
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
			.append("operationList=").append(operationList)
			.append("]")
			.toString();
	}
	
	public static class Builder {
		String ip;
		String deviceId;
		String uvid;
		String os;
		String version;
		String timestamp;
		List<Map<String, Object>> operationList = Collections.emptyList();
		
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
		
		public Builder version(String version) {
			this.version = version;
			return this;
		}
		
		public Builder timestamp(String timestamp) {
			this.timestamp = timestamp;
			return this;
		}
		
		public Builder operationList(List<Map<String, Object>> operationList) {
			if(CollectionUtils.isEmpty(operationList)) {
				return this;
			}
			this.operationList = operationList;
			return this;
		}
		
		public OperationLog build() {
			return new OperationLog(ip, deviceId, uvid, os, version, timestamp, operationList);
		}
	}

}
