package com.sogou.map.logreplay.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sogou.map.logreplay.bean.Role;
import com.sogou.map.logreplay.bean.User;
import com.sogou.map.logreplay.bean.UserRelRole;
import com.sogou.map.logreplay.bean.UserWithRoles;
import com.sogou.map.logreplay.dao.UserDao;
import com.sogou.map.logreplay.dao.UserRelRoleDao;
import com.sogou.map.logreplay.dao.UserWithRolesDao;
import com.sogou.map.logreplay.dao.base.Page;
import com.sogou.map.logreplay.dao.base.QueryParamMap;

@Service
public class UserService {
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private UserRelRoleDao userRelRoleDao;
	
	@Autowired
	private UserWithRolesDao userWithRolesDao;
	
	public User getUserById(Long id) {
		return userDao.getById(id);
	}

	public User getUserByUsername(String username) {
		return userDao.first(new QueryParamMap().addParam("username", username)); 
	}
	
	public UserWithRoles getUserWithRolesById(Long id) {
		if(id == null) {
			return null;
		}
		return userWithRolesDao.getById(id);
	}
	
	public Page<User> getUserPaginateResult(int start, int limit, Map<String, Object> param) {
		return userDao.paginate(start, limit, param);
	}
	
	public Page<UserWithRoles> getUserWithRolesPaginateResult(int start, int limit, Map<String, Object> param) {
		return userWithRolesDao.paginate(start, limit, param);
	}
	
	@Transactional
	public void createUser(User user, List<Role> roleList) {
		user.setCreateTime(new Timestamp(System.currentTimeMillis()));
		if(user.getEnabled() == null) {
			user.setEnabled(true);
		}
		userDao.save(user);
		List<UserRelRole> userRelRoleList = new ArrayList<UserRelRole>();
		for(Role role: roleList) {
			UserRelRole rel = new UserRelRole();
			rel.setUserId(user.getId());;
			rel.setRoleId(role.getId());
			userRelRoleList.add(rel);
		}
		userRelRoleDao.batchSave(userRelRoleList, 20);
	}
	
	public void updateUser(User user) {
		user.setUpdateTime(new Timestamp(System.currentTimeMillis()));
		userDao.update(user);
	}
	
	@Transactional
	public void updateUser(User user, List<Role> roleList) {
		user.setUpdateTime(new Timestamp(System.currentTimeMillis()));
		userDao.update(user);
		userRelRoleDao.deleteUserRelRolesByUserId(user.getId());
		List<UserRelRole> userRelRoleList = new ArrayList<UserRelRole>();
		for(Role role: roleList) {
			UserRelRole rel = new UserRelRole();
			rel.setUserId(user.getId());;
			rel.setRoleId(role.getId());
			userRelRoleList.add(rel);
		}
		userRelRoleDao.batchSave(userRelRoleList, 20);
	}
	
}
