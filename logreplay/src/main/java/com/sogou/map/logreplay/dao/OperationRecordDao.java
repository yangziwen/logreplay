package com.sogou.map.logreplay.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.sogou.map.logreplay.bean.OperationRecord;
import com.sogou.map.logreplay.dao.base.AbstractJdbcDaoImpl;
import com.sogou.map.logreplay.dao.base.OperationParsedResult;

@Repository
public class OperationRecordDao extends AbstractJdbcDaoImpl<OperationRecord> {

	@Override
	protected String generateSqlByParam(int start, int limit, Map<String, Object> param) {
		String selectClause = new StringBuilder()
			.append(" select operation_record.*, ")
			.append(" tag_info.id as tag_info_id ")
			.toString();
		return generateSqlByParam(start, limit, selectClause, param);
	}
	
	@Override
	protected String generateSqlByParam(int start, int limit, String selectClause, Map<String, Object> param) {
		String fromClause = " from " + tableName 
				+ " left join tag_info on tag_info.tag_no = operation_record.tag_no "
				+ " and tag_info.page_no = operation_record.page_no ";
		return generateSqlByParam(start, limit, selectClause, fromClause, param);
	}
	
	@Override
	protected OperationParsedResult parseOperation(String keyWithOper) {
		OperationParsedResult parsedResult = super.parseOperation(keyWithOper);
		parsedResult.setKey(tableName + "." + parsedResult.getKey());
		return parsedResult;
	}
	
	@Override
	public List<OperationRecord> doList(String sql, Map<String, Object> param) {
		return jdbcTemplate.query(sql, param, new RowMapper<OperationRecord>() {
			@Override
			public OperationRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
				OperationRecord record = ROW_MAPPER.mapRow(rs, rowNum);
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
			.append(" insert into operation_record (ip, device_id, uvid, os, version, timestamp, page_no, tag_no, params_json) values ");
		OperationRecord record = null;
		for(int i = startIndexInclusive; i < endIndexExclusive; i++) {
			if(i > startIndexInclusive) {
				sqlBuff.append(",");
			}
			record = records[i];
			sqlBuff.append("(")
				.append(escapeSqlStringValue(record.getIp()))
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
