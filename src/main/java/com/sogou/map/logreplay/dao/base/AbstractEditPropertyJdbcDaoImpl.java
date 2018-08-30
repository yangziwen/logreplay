package com.sogou.map.logreplay.dao.base;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.sogou.map.logreplay.bean.base.AbstractBean;

/**
 * 针对一些特殊类型(如枚举)，在程序与数据库之间做类型转换
 */
public abstract class AbstractEditPropertyJdbcDaoImpl<E extends AbstractBean> extends AbstractJdbcDaoImpl<E> {
	
	protected abstract Map<Class<?>, CustomPropertyEditor> getPropertyEditorMap();
	
	/**
	 * 输入参数的类型为程序中字段的类型
	 * 返回值的类型为数据库中字段对应的类型
	 */
	protected Object processValueBeforeQuery(Object value) {
		Map<Class<?>, CustomPropertyEditor> editorMap = getPropertyEditorMap();
		if(MapUtils.isEmpty(editorMap)) {
			return value;
		}
		Object newValue = null;
		Object oldValue = value;
		if(value instanceof Collection<?>) {
			value = ((Collection<?>) value).toArray();
		}
		for(CustomPropertyEditor editor: editorMap.values()) {
			if((newValue = editor.convertValue(value)) != null) {
				break;
			}
		}
		return newValue != null? newValue: oldValue;
	}

	// 处理查询的返回结果时用
	@Override
	protected BeanMapping<E> createBeanMapping(Class<E> entityClass) {
		return new BeanMapping<E>(entityClass) {
			@Override
			protected void afterInitBeanWrapper(BeanWrapper bw) {
				for(Entry<Class<?>, CustomPropertyEditor> entry: getPropertyEditorMap().entrySet()) {
					bw.registerCustomEditor(entry.getKey(), entry.getValue());
				}
			}
		};
	}
	
	// 发送查询请求时用
	@Override
	protected SqlParameterSource createSqlParameterSource(Map<String, Object> params) {
		return new MapSqlParameterSource(params) {
			@Override
			public Object getValue(String paramName) {
				Object value = super.getValue(paramName);
				return processValueBeforeQuery(value);
			}
		};
	}
	
	// 进行更新操作时用
	@Override
	protected SqlParameterSource createSqlParameterSource(E entity) {
		return new BeanPropertySqlParameterSource(entity) {
			@Override
			public Object getValue(String paramName) { 
				Object value = super.getValue(paramName);
				return processValueBeforeQuery(value);
			}
		};
	}
	
}
