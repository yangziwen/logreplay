package io.github.yangziwen.logreplay.dao.base;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;

import io.github.yangziwen.logreplay.bean.base.AbstractBean;

public class BeanMapping<E extends AbstractBean> {

	protected Class<E> entityClass;

	protected final String tableName ;

	protected final List<Field> entityFields;

	protected final String idFieldName;

	protected final Map<String, String> fieldColumnMapping;

	protected final RowMapper<E> rowMapper;

	public final E[] emptyArray;

	@SuppressWarnings("unchecked")
	public BeanMapping(Class<E> entityClass) {
		this.entityClass = entityClass;
		this.tableName = getTableNameFromAnnotation(entityClass);
		this.entityFields = Collections.unmodifiableList(getFieldsWithColumnAnnotation(entityClass));
		this.idFieldName = getIdField(entityFields).getName();
		this.fieldColumnMapping = Collections.unmodifiableMap(generateFieldColumnMapping(entityFields));
		this.rowMapper = createRowMapper(entityClass);
		this.emptyArray = (E[])Array.newInstance(entityClass, 0);
	}

	protected RowMapper<E> createRowMapper(Class<E> entityClass) {
		BeanPropertyRowMapper<E> newInstance = new BeanPropertyRowMapper<E>() {
			@Override
			protected void initBeanWrapper(BeanWrapper bw) {
				super.initBeanWrapper(bw);
				afterInitBeanWrapper(bw);
			}
		};
		newInstance.setMappedClass(entityClass);
		return newInstance;
	}

	protected void afterInitBeanWrapper(BeanWrapper bw) {
		// defaultly do nothing
	}

	public static String getTableNameFromAnnotation(Class<?> clazz) {
		Table table = clazz.getAnnotation(Table.class);
		if (table != null && StringUtils.isNotBlank(table.name())) {
			return table.name();
		}
		return convertCamelToUnderscore(clazz.getSimpleName());
	}

	public static List<Field> getFieldsWithColumnAnnotation(Class<?> clazz) {
		List<Field> list = new ArrayList<Field>();
		if (clazz.getSuperclass() != Object.class) {
			list.addAll(getFieldsWithColumnAnnotation(clazz.getSuperclass()));
		}
		for(Field field: clazz.getDeclaredFields()) {
			if (field.getAnnotation(Column.class) == null) {
				continue;
			}
			list.add(field);
		}
		return list;
	}

	/**
	 * 寻找主键所对应的field时，
	 * 优先使用@Id，
	 * 退而使用fieldName
	 */
	public static Field getIdField(List<Field> fields) {
		Field idNamedField = null;
		for(Field field: fields) {
			if (field.getAnnotation(Id.class) != null) {
				return field;
			}
			if ("id".equalsIgnoreCase(field.getName())) {
				idNamedField = field;
			}
		}
		if (idNamedField == null) {
			throw new IllegalStateException("No id field specified in the relative entity class");
		}
		return idNamedField;
	}

	public static Map<String, String> generateFieldColumnMapping(List<Field> fields) {
		Map<String, String> mapping = new LinkedHashMap<String, String>();
		for(Field field: fields) {
			Column column = field.getAnnotation(Column.class);
			if (column == null) {
				continue;
			}
			String columnName = StringUtils.isNotBlank(column.name())
					? column.name()
					: convertCamelToUnderscore(field.getName());
			mapping.put(field.getName(), columnName);
		}
		return mapping;
	}

	public static String convertCamelToUnderscore(String str) {
		return StringUtils.isBlank(str)? "": str.replaceAll("([^\\sA-Z])([A-Z])", "$1_$2").toLowerCase();
	}

	public String getIdField() {
		return idFieldName;
	}

	public String getIdColumn() {
		return getColumnByField(getIdField());
	}

	public String getColumnByField(String field) {
		String column = fieldColumnMapping.get(field);
		if (StringUtils.isBlank(column)) {
			column = BeanMapping.convertCamelToUnderscore(field);
		}
		return column;
	}

	public Map<String, String> getFieldColumnMapping() {
		return fieldColumnMapping;
	}

	public String getTableName() {
		return tableName;
	}

	public String getTableName(Map<String, Object> params) {
		return getTableName();
	}

	public RowMapper<E> getRowMapper() {
		return rowMapper;
	}

	public E[] emptyArray() {
		return emptyArray;
	}

}
