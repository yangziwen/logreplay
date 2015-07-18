package com.sogou.map.logreplay.dao;

import java.util.Map;

import org.springframework.stereotype.Repository;

import com.sogou.map.logreplay.bean.RoleRelPermission;
import com.sogou.map.logreplay.dao.base.AbstractJdbcDaoImpl;
import com.sogou.map.logreplay.dao.base.QueryParamMap;

@Repository
public class RoleRelPermissionDao extends AbstractJdbcDaoImpl<RoleRelPermission> {
	
	public void deleteRoleRelPermissionByRoleId(Long roleId) {
		String sql = "delete from role_rel_permission where role_id = :roleId";
		Map<String, Object> params = new QueryParamMap().addParam("roleId", roleId);
		jdbcTemplate.update(sql, params);
	}
	
}
