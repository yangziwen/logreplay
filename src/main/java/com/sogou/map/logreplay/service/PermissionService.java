package com.sogou.map.logreplay.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.sogou.map.logreplay.bean.Permission;
import com.sogou.map.logreplay.bean.Role;
import com.sogou.map.logreplay.bean.RoleRelPermission;
import com.sogou.map.logreplay.dao.PermissionDao;
import com.sogou.map.logreplay.dao.RoleRelPermissionDao;
import com.sogou.map.logreplay.dao.base.QueryParamMap;

@Service
public class PermissionService {

	@Autowired
	private PermissionDao permissionDao;

	@Autowired
	private RoleRelPermissionDao roleRelPermissionDao;
	
	public List<Permission> getPermissionListResult(Map<String, Object> params) {
		return permissionDao.list(params);
	}
	
	public List<Permission> getPermissionListByRole(Role role, boolean excluded) {
		return getPermissionListByRoleId(role != null? role.getId(): null, excluded);
	}
	
	public List<Permission> getPermissionListByRoleId(Long roleId, boolean excluded) {
		if(roleId == null && !excluded) {
			return Collections.emptyList();
		}
		List<RoleRelPermission> relList = roleRelPermissionDao.list(new QueryParamMap()
			.addParam("roleId", roleId)
		);
		return getPermissionListByRelList(relList, excluded);
	}
	
	public List<Permission> getPermissionListByRoleList(List<Role> roleList) {
		List<Long> roleIdList = CollectionUtils.isEmpty(roleList)
				? Collections.<Long>emptyList()
				: Lists.transform(roleList, new Function<Role, Long>() {
					@Override
					public Long apply(Role role) {
						return role != null? role.getId(): null;
					}
				});
		return getPermissionListByRoleIdList(roleIdList);
	}
	
	public List<Permission> getPermissionListByRoleIdList(List<Long> roleIdList) {
		if(CollectionUtils.isEmpty(roleIdList)) {
			return Collections.emptyList();
		}
		List<RoleRelPermission> relList = roleRelPermissionDao.list(new QueryParamMap()
			.addParam("roleId__in", roleIdList)
		);
		return getPermissionListByRelList(relList, false);
	}
	
	private List<Permission> getPermissionListByRelList(List<RoleRelPermission> relList, boolean excluded) {
		if(CollectionUtils.isEmpty(relList) && !excluded) {
			return Collections.emptyList();
		}
		List<Long> permissionIdList = Lists.transform(relList, new Function<RoleRelPermission, Long>() {
			@Override
			public Long apply(RoleRelPermission rel) {
				return rel != null? rel.getPermissionId(): null;
			}
		});
		return getPermissionListByIdList(permissionIdList, excluded);
	}
	
	public List<Permission> getPermissionListByIdList(List<Long> idList) {
		return getPermissionListByIdList(idList, false);
	}
	
	public List<Permission> getPermissionListByIdList(List<Long> idList, boolean excluded) {
		QueryParamMap params = new QueryParamMap().orderByAsc("target").orderByAsc("action");
		if(CollectionUtils.isEmpty(idList)) {
			return excluded
					? permissionDao.list(params)
					: Collections.<Permission>emptyList();
		}
		return permissionDao.list(params
			.addParam(excluded? "id__not_in": "id__in", idList)
		); 
	}
	
}
