package com.sogou.map.logreplay.dao.base;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.sogou.map.logreplay.bean.AbstractBean;
import com.sogou.map.logreplay.util.ClassUtil;

public class AbstractReadOnlyJdbcDaoImpl <E extends AbstractBean> {

protected static final boolean DEBUG_SQL = BooleanUtils.toBoolean(System.getProperty("jdbc.sql.debug"));
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@SuppressWarnings("unchecked")
	protected final Class<E> entityClass = ClassUtil.getSuperClassGenericType(this.getClass(), 0);
	
	protected final String tableName = getTableNameFromAnnotation(entityClass);
	
	protected final List<Field> entityFields = Collections.unmodifiableList(getFieldsWithColumnAnnotation(entityClass));
	
	protected final String idFieldName = getIdField(entityFields).getName();
	
	protected final Map<String, String> fieldColumnMapping = Collections.unmodifiableMap(generateFieldColumnMapping(entityFields));
	
	protected final RowMapper<E> ROW_MAPPER = ParameterizedBeanPropertyRowMapper.newInstance(entityClass);
	
	@SuppressWarnings("unchecked")
	protected final E[] emptyArray = (E[])Array.newInstance(entityClass, 0);
	
	protected NamedParameterJdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	protected static String getTableNameFromAnnotation(Class<?> clazz) {
		Table table = clazz.getAnnotation(Table.class);
		if(table != null && StringUtils.isNotBlank(table.name())) {
			return table.name();
		}
		return convertCamelToUnderscore(clazz.getSimpleName());
	}
	
