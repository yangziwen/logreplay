package com.sogou.map.logreplay.util;

import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

public class JsonUtil {
	
	private static JsonConfig config = new JsonConfig();

	static {
		config.registerJsonValueProcessor(java.sql.Date.class, new JsonValueProcessor() {
			@Override
			public Object processObjectValue(String key, Object value, JsonConfig config) {
				if(value == null){
					return 0;
				}
				if(value instanceof java.sql.Date){
					return ((java.sql.Date) value).getTime();
				}
				return 0;
			}
			@Override
			public Object processArrayValue(Object value, JsonConfig config) {
				return null;
			}
		});
		
		config.registerJsonValueProcessor(java.util.Date.class, new JsonValueProcessor() {
			@Override
			public Object processObjectValue(String key, Object value, JsonConfig config) {
				if(value == null){
					return 0;
				}
				if(value instanceof java.util.Date){
					return ((java.util.Date) value).getTime();
				}
				return 0;
			}
			@Override
			public Object processArrayValue(Object value, JsonConfig config) {
				return null;
			}
		});
		
		config.registerJsonValueProcessor(java.sql.Timestamp.class, new JsonValueProcessor() {
			@Override
			public Object processObjectValue(String key, Object value, JsonConfig arg2) {
				if(value == null){
					return 0;
				}
				if(value instanceof java.sql.Timestamp){
					return ((java.sql.Timestamp) value).getTime();
				}
				return 0;
			}
			public Object processArrayValue(Object arg0, JsonConfig arg1) {
				return null;
			}
		});
	}
	
	public static JsonConfig configInstance() {
		return config;
	}
}
