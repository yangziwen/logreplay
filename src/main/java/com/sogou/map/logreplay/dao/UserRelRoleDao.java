package com.sogou.map.logreplay.dao;

import java.util.Map;

import org.springframework.stereotype.Repository;

import com.sogou.map.logreplay.bean.UserRelRole;
import com.sogou.map.logreplay.dao.base.AbstractJdbcDaoImpl;
import com.sogou.map.logreplay.dao.base.QueryParamMap;

@Repository
public class UserRelRoleDao extends AbstractJdbcDaoImpl<UserRelRole> {
	
	public void deleteUserRelRolesByUserId(Long userId) {
		String sql = "delete from user_rel_role where user_id = :userId";
		Map<String, Object> params = new QueryParamMap().addParam("userId", userId);
		jdbcTemplate.update(sql, params);
	}

}
