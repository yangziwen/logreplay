package io.github.yangziwen.logreplay.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.yangziwen.logreplay.bean.Permission;
import io.github.yangziwen.logreplay.bean.Role;
import io.github.yangziwen.logreplay.bean.RoleRelPermission;
import io.github.yangziwen.logreplay.dao.RoleDao;
import io.github.yangziwen.logreplay.dao.RoleRelPermissionDao;

@Service
public class RoleService {

	@Autowired
	private RoleDao roleDao;
	
	@Autowired
	private RoleRelPermissionDao roleRelPermissionDao;
	
	public Role getRoleById(Long id) {
		return roleDao.getById(id);
	}
	
	public List<Role> getRoleListResult(Map<String, Object> params) {
		return roleDao.list(params);
	}
	
	public void updateRole(Role role) {
		roleDao.update(role);
	}
	
	@Transactional
	public void updateRelatedPermissions(Role role, List<Permission> permissionList) {
		roleRelPermissionDao.deleteRoleRelPermissionByRoleId(role.getId());
		List<RoleRelPermission> relList = new ArrayList<RoleRelPermission>();
		for (Permission permission: permissionList) {
			RoleRelPermission rel = new RoleRelPermission(role.getId(), permission.getId());
			relList.add(rel);
		}
		roleRelPermissionDao.batchSave(relList);
	}
	
}
