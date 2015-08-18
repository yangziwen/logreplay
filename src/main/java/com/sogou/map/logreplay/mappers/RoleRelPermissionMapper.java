package com.sogou.map.logreplay.mappers;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.session.RowBounds;

import com.sogou.map.logreplay.bean.RoleRelPermission;

public interface RoleRelPermissionMapper {

	@Delete("delete from role_rel_permission where role_id = #{roleId}")
	public void deleteRoleRelPermissionByRoleId(Long roleId);
	
	public int batchSave(List<RoleRelPermission> list);
	
	public List<RoleRelPermission> list(Map<String, Object> params);
	
	public List<RoleRelPermission> list(Map<String, Object> params, RowBounds rowBounds);
	
}
