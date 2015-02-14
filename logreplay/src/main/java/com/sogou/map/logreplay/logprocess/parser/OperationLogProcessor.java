package com.sogou.map.logreplay.logprocess.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sogou.map.logreplay.logprocess.log.MobLog;
import com.sogou.map.logreplay.logprocess.log.NginxLog;
import com.sogou.map.logreplay.logprocess.log.OperationLog;
import com.sogou.map.logreplay.logprocess.log.UrlInfo;

public class OperationLogProcessor {
	
	private static final Logger logger = LoggerFactory.getLogger(OperationLogProcessor.class);
	
	private NginxLogProcessor nginxLogProcessor = new NginxLogProcessor();
	
	private UrlInfoProcessor urlInfoProcessor = new UrlInfoProcessor();
	
	private MobLogProcessor mobLogProcessor = new MobLogProcessor();

	public OperationLog process(String log) {
		if(StringUtils.isBlank(log)) {
			return null;
		}
		NginxLog nginxLog = nginxLogProcessor.process(log);
		if(nginxLog == null) {
			return null;
		}
		UrlInfo urlInfo = urlInfoProcessor.process(nginxLog.getUrl());
		if(urlInfo == null) {
			return null;
		}
		MobLog mobLog = mobLogProcessor.process(urlInfo.getParam("moblog"));
		List<Map<String, Object>> operationList = parseOperationList(urlInfo.getParam("info"));
		
		return new OperationLog.Builder()
			.ip(nginxLog.getIp())
			.deviceId(mobLog.getDeviceId())
			.uvid(mobLog.getUvid())
			.os(mobLog.getOs())
			.version(mobLog.getVersion())
			.timestamp(nginxLog.getTimestamp())
			.operationList(operationList)
			.build();
	}
	
	public List<Map<String, Object>> parseOperationList(String allInfoStr) {
		if(StringUtils.isBlank(allInfoStr)) {
			return null;
		}
		JSONObject allInfo = JSON.parseObject(allInfoStr);
		JSONArray eventInfoList = allInfo.getJSONArray("l");
		JSONObject event6000Info = null;
		for(int i = 0, l = eventInfoList.size(); i < l; i++) {
			JSONObject info = eventInfoList.getJSONObject(i);
			if(info == null) {
				continue;
			}
			if(NumberUtils.toInt(info.getString("e")) != 6000) {
				continue;
			}
			event6000Info = info;
			break;
		}
		JSONArray subInfoList = null;
		try {
			if(event6000Info.get("info") instanceof JSONArray) {
				subInfoList = event6000Info.getJSONArray("info");
			} else {
				subInfoList = JSONObject.parseArray(event6000Info.getString("info"));
			}
		} catch (Exception e) {
			logger.warn("Unexpected OperationLog format [{}]", event6000Info.getString("info").toString());
			return null;
		}
		List<Map<String, Object>> operationList = new ArrayList<Map<String,Object>>();
		for(int i = 0, l = subInfoList.size(); i < l; i++) {
			JSONObject subInfo = subInfoList.getJSONObject(i);
			if(subInfo.get("tag") != null && subInfo.get("p") != null) {
				operationList.add(subInfo);
			}
		}
		return operationList;
	}
}
