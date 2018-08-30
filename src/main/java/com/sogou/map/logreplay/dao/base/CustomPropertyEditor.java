package com.sogou.map.logreplay.dao.base;

import java.beans.PropertyEditorSupport;

public class CustomPropertyEditor extends PropertyEditorSupport {
	
	private ThreadLocal<Object> valueHolder = new ThreadLocal<Object>();
	
	@Override
	public String getAsText() {
		Object value = getValue();
		return value != null? value.toString(): null;
	}
	
	@Override
	public Object getValue() {
		return valueHolder.get();
	}
	
	@Override
	public void setAsText(String text) throws java.lang.IllegalArgumentException {
		 if (getValue() instanceof String) {
            setValue(text);
            return;
        }
        throw new java.lang.IllegalArgumentException(text);
	}
	
	@Override
	public void setValue(Object value) {
		valueHolder.set(value);
        //firePropertyChange();
	}
	
	/**
	 * 如果输入参数无法转换，则返回null
	 */
	public Object convertValue(Object value) {
		return value;
	}

}
