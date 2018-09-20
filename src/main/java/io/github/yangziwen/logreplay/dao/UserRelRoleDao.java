package io.github.yangziwen.logreplay.dao;

import java.util.Map;

import org.springframework.stereotype.Repository;

import io.github.yangziwen.logreplay.bean.UserRelRole;
import io.github.yangziwen.logreplay.dao.base.AbstractJdbcDaoImpl;
import io.github.yangziwen.logreplay.dao.base.QueryParamMap;

@Repository
public class UserRelRoleDao extends AbstractJdbcDaoImpl<UserRelRole> {
	
	public void deleteUserRelRolesByUserId(Long userId) {
		String sql = "delete from user_rel_role where user_id = :userId";
		Map<String, Object> params = new QueryParamMap().addParam("userId", userId);
		jdbcTemplate.update(sql, params);
	}

}
