package com.sogou.map.logreplay.logprocess.parser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.sogou.map.logreplay.logprocess.log.MobLog;
import com.sogou.map.mengine.common.tool.impl.encode.URLDecoder;

public class MobLogProcessor {

	public MobLog process(String log) {
		return new MobLog.Builder()
			.infoMap(parseParams(log))
			.build();
	}
	
	private Map<String, String> parseParams(String log) {
		if(StringUtils.isBlank(log)) {
			return Collections.emptyMap();
		}
		int colonIndex = -1;
		Map<String, String> params = new HashMap<String, String>();
		for(String pair: log.split(",")) {
			if(StringUtils.isBlank(pair) || (colonIndex = pair.indexOf(":")) == -1 ) {
				continue;
			}
			String key = pair.substring(0, colonIndex);
			String value = pair.substring(colonIndex + 1, pair.length());
			params.put(key, URLDecoder.decode(URLDecoder.decode(value)));
		}
		return params;
	}
	
}
