package io.github.yangziwen.logreplay.dao.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("serial")
public class QueryParamMap extends HashMap<String, Object> {
	
	private AtomicInteger seqHolder = new AtomicInteger(101);
	
	/**
	 * 由于拼接sql的机制所限，表达or的paramMap中不能再嵌套表达or的paramMap
	 * 但整套dao的抽象，都是为了应对大多数简单情况
	 * 而且or嵌套or的复杂sql本不应被推荐使用
	 */
	private boolean isOrMap = false;
	
	/** an unmodifiable empty QueryParamMap instance  **/
	public static final QueryParamMap EMPTY_MAP = new QueryParamMap(0) {
		@Override
		public Object put(String key, Object value) {
			throw new UnsupportedOperationException();
		}
		@Override
		public Object remove(Object key) {
			return null;
		}
		@Override
		public void putAll(Map<? extends String, ? extends Object> m) {
			throw new UnsupportedOperationException();
		}
		@Override
		public void clear() {}
	};
	
	public QueryParamMap() {
		super();
	}
	
	public QueryParamMap(int initialCapacity) {
		super(initialCapacity);
	}
	
	public QueryParamMap addParam(boolean isValid, String key, Object value) {
		if (isValid) {
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
	
	private String generateOrMapKey() {
		return seqHolder.getAndIncrement() + DaoConstant.OR_SUFFIX;
	}
	
	public QueryParamMap or(Map<String, Object> orMap) {
		return or(generateOrMapKey(), orMap);
	}
	
	public QueryParamMap or(String orMapKey, Map<String, Object> orMap) {
		ensureOrMap(orMapKey).putAll(orMap);
		return this;
	}
	
	public QueryParamMap or(String key, String value) {
		return or(generateOrMapKey(), key, value);
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
	
	private QueryParamMap markOrMap() {
		this.isOrMap = true;
		return this;
	}
	
	private QueryParamMap ensureOrMap(String orMapKey) {
		if (this.isOrMap) {
			throw new UnsupportedOperationException("Nested orMap is not supported!");
		}
		QueryParamMap orMap = (QueryParamMap) get(orMapKey + DaoConstant.OR_SUFFIX);
		if (orMap == null) {
			orMap = new QueryParamMap();
			put(orMapKey + DaoConstant.OR_SUFFIX, orMap);
		}
		return orMap.markOrMap();
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, String> ensureOrderByMap() {
		Map<String, String> orderByMap = (Map<String, String>) get(DaoConstant.ORDER_BY);
		if (orderByMap == null) {
			orderByMap = new LinkedHashMap<String, String>();
			put(DaoConstant.ORDER_BY, orderByMap);
		}
		return orderByMap;
	}
	
	@SuppressWarnings("unchecked")
	private List<String> ensureGroupByList() {
		List<String> groupByList = (List<String>) get(DaoConstant.GROUP_BY);
		if (groupByList == null) {
			groupByList = new ArrayList<String>(3);
			put(DaoConstant.GROUP_BY, groupByList);
		}
		return groupByList;
	}
	
	public static QueryParamMap emptyMap() {
		return EMPTY_MAP;
	}
	
}
