package com.sogou.map.logreplay.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.sogou.map.logreplay.bean.PageInfo;
import com.sogou.map.logreplay.bean.TagInfo;
import com.sogou.map.logreplay.dao.base.AbstractJdbcDaoImpl;
import com.sogou.map.logreplay.dao.base.OperationParsedResult;
import com.sogou.map.logreplay.dao.base.QueryParamMap;

@Repository
public class TagInfoDao extends AbstractJdbcDaoImpl<TagInfo> {
	
	public void updatePageNoByPageInfoId(Long pageInfoId, Integer pageNo) {
		String sql = "update tag_info set page_no = :pageNo where page_info_id = :pageInfoId";
		Map<String, Object> param = new QueryParamMap().addParam("pageInfoId", pageInfoId).addParam("pageNo", pageNo);
		jdbcTemplate.update(sql, param);
	}
	
	@Override
	protected String generateSqlByParam(int start, int limit, Map<String, Object> param) {
		String selectClause = new StringBuilder()
			.append(" select tag_info.*, ")
			.append(" page_info.name as page_name,")
			.append(" page_info.page_no as page_no, ")
			.append(" page_info.create_time as page_create_time,")
			.append(" page_info.update_time as page_update_time")
			.toString();
		return generateSqlByParam(start, limit, selectClause, param);
	}
	
	@Override
	protected String generateSqlByParam(int start, int limit, String selectClause, Map<String, Object> param) {
		return generateSqlByParam(start, limit, selectClause, " from " + getTableName() + " left join page_info on page_info.id = page_info_id ", param);
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
	protected List<TagInfo> doList(String sql, Map<String, Object> param) {
		return doList(sql, param, new RowMapper<TagInfo>() {
			@Override
			public TagInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
				TagInfo tagInfo = ROW_MAPPER.mapRow(rs, rowNum);
				PageInfo pageInfo = new PageInfo();
				pageInfo.setId(tagInfo.getPageInfoId());
				pageInfo.setName(rs.getString("page_name"));
				pageInfo.setPageNo(rs.getInt("page_no"));
				pageInfo.setCreateTime(rs.getTimestamp("page_create_time"));
				pageInfo.setUpdateTime(rs.getTimestamp("page_update_time"));
				tagInfo.setPageInfo(pageInfo);
				return tagInfo;
			}
		});
	}
}
