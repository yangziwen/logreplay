package com.sogou.map.logreplay.dao.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.sogou.map.logreplay.bean.base.AbstractBean;

public class AbstractJdbcDaoImpl<E extends AbstractBean>  extends AbstractReadOnlyJdbcDaoImpl<E> {
	
	protected final String updateSql = generateUpdateSql(beanMapping.getTableName(), beanMapping.getIdField(), beanMapping.getFieldColumnMapping());
	
	protected SimpleJdbcInsert jdbcInsert;
	
	@Override
	@Autowired
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
		this.jdbcInsert = new SimpleJdbcInsert(dataSource)
			.withTableName(beanMapping.getTableName())
			.usingGeneratedKeyColumns(beanMapping.getIdColumn());
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
		String sql = "delete from " + beanMapping.getTableName() + " where " + beanMapping.getIdColumn() + " = :id";
		jdbcTemplate.update(sql, new MapSqlParameterSource().addValue("id", id));
	}
	
	/**
	 * 注意，数组中的对象在batchUpdate之后，是无法获取id的
	 */
	public int batchSave(List<E> entityList) {
		return batchSave(entityList, -1);
	}
	
	public int batchSave(List<E> entityList, int batchSize) {
		if(CollectionUtils.isEmpty(entityList)) {
			return 0;
		}
		return batchSave(entityList.toArray(beanMapping.emptyArray()), batchSize);
	}
	
	public int batchSave(E[] entities) {
		return batchSave(entities, -1);
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
	
	public int batchUpdate(List<E> entityList) {
		return batchUpdate(entityList, -1);
	}
	
	public int batchUpdate(List<E> entityList, int batchSize) {
		if(CollectionUtils.isEmpty(entityList)) {
			return 0;
		}
		return batchUpdate(entityList.toArray(beanMapping.emptyArray()), batchSize);
	}
	
	public int batchUpdate(E[] entities) {
		return batchUpdate(entities, -1);
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
		return batchSaveOrUpdate(entityList.toArray(beanMapping.emptyArray()), batchSize);
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
		return 	batchSave(toSaveList.toArray(beanMapping.emptyArray()), batchSize) + 
				batchUpdate(toUpdateList.toArray(beanMapping.emptyArray()), batchSize);
	}
	
	public int batchDeleteByIds(Collection<Long> ids) {
		if(CollectionUtils.isEmpty(ids)) {
			return 0;
		}
		String sql = "delete from " + beanMapping.getTableName() + " where " + beanMapping.getIdColumn() + " in (:ids)";
		return jdbcTemplate.update(sql, new MapSqlParameterSource().addValue("ids", ids));
	}
	
	public int executeSql(String sql) {
		return jdbcTemplate.getJdbcOperations().update(sql);
	}

	public int executeSql(String sql, Map<String, Object> params) {
		return jdbcTemplate.update(sql, params);
	}

}
