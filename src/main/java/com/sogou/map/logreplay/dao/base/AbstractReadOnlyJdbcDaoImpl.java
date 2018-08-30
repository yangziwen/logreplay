package com.sogou.map.logreplay.dao.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.sogou.map.logreplay.bean.base.AbstractBean;
import com.sogou.map.logreplay.util.ClassUtil;

public abstract class AbstractReadOnlyJdbcDaoImpl <E extends AbstractBean> {

	/** SQL_DEBUG == true时，会打印所有查询的sql **/
	protected static final boolean SQL_DEBUG = BooleanUtils.toBoolean(System.getProperty("jdbc.sql.debug"));
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	protected BeanMapping<E> beanMapping = createBeanMapping(ClassUtil.<E>getSuperClassGenericType(this.getClass(), 0));
	
	protected NamedParameterJdbcTemplate jdbcTemplate;
	
	protected BeanMapping<E> createBeanMapping(Class<E> entityClass) {
		return new BeanMapping<E>(entityClass);
	}
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	protected String getTableName() {
		return beanMapping.getTableName();
	}
	
	public E getById(Long id) {
		return first(new QueryParamMap().addParam(beanMapping.getIdColumn(), id));
	}

	public List<E> list(int start, int limit, Map<String, Object> params) {
		String sql = generateSqlByParam(start, limit, params);
		return doList(sql, params);
	}

	public List<E> list(Map<String, Object> params) {
		return list(0, 0, params);
	}
	
	protected List<E> doList(String sql, Map<String, Object> params) {
		return doList(sql, params, beanMapping.getRowMapper());
	}
	
	protected List<E> doList(String sql, Map<String, Object> params, RowMapper<E> rowMapper) {
		return doList(sql, params, new RowMapperResultSetExtractor<E>(rowMapper));
	}
	
	protected List<E> doList(String sql, Map<String, Object> params, ResultSetExtractor<List<E>> rse) {
		if(SQL_DEBUG) {
			return outputLogInfoWithTimespan(now(), jdbcTemplate.query(sql, createSqlParameterSource(params), rse), now(), sql);
		}
		return jdbcTemplate.query(sql, createSqlParameterSource(params), rse);
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
		if(SQL_DEBUG) {
			return outputLogInfoWithTimespan(now(), jdbcTemplate.queryForObject(sql, params, Integer.class), now(), sql);
		}
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

	protected SqlParameterSource createSqlParameterSource(Map<String, Object> params) {
		return new MapSqlParameterSource(params);
	}

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
		return generateSqlByParam(start, limit, selectClause, " from " + beanMapping.getTableName(params), params);
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
		groupByBuff.append(beanMapping.getColumnByField(iter.next().toString()));
		while(iter.hasNext()) {
			groupByBuff.append(",").append(beanMapping.getColumnByField(iter.next().toString()));
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
			String key = entry.getKey() != null? beanMapping.getColumnByField(entry.getKey().toString()): "";
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
	
	protected OperationParsedResult parseOperation(String keyWithOper) {
		if (StringUtils.isBlank(keyWithOper)) {
			return null;
		}
		int index = keyWithOper.lastIndexOf(QueryOperator.__);
		if (index == -1) {
			String key = keyWithOper;
			return QueryOperator.eq.buildResult(beanMapping.getColumnByField(key), keyWithOper);
		}
		String key = keyWithOper.substring(0, index);
		String operName = keyWithOper.substring(index + 2);
		return QueryOperator.valueOf(operName).buildResult(beanMapping.getColumnByField(key), keyWithOper);
	}
	
	protected static String escapeSqlStringValue(String value) {
		if(value == null) {
			return null;
		}
		return "'" + StringEscapeUtils.escapeSql(value) + "'";
	}
	
	protected <R> R outputLogInfoWithTimespan(long t1, R result, long t2, String logInfo) {
		logger.info("[{}ms] {}", t2 - t1, logInfo);
		return result;
	}
	
	protected static long now() {
		return System.currentTimeMillis();
	}
	
}
