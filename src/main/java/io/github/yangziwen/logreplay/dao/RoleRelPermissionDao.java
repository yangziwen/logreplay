package io.github.yangziwen.logreplay.dao;

import java.util.Map;

import org.springframework.stereotype.Repository;

import io.github.yangziwen.logreplay.bean.RoleRelPermission;
import io.github.yangziwen.logreplay.dao.base.AbstractJdbcDaoImpl;
import io.github.yangziwen.logreplay.dao.base.QueryParamMap;

@Repository
public class RoleRelPermissionDao extends AbstractJdbcDaoImpl<RoleRelPermission> {
	
	public void deleteRoleRelPermissionByRoleId(Long roleId) {
		String sql = "delete from role_rel_permission where role_id = :roleId";
		Map<String, Object> params = new QueryParamMap().addParam("roleId", roleId);
		jdbcTemplate.update(sql, params);
	}
	
}
