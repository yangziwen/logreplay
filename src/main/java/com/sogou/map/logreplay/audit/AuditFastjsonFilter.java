package com.sogou.map.logreplay.audit;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.audit4j.core.annotation.DeIdentify;
import org.audit4j.core.annotation.IgnoreAudit;

import com.alibaba.fastjson.serializer.PropertyFilter;
import com.alibaba.fastjson.serializer.ValueFilter;

public class AuditFastjsonFilter implements PropertyFilter, ValueFilter {
	
	private static final ConcurrentMap<String, Map<String, DeIdentify>> DEIDENTIFY_INDEX = new ConcurrentHashMap<String,  Map<String, DeIdentify>>();
	
	private static final ConcurrentMap<String, Map<String, Boolean>> IGNORE_AUDIT_INDEX = new ConcurrentHashMap<String, Map<String, Boolean>>();
	
	private static final AuditFastjsonFilter INSTANCE = new AuditFastjsonFilter();
	
	private AuditFastjsonFilter() {}

	@Override
	public Object process(Object object, String name, Object value) {
		Class<?> clazz = object.getClass();
		String indexKey = clazz.getName();
		Map<String, DeIdentify> deIdentifyMap = DEIDENTIFY_INDEX.get(indexKey);
		if (deIdentifyMap == null) {
			DEIDENTIFY_INDEX.putIfAbsent(indexKey, createDeIdentifyMapping(clazz));
			deIdentifyMap = DEIDENTIFY_INDEX.get(indexKey);
		}
		return ObjectToJsonSerializer.deidentifyValue(value, deIdentifyMap.get(name));
	}

	@Override
	public boolean apply(Object object, String name, Object value) {
		Class<?> clazz = object.getClass();
		String indexKey = clazz.getName();
		Map<String, Boolean> ignoreAuditMap = IGNORE_AUDIT_INDEX.get(indexKey);
		if (ignoreAuditMap == null) {
			IGNORE_AUDIT_INDEX.putIfAbsent(indexKey, createIgnoreAuditMapping(clazz));
			ignoreAuditMap = IGNORE_AUDIT_INDEX.get(indexKey);
		}
		return !Boolean.TRUE.equals(ignoreAuditMap.get(name));
	}
	
	private static Map<String, DeIdentify> createDeIdentifyMapping(Class<?> clazz) {
		Map<String, DeIdentify> mapping = new HashMap<String, DeIdentify>();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field: fields) {
			if (Modifier.isStatic(field.getModifiers())) {
				continue;
			}
			if (field.isAnnotationPresent(DeIdentify.class)) {
				mapping.put(field.getName(), field.getAnnotation(DeIdentify.class));
			}
		}
		return mapping;
	}
	
	public static Map<String, Boolean> createIgnoreAuditMapping(Class<?> clazz) {
		Map<String, Boolean> mapping = new HashMap<String, Boolean>();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field: fields) {
			if (Modifier.isStatic(field.getModifiers())) {
				continue;
			}
			if (!field.isAnnotationPresent(IgnoreAudit.class)) {
				continue;
			}
			mapping.put(field.getName(), Boolean.TRUE);
		}
		return mapping;
	}
	
	public static AuditFastjsonFilter instance() {
		return INSTANCE;
	}

}
