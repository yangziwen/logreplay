package com.sogou.map.logreplay.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sogou.map.logreplay.bean.Permission;
import com.sogou.map.logreplay.bean.Role;
import com.sogou.map.logreplay.bean.RoleRelPermission;
import com.sogou.map.logreplay.mappers.RoleMapper;
import com.sogou.map.logreplay.mappers.RoleRelPermissionMapper;

@Service
public class RoleService {

	@Autowired
	private RoleMapper roleMapper;
	
	@Autowired
	private RoleRelPermissionMapper roleRelPermissionMapper;
	
	public Role getRoleById(Long id) {
		return roleMapper.getById(id);
	}
	
	public List<Role> getRoleListResult(Map<String, Object> params) {
		return roleMapper.list(params);
	}
	
	public void updateRole(Role role) {
		roleMapper.update(role);
	}
	
	@Transactional
	public void updateRelatedPermissions(Role role, List<Permission> permissionList) {
		roleRelPermissionMapper.deleteRoleRelPermissionByRoleId(role.getId());
		List<RoleRelPermission> relList = new ArrayList<RoleRelPermission>();
		for(Permission permission: permissionList) {
			RoleRelPermission rel = new RoleRelPermission(role.getId(), permission.getId());
			relList.add(rel);
		}
		roleRelPermissionMapper.batchSave(relList);
	}
	
}
