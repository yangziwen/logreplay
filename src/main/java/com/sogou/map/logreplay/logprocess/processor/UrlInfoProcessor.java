package com.sogou.map.logreplay.logprocess.processor;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.sogou.map.logreplay.logprocess.log.UrlInfo;

public class UrlInfoProcessor {

	public UrlInfo process(String url) {
		int questionMarkIndex = url.indexOf("?");
		String uri = url;
		String queryString = "";
		Map<String, String> params = Collections.emptyMap();
		
		if(questionMarkIndex > 0) {
			uri = url.substring(0, questionMarkIndex);
			queryString = url.substring(questionMarkIndex + 1, url.length());
		}
		
		if(StringUtils.isNotEmpty(queryString)) {
			String customInfoPrefix = "info=";
			int customInfoBeginIndex = url.indexOf(customInfoPrefix) + customInfoPrefix.length();
			String otherQueries = customInfoBeginIndex > 0? queryString.substring(0, customInfoBeginIndex): queryString;
			String customInfoStr = customInfoBeginIndex > 0? url.substring(customInfoBeginIndex, url.length()): "";
			if(StringUtils.isNotBlank(customInfoStr)) {
				int appendedParamsBeginIndex = customInfoStr.lastIndexOf("}&") + 1;
				if(appendedParamsBeginIndex > 0) {
					otherQueries += customInfoStr.substring(appendedParamsBeginIndex);
					customInfoStr = customInfoStr.substring(0, appendedParamsBeginIndex);
				}
			}
			params = parseParams(otherQueries);
			params.put("info", customInfoStr);
		}
		
		return new UrlInfo.Builder()
			.uri(uri)
			.queryString(queryString)
			.params(params)
			.build();
	}
	
	private Map<String, String> parseParams(String queryString) {
		if(StringUtils.isEmpty(queryString)) {
			return Collections.emptyMap();
		}
		int equalityIndex = -1;
		Map<String, String> params = new LinkedHashMap<String, String>();
		for(String pair: queryString.split("&")) {
			if(StringUtils.isBlank(pair) || (equalityIndex = pair.indexOf("=")) == -1) {
				continue;
			}
			String key = pair.substring(0, equalityIndex);
			String value = pair.substring(equalityIndex + 1, pair.length());
			params.put(key, value);
		}
		return params;
	}
}
