package com.sogou.map.logreplay.dao.base;

import java.util.Map;

public class DaoConstant {
	
	public static final String OR_SUFFIX = "__or";
	public static final String ORDER_ASC = "asc";
	public static final String ORDER_DESC = "desc";
	public static final String ORDER_BY = "__order_by";
	public static final String GROUP_BY = "__group_by";
	public static final String START = "__query_start";
	public static final String LIMIT = "__query_limit";

	private DaoConstant() {}
	
	public static int offset(Map<String, Object> params) {
		Integer offset = (Integer) params.get(START);
		return offset != null && offset > 0? offset: 0;
	}
	
	public static int limit(Map<String, Object> params) {
		Integer limit = (Integer) params.get(LIMIT);
		return limit != null && limit > 0? limit: 0;
	}
	
	public static void offset(int offset, Map<String, Object> params) {
		params.put(START, offset);
	}
	
	public static void limit(int limit, Map<String, Object> params) {
		params.put(LIMIT, limit);
	}
	
}
