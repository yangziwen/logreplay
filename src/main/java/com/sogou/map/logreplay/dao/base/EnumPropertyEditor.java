package com.sogou.map.logreplay.dao.base;

import com.sogou.map.logreplay.util.EnumUtil;
import com.sogou.map.logreplay.util.EnumUtil.EnumConverter;

public class EnumPropertyEditor<E extends Enum<E>> extends CustomPropertyEditor {
	
	private Class<E> enumType;
	private EnumConverter converter;
	
	public EnumPropertyEditor(Class<E> enumType, EnumConverter converter) {
		this.enumType = enumType;
		this.converter = converter;
	}
	
	public EnumConverter getConverter() {
		return converter;
	}
	
	public Class<E> getEnumType() {
		return enumType;
	}
	
	@Override
	public void setValue(Object value) {
		super.setValue(parse(value));
	}
	
	@Override
	public void setAsText(String value) {
		super.setValue(parse(value));
	}
	
	protected E parse(Object value) {
		return EnumUtil.parse(value, getEnumType());
	}
	
	@Override
	public Object convertValue(Object value) {
		return EnumUtil.convertValue(value, getEnumType(), getConverter());
	}

}
