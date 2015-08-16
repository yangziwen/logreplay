package com.sogou.map.logreplay.mappers;

import java.util.Map.Entry;

import org.apache.ibatis.jdbc.SQL;

import com.sogou.map.logreplay.bean.base.AbstractBean;
import com.sogou.map.logreplay.dao.base.BeanMapping;


public abstract class AbstractSqlProvider<E extends AbstractBean> {
	
	public abstract BeanMapping<E> getBeanMapping();
	
	public String getById(Long id) {
		SQL sql = new SQL()
			.SELECT("*")
			.FROM(getBeanMapping().getTableName())
			.WHERE(getBeanMapping().getIdColumn() + " = #{id}");
		return sql.toString();
	}
	
	public String insert(E entity) {
		SQL sql = new SQL().INSERT_INTO(getBeanMapping().getTableName());
		String idField = getBeanMapping().getIdField();
		for(Entry<String, String> entry: getBeanMapping().getFieldColumnMapping().entrySet()) {
			if(idField.equals(entry.getKey())) {
				continue;
			}
			sql.VALUES(entry.getValue(), wrap(entry.getKey()));
		}
		return sql.toString();
	}
	
	public String update(E entity) {
		SQL sql = new SQL().UPDATE(getBeanMapping().getTableName());
		String idField = getBeanMapping().getIdField();
		String idColumn = getBeanMapping().getIdColumn();
		for(Entry<String, String> entry: getBeanMapping().getFieldColumnMapping().entrySet()) {
			if(idField.equals(entry.getKey())) {
				continue;
			}
			sql.SET(entry.getValue() +  " = " + wrap(entry.getKey()));
		}
		sql.WHERE(idColumn + " = " + wrap(idField) );
		return sql.toString();
	}
	
	public String delete(Long id) {
		SQL sql = new SQL().DELETE_FROM(getBeanMapping().getTableName());
		String idField = getBeanMapping().getIdField();
		String idColumn = getBeanMapping().getIdColumn();
		sql.WHERE(idColumn + " = " + wrap(idField));
		return sql.toString();
	}
	
	public static String wrap(String placeholder) {
		return "#{" +  placeholder + "}";
	}
	
}
