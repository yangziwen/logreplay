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
import com.sogou.map.logreplay.dao.RoleDao;
import com.sogou.map.logreplay.dao.RoleRelPermissionDao;

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
	
	@Transactional
	public void updateRelatedPermissions(Role role, List<Permission> permissionList) {
		roleRelPermissionDao.deleteRoleRelPermissionByRoleId(role.getId());
		List<RoleRelPermission> relList = new ArrayList<RoleRelPermission>();
		for(Permission permission: permissionList) {
			RoleRelPermission rel = new RoleRelPermission(role.getId(), permission.getId());
			relList.add(rel);
		}
		roleRelPermissionDao.batchSave(relList);
	}
	
}
