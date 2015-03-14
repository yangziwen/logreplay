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
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.sogou.map.logreplay.bean.AbstractBean;
import com.sogou.map.logreplay.util.ClassUtil;

public class AbstractJdbcDaoImpl<E extends AbstractBean> {
	
	protected static final boolean DEBUG_SQL = BooleanUtils.toBoolean(System.getProperty("jdbc.sql.debug"));
	
	private static final Pattern GROUP_BY_PATTERN = Pattern.compile("group by", Pattern.CASE_INSENSITIVE);
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	protected NamedParameterJdbcTemplate jdbcTemplate;
	
	protected SimpleJdbcInsert jdbcInsert;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		this.jdbcInsert = new SimpleJdbcInsert(dataSource)
			.withTableName(tableName)
			.usingGeneratedKeyColumns(getColumnByField(idFieldName));
	}

	@SuppressWarnings("unchecked")
	protected final Class<E> entityClass = ClassUtil.getSuperClassGenericType(this.getClass(), 0);
	
	protected final String tableName = getTableNameFromAnnotation(entityClass);
	
	protected final List<Field> entityFields = Collections.unmodifiableList(getFieldsWithColumnAnnotation(entityClass));
	
	protected final String idFieldName = getIdField(entityFields).getName();
	
	protected final Map<String, String> fieldColumnMapping = Collections.unmodifiableMap(generateFieldColumnMapping(entityFields));
	
	protected final String updateSql = generateUpdateSql(tableName, idFieldName, fieldColumnMapping);
	
	protected final RowMapper<E> ROW_MAPPER = ParameterizedBeanPropertyRowMapper.newInstance(entityClass);
	
	@SuppressWarnings("unchecked")
	protected final E[] emptyArray = (E[])Array.newInstance(entityClass, 0);
			
	private static String getTableNameFromAnnotation(Class<?> clazz) {
		Table table = clazz.getAnnotation(Table.class);
		if(table != null && StringUtils.isNotBlank(table.name())) {
			return table.name();
		}
		return convertCamelToUnderscore(clazz.getSimpleName());
	}
	
	private static List<Field> getFieldsWithColumnAnnotation(Class<?> clazz) {
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
	private static Field getIdField(List<Field> fields) {
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
	
	private static Map<String, String> generateFieldColumnMapping(List<Field> fields) {
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
	
	private static String convertCamelToUnderscore(String str) {
		return StringUtils.isBlank(str)? "": str.replaceAll("([^A-Z])([A-Z])", "$1_$2").toLowerCase();
	}
	
	private static String generateUpdateSql(String tableName, String idFieldName, Map<String, String> fieldColumnMapping) {
		Map<String, String> mappingWithoutId = new LinkedHashMap<String, String>(fieldColumnMapping);
		String idColumnName = mappingWithoutId.remove(idFieldName);
		StringBuilder updateBuff = new StringBuilder().append(" update ").append(tableName);
		@SuppressWarnings("unchecked")
		Entry<String, String>[] entrys = mappingWithoutId.entrySet().toArray(new Entry[]{});
		Entry<String, String> entry = entrys[0];
		updateBuff.append(" set ").append(entry.getValue()).append("=:").append(entry.getKey());
		for(int i=1; i<entrys.length; i++) {
			entry = entrys[i];
			updateBuff.append(", ").append(entry.getValue()).append("=:").append(entry.getKey());
		}
		updateBuff.append(" where ").append(idColumnName).append("=:").append(idFieldName);
		return updateBuff.toString();
	}
	
	//------------- 以上为一些工具方法 -------------//
	
	public void save(E entity) {
		Number id = jdbcInsert.executeAndReturnKey(new BeanPropertySqlParameterSource(entity));
		entity.setId(id.longValue());
	}
	
	public void update(E entity) {
		jdbcTemplate.update(updateSql, new BeanPropertySqlParameterSource(entity));
	}

	public void saveOrUpdate(E entity) {
		if(entity.getId() == null) {
			save(entity);
		} else {
			update(entity);
		}
	}

	public void delete(E entity) {
		deleteById(entity.getId());
	}

	public void deleteById(Long id) {
		String sql = "delete from " + tableName + " where " + getColumnByField(idFieldName) + " = :id";
		jdbcTemplate.update(sql, new MapSqlParameterSource().addValue("id", id));
	}

	public E getById(Long id) {
		return first(new QueryParamMap().addParam(getColumnByField(idFieldName), id));
	}

	public List<E> list(int start, int limit, Map<String, Object> param) {
		String sql = generateSqlByParam(start, limit, param);
		return doList(sql, param);
	}

	public List<E> list(Map<String, Object> param) {
		return list(0, 0, param);
	}
	
	protected List<E> doList(String sql, Map<String, Object> params) {
		return doList(sql, params, ROW_MAPPER);
	}
	
	protected List<E> doList(String sql, Map<String, Object> params, RowMapper<E> rowMapper) {
		if(DEBUG_SQL) logger.info(sql);
		return jdbcTemplate.query(sql, params, rowMapper);
	}

	public int count(Map<String, Object> param) {
		String sql = generateSqlByParam(0, 0, param);
		return doCount(sql, param);
	}
	
	protected int doCount(String sql, Map<String, Object> param) {
		int beginPos = sql.indexOf("from");
		int endPos = sql.indexOf("order by");
		if(endPos == -1) {
			endPos = sql.indexOf(" limit ");
		}
		if(endPos == -1) {
			endPos = sql.length();
		}
		if(GROUP_BY_PATTERN.matcher(sql).find()) {
			sql = "select count(*) from ( select 1 " + sql.substring(beginPos, endPos) + ") as result";
		} else {
			sql = "select count(*) " + sql.substring(beginPos, endPos);
		}
		if(DEBUG_SQL) logger.info(sql);
		return jdbcTemplate.queryForObject(sql, param, Integer.class);
	}

	public Page<E> paginate(int start, int limit, Map<String, Object> param) {
		String sql = generateSqlByParam(start, limit, param);
		return new Page<E>(start, limit, doCount(sql, param), doList(sql, param));
	}

	public E first(Map<String, Object> param) {
		List<E> list = list(0, 1, param);
		return list.size() > 0 ? list.get(0) : null;
	}

	public E unique(Map<String, Object> param) {
		List<E> list = list(0, 2, param);
		int len = list.size();
		if(len > 1) {
			throw new IllegalStateException("The query result should be unique!");
		}
		return len > 0? list.get(0): null;
	}
	
	/**
	 * 注意，数组中的对象在batchUpdate之后，是无法获取id的
	 */
	public int batchSave(List<E> entityList, int batchSize) {
		if(CollectionUtils.isEmpty(entityList)) {
			return 0;
		}
		return batchSave(entityList.toArray(emptyArray), batchSize);
	}
	
	public int batchSave(E[] entities, int batchSize) {
		if(ArrayUtils.isEmpty(entities)) {
			return 0;
		}
		for(E entity: entities) {
			if(entity == null) {
				throw new IllegalArgumentException("Entity should not be null before insertion!");
			}
			if(entity.getId() != null) {
				throw new IllegalArgumentException("The id of the entity should be null before insersion!");
			}
		}
		if(batchSize <= 0) {
			batchSize = entities.length;
		}
		int insertRows = 0;
		for(int i = 0, l = entities.length; i < l; i += batchSize) {
			SqlParameterSource[] paramSources = SqlParameterSourceUtils.createBatch(
					ArrayUtils.subarray(entities, i, Math.min(i + batchSize, l)));
			for(int c: jdbcInsert.executeBatch(paramSources)) {
				insertRows += c;
			}
		}
		return insertRows;
	}
	
	public int batchUpdate(List<E> entityList, int batchSize) {
		if(CollectionUtils.isEmpty(entityList)) {
			return 0;
		}
		return batchUpdate(entityList.toArray(emptyArray), batchSize);
	}
	
	public int batchUpdate(E[] entities, int batchSize) {
		if(ArrayUtils.isEmpty(entities)) {
			return 0;
		}
		for(E entity: entities) {
			if(entity == null) {
				throw new IllegalArgumentException("The entity should not be null before update!");
			}
			if(entity.getId() == null || entity.getId() <= 0) {
				throw new IllegalArgumentException("The id of the entity is not valid!");
			}
		}
		if(batchSize <= 0) {
			batchSize = entities.length;
		}
		int updateRows = 0;
		for(int i = 0, l = entities.length; i < l; i += batchSize) {
			SqlParameterSource[] paramSources = SqlParameterSourceUtils.createBatch(
					ArrayUtils.subarray(entities, i, Math.min(i + batchSize, l)));
			for(int c: jdbcTemplate.batchUpdate(updateSql, paramSources)) {
				updateRows += c;
			}
		}
		return updateRows;
	}
	
	public int batchSaveOrUpdate(List<E> entityList, int batchSize) {
		if(CollectionUtils.isEmpty(entityList)) {
			return 0;
		}
		return batchSaveOrUpdate(entityList.toArray(emptyArray), batchSize);
	}
	
	public int batchSaveOrUpdate(E[] entities, int batchSize) {
		if(ArrayUtils.isEmpty(entities)) {
			return 0;
		}
		int initCapacity = entities.length / 2 + 1;
		List<E> toSaveList = new ArrayList<E>(initCapacity);
		List<E> toUpdateList = new ArrayList<E>(initCapacity);
		for(E entity: entities) {
			if(entity == null) {
				continue;
			}
			if(entity.getId() == null) {
				toSaveList.add(entity);
			} else {
				toUpdateList.add(entity);
			}
		}
		return 	batchSave(toSaveList.toArray(emptyArray), batchSize) + 
				batchUpdate(toUpdateList.toArray(emptyArray), batchSize);
	}
	
	public int batchDeleteByIds(Collection<Long> ids) {
		if(CollectionUtils.isEmpty(ids)) {
			return 0;
		}
		String sql = "delete from " + tableName + " where " + getColumnByField(idFieldName) + " in (:ids)";
		return jdbcTemplate.update(sql, new MapSqlParameterSource().addValue("ids", ids));
	}
	
	public int executeSql(String sql) {
		return jdbcTemplate.getJdbcOperations().update(sql);
	}

	public int executeSql(String sql, Map<String, Object> param) {
		return jdbcTemplate.update(sql, param);
	}
	
	//-------------- 以下为内部方法 ----------------//
	
	protected String generateSqlByParam(Map<String, Object> param) {
		return generateSqlByParam(0, 0, param);
	}
	
	protected String generateSqlByParam(int start, int limit, Map<String, Object> param) {
		return generateSqlByParam(start, limit, " select * ", param);
	}
	
	protected String generateSqlByParam(String selectClause, Map<String, Object> param) {
		return generateSqlByParam(0, 0, selectClause, param);
	}
	
	protected String generateSqlByParam(int start, int limit, String selectClause, Map<String, Object> param) {
		return generateSqlByParam(start, limit, selectClause, " from " + tableName, param);
	}
	
	protected String generateSqlByParam(int start, int limit, String selectClause, String fromClause, Map<String, Object> param) {
		return new StringBuilder()
			.append(selectClause)
			.append(fromClause)
			.append(" ").append(generateWhereByParam(param))
			.append(" ").append(generateGroupByByParam(param))
			.append(" ").append(generateOrderByByParam(param))
			.append(" ").append(generateLimit(start, limit, param))
			.toString();
	}
	
	protected String generateLimit(int start, int limit, Map<String, Object> param) {
		if(limit <= 0) {
			return "";
		}
		if(start < 0) {
			start = 0;
		}
		param.put(DaoConstant.START, start);
		param.put(DaoConstant.LIMIT, limit);
		return " limit :" + DaoConstant.START + ", :" + DaoConstant.LIMIT;
	}
	
	@SuppressWarnings("unchecked")
	protected String generateWhereByParam(Map<String, Object> param) {
		List<Map<String, Object>> orParamList = new ArrayList<Map<String,Object>>();
		List<String> keyList = new ArrayList<String>(param.keySet());
		for(String key: keyList) {
			if(key == null || !key.toLowerCase().endsWith(DaoConstant.OR_SUFFIX)) {
				continue;
			}
			Map<String, Object> orParam = (Map<String, Object>)param.remove(key);
			if(MapUtils.isEmpty(orParam)) {
				continue;
			}
			orParamList.add(orParam);
		}
		StringBuilder whereBuff = new StringBuilder(" where ")
			.append(generateAndConditionsByParam(param));
		for(Map<String, Object> orParam: orParamList) {
			whereBuff.append(" and (").append(generateOrConditionsByParam(orParam)).append(")");
			param.putAll(orParam);
		}
		return whereBuff.toString();
	}
	
	private String generateAndConditionsByParam(Map<String, Object> param) {
		StringBuilder andBuff = new StringBuilder(" 1 = 1 ");
		for(Entry<String, Object> entry: param.entrySet()) {
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
	
	private String generateOrConditionsByParam(Map<String, Object> param) {
		StringBuilder orBuff = new StringBuilder(" 1 = 2 ");
		for(Entry<String, Object> entry: param.entrySet()) {
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
	
	protected String generateGroupByByParam(Map<String, Object> param) {
		Object groupBy = param.remove(DaoConstant.GROUP_BY);
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
	
	protected String generateOrderByByParam(Map<String, Object> param) {
		Object orderBy = param.remove(DaoConstant.ORDER_BY);
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
	
	private String generateOrderBy(Map<?, ?> orderByParam) {
		if(MapUtils.isEmpty(orderByParam)) {
			return "";
		}
		StringBuilder orderByBuff = new StringBuilder();
		boolean isFirst = true;
		for(Entry<?, ?> entry: orderByParam.entrySet()) {
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
