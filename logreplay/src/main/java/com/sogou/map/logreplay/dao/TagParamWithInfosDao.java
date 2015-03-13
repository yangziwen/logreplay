package com.sogou.map.logreplay.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.data.jdbc.core.OneToManyResultSetExtractor;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.sogou.map.logreplay.bean.ParamInfo;
import com.sogou.map.logreplay.bean.TagParam;
import com.sogou.map.logreplay.dao.base.AbstractJdbcDaoImpl;
import com.sogou.map.logreplay.dao.base.OperationParsedResult;

@Repository
public class TagParamWithInfosDao extends AbstractJdbcDaoImpl<TagParam> {

	private static final ResultSetExtractor<List<TagParam>> RSE = new TagParamWithInfosExtractor();
	
	@Override
	protected String generateSqlByParam(int start, int limit, Map<String, Object> param) {
		String selectClause = new StringBuilder()
			.append(" select tag_param.id as 'tag_param.id', ")
			.append(" tag_param.tag_info_id,")
			.append(" tag_param.comment,")
			.append(" param_info.id as 'param_info.id',")
			.append(" param_info.tag_param_id, ")
			.append(" param_info.name,")
			.append(" param_info.value, ")
			.append(" param_info.description ")
			.toString();
		return generateSqlByParam(start, limit, selectClause, param);
	}
	
	@Override
	protected String generateSqlByParam(int start, int limit, String selectClause, Map<String, Object> param) {
		String fromClause = new StringBuilder()
			.append(" from tag_param ")
			.append("	left join param_info on tag_param.id = param_info.tag_param_id ")
			.toString();
		return generateSqlByParam(start, limit, selectClause, fromClause, param);
	}
	
	@Override
	protected OperationParsedResult parseOperation(String keyWithOper) {
		OperationParsedResult parsedResult = super.parseOperation(keyWithOper);
		if(parsedResult.getKey().indexOf(".") == -1) {
			parsedResult.setKey("tag_param." + parsedResult.getKey());
		}
		return parsedResult;
	}
	
	@Override
	protected List<TagParam> doList(String sql, Map<String, Object> param) {
		if(DEBUG_SQL) logger.info(sql);
		return jdbcTemplate.query(sql, param, RSE);
	}
	
	@Override
	protected int doCount(String sql, Map<String, Object> param) {
		int endPos = sql.indexOf("order by");
		if(endPos == -1) {
			endPos = sql.indexOf(" limit ");
		}
		if(endPos == -1) {
			endPos = sql.length();
		}
		return super.doCount(sql.substring(0, endPos) + " group by tag_param.id", param);
	}
	
	private static class TagParamWithInfosExtractor extends OneToManyResultSetExtractor<TagParam, ParamInfo, Long> {
		
		private static final RowMapper<TagParam> TAG_PARAM_WITH_INFOS_ROW_MAPPER = new TagParamWithInfosMapper();
		
		private static final RowMapper<ParamInfo> PARAM_INFO_ROW_MAPPER = new ParamInfoMapper();
		
		public TagParamWithInfosExtractor() {
			super(TAG_PARAM_WITH_INFOS_ROW_MAPPER, PARAM_INFO_ROW_MAPPER);
		}
		
		@Override
		protected Long mapPrimaryKey(ResultSet rs) throws SQLException {
			return rs.getLong("tag_param.id");
		}

		@Override
		protected Long mapForeignKey(ResultSet rs) throws SQLException {
			if(rs.getObject("param_info.id") == null) {
				return null;
			}
			return rs.getLong("tag_param.id");
		}

		@Override
		protected void addChild(TagParam root, ParamInfo child) {
			root.addParamInfo(child);
		}
		
	}
	
	private static class TagParamWithInfosMapper implements RowMapper<TagParam> {
		@Override
		public TagParam mapRow(ResultSet rs, int rowNum) throws SQLException {
			TagParam tagParam = new TagParam();
			tagParam.setId(rs.getLong("tag_param.id"));
			tagParam.setTagInfoId(rs.getLong("tag_param.tag_info_id"));
			tagParam.setComment(rs.getString("tag_param.comment"));
			return tagParam;
		}
		
	}
	
	private static class ParamInfoMapper implements RowMapper<ParamInfo> {
		@Override
		public ParamInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
			ParamInfo paramInfo = new ParamInfo();
			paramInfo.setId(rs.getLong("param_info.id"));
			paramInfo.setTagParamId(rs.getLong("param_info.tag_param_id"));
			paramInfo.setName(rs.getString("param_info.name"));
			paramInfo.setValue(rs.getString("param_info.value"));
			paramInfo.setDescription(rs.getString("param_info.description"));
			return paramInfo;
		}
		
	}
}
