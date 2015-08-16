package com.sogou.map.logreplay.dao.base;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class Page<E> {
	
	public static final String DEFAULT_START = "0";
	public static final String DEFAULT_LIMIT = "30";

	private int start;
	private int limit;
	private int count;
	private List<E> list;
	
	public Page() {}
	
	public Page(int start, int limit, int count, List<E> list) {
		this.start = start;
		this.limit = limit;
		this.count = count;
		this.list = list != null? list: Collections.<E>emptyList();
	}
	
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public List<E> getList() {
		return list;
	}
	public void setList(List<E> list) {
		this.list = list;
	}
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
	
	public static <T> Page<T> emptyPage() {
		return new Page<T>(0, 0, 0, Collections.<T>emptyList());
	}
	
}
