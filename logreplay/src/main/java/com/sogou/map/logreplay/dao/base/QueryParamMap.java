package com.sogou.map.logreplay.dao.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class QueryParamMap extends HashMap<String, Object> {
	
	public static final QueryParamMap EMPTY_MAP = new QueryParamMap(0);
	
	public QueryParamMap() {
		super();
	}
	
	public QueryParamMap(int initialCapacity) {
		super(initialCapacity);
	}
	
	public QueryParamMap addParam(boolean isValid, String key, Object value) {
		if(isValid) {
			put(key, value);
		}
		return this;
	}
	
	public QueryParamMap addParam(String key, Object value) {
		return addParam(true, key, value);
	}
	
	public QueryParamMap addParam(boolean isValid, String key) {
		return addParam(isValid, key, null);
	}
	
	public QueryParamMap addParam(String key) {
		return addParam(true, key);
	}
	
	public QueryParamMap or(String orMapKey, Map<String, Object> orMap) {
		ensureOrMap(orMapKey).putAll(orMap);
		return this;
	}
	
	public QueryParamMap or(String orMapKey, String key, String value) {
		ensureOrMap(orMapKey).addParam(key, value);
		return this;
	}
	
	public QueryParamMap getOrMap(String orMapKey) {
		return ensureOrMap(orMapKey);
	}
	
	public QueryParamMap orderBy(String key, String sort) {
		ensureOrderByMap().put(key, sort);
		return this;
	}
	
	public QueryParamMap orderByAsc(String key) {
		return orderBy(key, DaoConstant.ORDER_ASC);
	}
	
	public QueryParamMap orderByDesc(String key) {
		return orderBy(key, DaoConstant.ORDER_DESC);
	}
	
	public QueryParamMap groupBy(String value) {
		ensureGroupByList().add(value);
		return this;
	}
	
	private QueryParamMap ensureOrMap(String orMapKey) {
		QueryParamMap orMap = (QueryParamMap) get(orMapKey + DaoConstant.OR_SUFFIX);
		if(orMap == null) {
			orMap = new QueryParamMap();
			put(orMapKey + DaoConstant.OR_SUFFIX, orMap);
		}
		return orMap;
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, String> ensureOrderByMap() {
		Map<String, String> orderByMap = (Map<String, String>) get(DaoConstant.ORDER_BY);
		if(orderByMap == null) {
			orderByMap = new LinkedHashMap<String, String>();
			put(DaoConstant.ORDER_BY, orderByMap);
		}
		return orderByMap;
	}
	
	@SuppressWarnings("unchecked")
	private List<String> ensureGroupByList() {
		List<String> groupByList = (List<String>) get(DaoConstant.GROUP_BY);
		if(groupByList == null) {
			groupByList = new ArrayList<String>(3);
			put(DaoConstant.GROUP_BY, groupByList);
		}
		return groupByList;
	}
	
}
