package io.github.yangziwen.logreplay.logprocess.log;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

public class MobLog {

	private Map<String, String> infoMap;
	
	private MobLog(Map<String, String> infoMap) {
		this.infoMap = infoMap;
	}
	
	public String getOs() {
		return infoMap.get("os");
	}
	
	public String getUvid() {
		return infoMap.get("u");
	}
	
	public Long getProductId() {
		return NumberUtils.toLong(infoMap.get("pd"));
	}
	
	/**
	 * ios使用kd作为设备编号
	 */
	public String getDeviceId() {
		String deviceId = infoMap.get("d");
		if(StringUtils.isBlank(deviceId)) {
			deviceId = infoMap.get("kd");
		}
		return deviceId;
	}
	
	public String getLoginId() {
		return infoMap.get("loginid");
	}
	
	public String getVersion() {
		return infoMap.get("v");
	}
	
	public String getNet() {
		return infoMap.get("net");
	}
	
	@Override
	public String toString() {
		return new StringBuilder("MobLog [")
			.append("os=").append(getOs())
			.append(", ")
			.append("version=").append(getVersion())
			.append(", ")
			.append("uvid=").append(getUvid())
			.append(", ")
			.append("deviceId=").append(getDeviceId())
			.append(", ")
			.append("net=").append(getNet())
			.append("]")
			.toString();
	}
	
	public static class Builder {
		
		Map<String, String> infoMap = Collections.emptyMap();
		
		public Builder infoMap(Map<String, String> infoMap) {
			this.infoMap = infoMap;
			return this;
		}
		
		public MobLog build() {
			return new MobLog(infoMap);
		}
	}
}
