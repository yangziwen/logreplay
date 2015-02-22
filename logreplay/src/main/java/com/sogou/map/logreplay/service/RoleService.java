package com.sogou.map.logreplay.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sogou.map.logreplay.bean.Role;
import com.sogou.map.logreplay.dao.RoleDao;
import com.sogou.map.logreplay.dao.UserRelRoleDao;

@Service
public class RoleService {

	@Autowired
	private RoleDao roleDao;
	
	@Autowired
	private UserRelRoleDao userRelRoleDao;
	
	public List<Role> getRoleListResult(Map<String, Object> param) {
		return roleDao.list(param);
	}
	
}
