package com.sogou.map.logreplay.logprocess.processor;

import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sogou.map.logreplay.logprocess.log.MobLog;

public class MobLogProcessor {
	
	private static final Logger logger = LoggerFactory.getLogger(MobLogProcessor.class);

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
			try {
				params.put(key, URLDecoder.decode(decodeUnicode(URLDecoder.decode(value, "GBK")), "GBK"));
			} catch (Exception e) {
				logger.warn("Failed to decode content of [{}]", value);
			}
		}
		return params;
	}
	
	/**
	 * unicode解码，仅适用于sogou内部
	 * 前端使用escape而不是encodeURIComponent，很令人惆怅
	 */
	private static String decodeUnicode(String text) {
		int start = text.indexOf("%u");
		if(start == -1) {
			return text;
		}
		int end = 0;
		final StringBuilder buff = new StringBuilder();
		while (start > -1) {
			buff.append(text.substring(end, start));
			end = start + 6;	// unicode两字节代表一个字，其实还是有点问题，先将就吧
			String charStr = text.substring(start + 2, end);
			char letter = (char) Integer.parseInt(charStr, 16);
			buff.append(new Character(letter).toString());
			start = text.indexOf("%u", start + 1);
		}
		if(end < text.length()) {
			buff.append(text.substring(end, text.length()));
		}
		return buff.toString();
	}
	
}
