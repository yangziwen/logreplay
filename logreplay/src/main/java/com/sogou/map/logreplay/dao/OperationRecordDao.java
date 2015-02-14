package com.sogou.map.logreplay.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

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
	
}
