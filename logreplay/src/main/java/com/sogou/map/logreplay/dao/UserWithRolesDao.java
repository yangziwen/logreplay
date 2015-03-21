package com.sogou.map.logreplay.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.data.jdbc.core.OneToManyResultSetExtractor;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.sogou.map.logreplay.bean.Role;
import com.sogou.map.logreplay.bean.UserWithRoles;
import com.sogou.map.logreplay.dao.base.AbstractJdbcDaoImpl;
import com.sogou.map.logreplay.dao.base.OperationParsedResult;

@Repository
public class UserWithRolesDao extends AbstractJdbcDaoImpl<UserWithRoles> {
	
	private static final ResultSetExtractor<List<UserWithRoles>> RSE = new UserWithRolesExtractor();

	@Override
	protected String generateSqlByParam(int start, int limit, Map<String, Object> param) {
		String selectClause = new StringBuilder()
			.append(" select user.id as 'user.id', ")
			.append(" user.username,")
			.append(" user.screen_name,")
			.append(" user.create_time,")
			.append(" user.update_time,")
			.append(" user.enabled, ")
			.append(" role.id as 'role.id', ")
			.append(" role.name ")
			.toString();
		return generateSqlByParam(start, limit, selectClause, param);
	}
	
	@Override
	protected String generateSqlByParam(int start, int limit, String selectClause, Map<String, Object> param) {
		String fromClause = new StringBuilder()
			.append(" from user ")
			.append("	left join user_rel_role on user.id = user_rel_role.user_id ")
			.append("	left join role on role.id = user_rel_role.role_id ")
			.toString();
		return generateSqlByParam(start, limit, selectClause, fromClause, param);
	}
	
	@Override
	protected OperationParsedResult parseOperation(String keyWithOper) {
		OperationParsedResult parsedResult = super.parseOperation(keyWithOper);
		if(parsedResult.getKey().indexOf(".") == -1) {
			parsedResult.setKey("user." + parsedResult.getKey());
		}
		return parsedResult;
	}
	
	@Override
	protected List<UserWithRoles> doList(String sql, Map<String, Object> param) {
		if(DEBUG_SQL) {
			logger.info(sql);
		}
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
		return super.doCount(sql.substring(0, endPos) + " group by user.id", param);
	}
	
	private static class UserWithRolesExtractor extends OneToManyResultSetExtractor<UserWithRoles, Role, Long> {
		
		private static final RowMapper<UserWithRoles> USER_WITH_ROLES_ROW_MAPPER = new UserWithRolesMapper();
		
		private static final RowMapper<Role> ROLE_ROW_MAPPER = new RoleMapper();
		
		public UserWithRolesExtractor() {
			super(USER_WITH_ROLES_ROW_MAPPER, ROLE_ROW_MAPPER);
		}
		
		@Override
		protected Long mapPrimaryKey(ResultSet rs) throws SQLException {
			return rs.getLong("user.id");
		}

		@Override
		protected Long mapForeignKey(ResultSet rs) throws SQLException {
			if(rs.getObject("role.id") == null) {
				return null;
			}
			return rs.getLong("user.id");
		}

		@Override
		protected void addChild(UserWithRoles root, Role child) {
			root.addRole(child);
		}
		
	}
	
	private static class UserWithRolesMapper implements RowMapper<UserWithRoles> {

		@Override
		public UserWithRoles mapRow(ResultSet rs, int rowNum) throws SQLException {
			UserWithRoles user = new UserWithRoles();
			user.setId(rs.getLong("user.id"));
			user.setUsername(rs.getString("user.username"));
			user.setScreenName(rs.getString("user.screen_name"));
			user.setCreateTime(rs.getTimestamp("user.create_time"));
			user.setUpdateTime(rs.getTimestamp("user.update_time"));
			user.setEnabled(rs.getBoolean("user.enabled"));
			return user;
		}
		
	}
	
	private static class RoleMapper implements RowMapper<Role> {

		@Override
		public Role mapRow(ResultSet rs, int rowNum) throws SQLException {
			Role role = new Role();
			role.setId(rs.getLong("role.id"));
			role.setName(rs.getString("role.name"));
			return role;
		}
		
	}
	
}
