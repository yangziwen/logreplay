package io.github.yangziwen.logreplay.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import io.github.yangziwen.logreplay.bean.OperationRecord;
import io.github.yangziwen.logreplay.dao.base.AbstractJdbcDaoImpl;
import io.github.yangziwen.logreplay.dao.base.OperationParsedResult;

@Repository
public class OperationRecordDao extends AbstractJdbcDaoImpl<OperationRecord> {

	@Override
	protected String generateSqlByParam(int start, int limit, Map<String, Object> params) {
		String selectClause = new StringBuilder()
			.append(" select operation_record.*, ")
			.append(" tag_info.id as tag_info_id ")
			.toString();
		return generateSqlByParam(start, limit, selectClause, params);
	}
	
	@Override
	protected String generateSqlByParam(int start, int limit, String selectClause, Map<String, Object> params) {
		String fromClause = " from " + getTableName() 
				+ " left join tag_info on tag_info.tag_no = operation_record.tag_no "
				+ " and tag_info.page_no = operation_record.page_no "
				+ " and tag_info.product_id = operation_record.product_id ";
		return generateSqlByParam(start, limit, selectClause, fromClause, params);
	}
	
	@Override
	protected OperationParsedResult parseOperation(String keyWithOper) {
		OperationParsedResult parsedResult = super.parseOperation(keyWithOper);
		if(parsedResult.getKey().indexOf(".") == -1) {
			parsedResult.setKey(getTableName() + "." + parsedResult.getKey());
		}
		return parsedResult;
	}
	
	@Override
	protected List<OperationRecord> doList(String sql, Map<String, Object> params) {
		return doList(sql, params, new RowMapper<OperationRecord>() {
			@Override
			public OperationRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
				OperationRecord record = beanMapping.getRowMapper().mapRow(rs, rowNum);
				record.setTagInfoId(rs.getLong("tag_info_id"));
				return record;
			}
		});
	}
	
	public int batchSaveByDirectSql(OperationRecord[] records, int batchSize) {
		if(ArrayUtils.isEmpty(records)) {
			return 0;
		}
		for(OperationRecord record: records) {
			if(record.getId() != null) {
				throw new IllegalArgumentException("Id of the OperationRecord should be 0 before insert! [" + record + "]");
			}
		}
		if(batchSize <= 0) {
			batchSize = 100;
		}
		for(int i = 0, l = records.length; i < l; i += batchSize) {
			String sql = generateBatchSaveSql(records, i, Math.min(i + batchSize, l));
			jdbcTemplate.getJdbcOperations().execute(sql);
		}
		return records.length;
	}
	
	protected static String generateBatchSaveSql(OperationRecord[] records, int startIndexInclusive, int endIndexExclusive) {
		StringBuilder sqlBuff = new StringBuilder()
			.append(" insert into operation_record (ip, product_id, device_id, uvid, os, version, timestamp, page_no, tag_no, params) values ");
		OperationRecord record = null;
		for(int i = startIndexInclusive; i < endIndexExclusive; i++) {
			if(i > startIndexInclusive) {
				sqlBuff.append(",");
			}
			record = records[i];
			sqlBuff.append("(")
				.append(escapeSqlStringValue(record.getIp()))
				.append(", ")
				.append(record.getProductId())
				.append(", ")
				.append(escapeSqlStringValue(record.getDeviceId()))
				.append(", ")
				.append(escapeSqlStringValue(record.getUvid()))
				.append(", ")
				.append(escapeSqlStringValue(record.getOs()))
				.append(", ")
				.append(record.getVersion())
				.append(", ")
				.append(record.getTimestamp())
				.append(", ")
				.append(record.getPageNo())
				.append(", ")
				.append(record.getTagNo())
				.append(", ")
				.append(escapeSqlStringValue(record.getParams()))
				.append(")");
		}
		return sqlBuff.toString();
	}
	
}