	protected static List<Field> getFieldsWithColumnAnnotation(Class<?> clazz) {
		List<Field> list = new ArrayList<Field>();
		if(clazz.getSuperclass() != Object.class) {
			list.addAll(getFieldsWithColumnAnnotation(clazz.getSuperclass()));
		}
		for(Field field: clazz.getDeclaredFields()) {
			if(field.getAnnotation(Column.class) == null) {
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
	protected static Field getIdField(List<Field> fields) {
		Field idNamedField = null;
		for(Field field: fields) {
			if(field.getAnnotation(Id.class) != null) {
				return field;
			}
			if("id".equalsIgnoreCase(field.getName())) {
				idNamedField = field;
			}
		}
		if(idNamedField == null) {
			throw new IllegalStateException("No id field specified in the relative entity class");
		}
		return idNamedField;
	}
	
	protected static Map<String, String> generateFieldColumnMapping(List<Field> fields) {
		Map<String, String> mapping = new LinkedHashMap<String, String>();
		for(Field field: fields) {
			Column column = field.getAnnotation(Column.class);
			if(column == null) {
				continue;
			}
			String columnName = StringUtils.isNotBlank(column.name())
					? column.name()
					: convertCamelToUnderscore(field.getName());
			mapping.put(field.getName(), columnName);
		}
		return mapping;
	}
	
	protected static String convertCamelToUnderscore(String str) {
		return StringUtils.isBlank(str)? "": str.replaceAll("([^A-Z])([A-Z])", "$1_$2").toLowerCase();
	}
	
	protected String getTableName() {
		return tableName;
	}
	
	protected String getTableName(Map<String, Object> params) {
		return getTableName();
	}
	
	//------------- 以上为一些工具方法 -------------//
	
	public E getById(Long id) {
		return first(new QueryParamMap().addParam(getColumnByField(idFieldName), id));
	}

	public List<E> list(int start, int limit, Map<String, Object> params) {
		String sql = generateSqlByParam(start, limit, params);
		return doList(sql, params);
	}

	public List<E> list(Map<String, Object> params) {
		return list(0, 0, params);
	}
	
	protected List<E> doList(String sql, Map<String, Object> params) {
		return doList(sql, params, ROW_MAPPER);
	}
	
	protected List<E> doList(String sql, Map<String, Object> params, RowMapper<E> rowMapper) {
		return doList(sql, params, new RowMapperResultSetExtractor<E>(rowMapper));
	}
	
	protected List<E> doList(String sql, Map<String, Object> params, ResultSetExtractor<List<E>> rse) {
		if(DEBUG_SQL) {
			logger.info(sql);
		}
		return jdbcTemplate.query(sql, params, rse);
	}

	public int count(Map<String, Object> params) {
		String sql = generateSqlByParam(0, 0, params);
		return doCount(sql, params);
	}
	
	/**
	 * 如果sql中存在任何子查询，请在子查询中使用大写的关键字
	 */
	protected int doCount(String sql, Map<String, Object> params) {
		int beginPos = sql.indexOf("from");
		int endPos = sql.indexOf("order by");
		if(endPos == -1) {
			endPos = sql.indexOf(" limit ");
		}
		if(endPos == -1) {
			endPos = sql.length();
		}
		if(sql.indexOf("group by") != -1) {
			sql = "select count(*) from ( select 1 " + sql.substring(beginPos, endPos) + ") as result";
		} else {
			sql = "select count(*) " + sql.substring(beginPos, endPos);
		}
		if(DEBUG_SQL) logger.info(sql);
		return jdbcTemplate.queryForObject(sql, params, Integer.class);
	}

	public Page<E> paginate(int start, int limit, Map<String, Object> params) {
		String sql = generateSqlByParam(start, limit, params);
		return new Page<E>(start, limit, doCount(sql, params), doList(sql, params));
	}

	public E first(Map<String, Object> params) {
		List<E> list = list(0, 1, params);
		return list.size() > 0 ? list.get(0) : null;
	}

	public E unique(Map<String, Object> params) {
		List<E> list = list(0, 2, params);
		int len = list.size();
		if(len > 1) {
			throw new IllegalStateException("The query result should be unique!");
		}
		return len > 0? list.get(0): null;
	}
	
	//-------------- 以下为内部方法 ----------------//

	protected String generateSqlByParam(Map<String, Object> params) {
		return generateSqlByParam(0, 0, params);
	}
	
	protected String generateSqlByParam(int start, int limit, Map<String, Object> params) {
		return generateSqlByParam(start, limit, " select * ", params);
	}
	
	protected String generateSqlByParam(String selectClause, Map<String, Object> params) {
		return generateSqlByParam(0, 0, selectClause, params);
	}
	
	protected String generateSqlByParam(int start, int limit, String selectClause, Map<String, Object> params) {
		return generateSqlByParam(start, limit, selectClause, " from " + getTableName(params), params);
	}
	
	protected String generateSqlByParam(int start, int limit, String selectClause, String fromClause, Map<String, Object> params) {
		return new StringBuilder()
			.append(selectClause)
			.append(fromClause)
			.append(" ").append(generateWhereByParam(params))
			.append(" ").append(generateGroupByByParam(params))
			.append(" ").append(generateOrderByByParam(params))
			.append(" ").append(generateLimit(start, limit, params))
			.toString();
	}
	
	protected String generateLimit(int start, int limit, Map<String, Object> params) {
		if(limit <= 0) {
			return "";
		}
		if(start < 0) {
			start = 0;
		}
		params.put(DaoConstant.START, start);
		params.put(DaoConstant.LIMIT, limit);
		return " limit :" + DaoConstant.START + ", :" + DaoConstant.LIMIT;
	}
	
	@SuppressWarnings("unchecked")
	protected String generateWhereByParam(Map<String, Object> params) {
		List<Map<String, Object>> orParamList = new ArrayList<Map<String,Object>>();
		List<String> keyList = new ArrayList<String>(params.keySet());
		for(String key: keyList) {
			if(key == null || !key.toLowerCase().endsWith(DaoConstant.OR_SUFFIX)) {
				continue;
			}
			Map<String, Object> orParam = (Map<String, Object>)params.remove(key);
			if(MapUtils.isEmpty(orParam)) {
				continue;
			}
			orParamList.add(orParam);
		}
		StringBuilder whereBuff = new StringBuilder(" where ")
			.append(generateAndConditionsByParam(params));
		for(Map<String, Object> orParam: orParamList) {
			whereBuff.append(" and (").append(generateOrConditionsByParam(orParam)).append(")");
			params.putAll(orParam);
		}
		return whereBuff.toString();
	}
	
	private String generateAndConditionsByParam(Map<String, Object> params) {
		StringBuilder andBuff = new StringBuilder(" 1 = 1 ");
		for(Entry<String, Object> entry: params.entrySet()) {
			if(DaoConstant.ORDER_BY.equals(entry.getKey())) {
				continue;
			}
			if(DaoConstant.GROUP_BY.equals(entry.getKey())) {
				continue;
			}
			// jdbcTemplate对数组不提供in的支持，所以需要先将数组转换成List
			if(entry.getValue() instanceof Object[]) {
				entry.setValue(Arrays.asList((Object[]) entry.getValue()));
			}
			OperationParsedResult parsedResult = parseOperation(entry.getKey());
			if(parsedResult == null) {
				continue;
			}
			andBuff.append(" and ").append(parsedResult.toSql());
		}
		return andBuff.toString();
	}
	
	private String generateOrConditionsByParam(Map<String, Object> params) {
		StringBuilder orBuff = new StringBuilder(" 1 = 2 ");
		for(Entry<String, Object> entry: params.entrySet()) {
			if(DaoConstant.ORDER_BY.equals(entry.getKey())) {
				continue;
			}
			if(DaoConstant.GROUP_BY.equals(entry.getKey())) {
				continue;
			}
			// jdbcTemplate对数组不提供in的支持，所以需要先将数组转换成List
			if(entry.getValue() instanceof Object[]) {
				entry.setValue(Arrays.asList((Object[]) entry.getValue()));
			}
			OperationParsedResult parsedResult = parseOperation(entry.getKey());
			if(parsedResult == null) {
				continue;
			}
			orBuff.append(" or ").append(parsedResult.toSql());
		}
		return orBuff.toString();
	}
	
	protected String generateGroupByByParam(Map<String, Object> params) {
		Object groupBy = params.remove(DaoConstant.GROUP_BY);
		if(groupBy == null) {
			return "";
		}
		if(groupBy instanceof String) {
			return generateGroupBy((String) groupBy);
		}
		if(groupBy instanceof Collection) {
			return generateGroupBy((Collection<?>) groupBy);
		}
		return " group by " + groupBy.toString();
	}
	
	private String generateGroupBy(String groupBy) {
		return StringUtils.isNotBlank(groupBy)? " group by " + groupBy: "";
	}
	
	private String generateGroupBy(Collection<?> groupBy) {
		if(CollectionUtils.isEmpty(groupBy)) {
			return "";
		}
		StringBuilder groupByBuff = new StringBuilder(" group by ");
		Iterator<?> iter = groupBy.iterator();
		groupByBuff.append(getColumnByField(iter.next().toString()));
		while(iter.hasNext()) {
			groupByBuff.append(",").append(getColumnByField(iter.next().toString()));
		}
		return groupByBuff.toString();
	}
	
	protected String generateOrderByByParam(Map<String, Object> params) {
		Object orderBy = params.remove(DaoConstant.ORDER_BY);
		if(orderBy == null) {
			return "";
		}
		if(orderBy instanceof String) {	// 这种字符串类型的orderBy比较弱
			return generateOrderBy((String) orderBy);
		}
		if(orderBy instanceof Map) {
			return generateOrderBy((Map<?, ?>) orderBy);
		}
		return "";
	}
	
	private String generateOrderBy(String orderByStr) {
		if(StringUtils.isBlank(orderByStr)) {
			return "";
		}
		return " order by " + orderByStr;
	}
	
	private String generateOrderBy(Map<?, ?> orderByParams) {
		if(MapUtils.isEmpty(orderByParams)) {
			return "";
		}
		StringBuilder orderByBuff = new StringBuilder();
		boolean isFirst = true;
		for(Entry<?, ?> entry: orderByParams.entrySet()) {
			String key = entry.getKey() != null? getColumnByField(entry.getKey().toString()): "";
			String value = entry.getValue() != null? entry.getValue().toString(): "";
			if(StringUtils.isBlank(key) && StringUtils.isBlank(value)) {
				continue;
			}
			if(isFirst) {
				orderByBuff.append(" order by ");
				isFirst = false;
			} else {
				orderByBuff.append(", ");
			}
			orderByBuff.append(key).append(" ").append(value).append(" ");
		}
		return orderByBuff.toString();
	}
	
	protected String getColumnByField(String field) {
		String column = fieldColumnMapping.get(field);
		if(StringUtils.isBlank(column)) {
			column = convertCamelToUnderscore(field);
		}
		return column;
	}
	
	protected OperationParsedResult parseOperation(String keyWithOper) {
		if (StringUtils.isBlank(keyWithOper)) {
			return null;
		}
		int index = keyWithOper.lastIndexOf("__");
		if (index == -1) {
			String key = keyWithOper;
			return QueryOperator.eq.buildResult(getColumnByField(key), keyWithOper);
		}
		String key = keyWithOper.substring(0, index);
		String operName = keyWithOper.substring(index + 2);
//		if(!fieldColumnMapping.containsKey(key)) {
//			return null;
//		}
		return QueryOperator.valueOf(operName).buildResult(getColumnByField(key), keyWithOper);
	}
	
	protected static String escapeSqlStringValue(String value) {
		if(value == null) {
			return null;
		}
		return "'" + StringEscapeUtils.escapeSql(value) + "'";
	}
	
}
