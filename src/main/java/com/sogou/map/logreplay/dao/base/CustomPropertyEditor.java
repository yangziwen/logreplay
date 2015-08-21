package com.sogou.map.logreplay.dao.base;

import java.beans.PropertyEditorSupport;

public class CustomPropertyEditor extends PropertyEditorSupport {
	
	/**
	 * 如果输入参数无法转换，则返回null
	 */
	public Object convertValue(Object value) {
		return value;
	}

}
