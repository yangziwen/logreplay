package com.sogou.map.logreplay.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.sogou.map.logreplay.bean.Role;
import com.sogou.map.logreplay.bean.User;
import com.sogou.map.logreplay.bean.UserRelRole;
import com.sogou.map.logreplay.bean.UserWithRoles;
import com.sogou.map.logreplay.dao.base.Page;
import com.sogou.map.logreplay.dao.base.QueryParamMap;
import com.sogou.map.logreplay.mappers.UserMapper;
import com.sogou.map.logreplay.mappers.UserRelRoleMapper;
import com.sogou.map.logreplay.mappers.UserWithRolesMapper;

@Service
public class UserService {
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private UserRelRoleMapper userRelRoleMapper;
	
	@Autowired
	private UserWithRolesMapper userWithRolesMapper;
	
	public User getUserById(Long id) {
		return userMapper.getById(id);
	}

	public User getUserByUsername(String username) {
		return userMapper.getByUsername(username); 
	}
	
	public UserWithRoles getUserWithRolesById(Long id) {
		if(id == null) {
			return null;
		}
		return userWithRolesMapper.getById(id);
	}
	
	public Page<User> getUserPaginateResult(int start, int limit, Map<String, Object> params) {
		int count = userMapper.count(params);
		List<User> list = userMapper.list(params, new RowBounds(start, limit));
		return new Page<User>(start, limit, count, list);
	}
	
	public Page<UserWithRoles> getUserWithRolesPaginateResult(int start, int limit, Map<String, Object> params) {
		int count = userWithRolesMapper.count(params);
		List<UserWithRoles> list = userWithRolesMapper.list(params, new RowBounds(start, limit));
		return new Page<UserWithRoles>(start, limit, count, list);
	}
	
	/**
	 * 根据username或screenName来查找用户
	 */
	public List<User> getUserListResultByName(String name) {
		return userMapper.list(new QueryParamMap()
			.or(new QueryParamMap()
				.addParam("username__contain", name)
				.addParam("screenName__contain", name)
			)
		);
	}
	
	public List<Long> getUserIdListResultByName(String name) {
		if(StringUtils.isBlank(name)) {
			return Collections.emptyList();
		}
		return Lists.transform(getUserListResultByName(name), new Function<User, Long>() {
			@Override
			public Long apply(User user) {
				return user.getId();
			}
		});
	}
	
	@Transactional
	public void createUser(User user, List<Role> roleList) {
		user.setCreateTime(new Timestamp(System.currentTimeMillis()));
		if(user.getEnabled() == null) {
			user.setEnabled(true);
		}
		userMapper.save(user);
		List<UserRelRole> userRelRoleList = new ArrayList<UserRelRole>();
		for(Role role: roleList) {
			UserRelRole rel = new UserRelRole();
			rel.setUserId(user.getId());;
			rel.setRoleId(role.getId());
			userRelRoleList.add(rel);
		}
		userRelRoleMapper.batchSave(userRelRoleList);
	}
	
	public void updateUser(User user) {
		user.setUpdateTime(new Timestamp(System.currentTimeMillis()));
		userMapper.update(user);
	}
	
	@Transactional
	public void updateUser(User user, List<Role> roleList) {
		user.setUpdateTime(new Timestamp(System.currentTimeMillis()));
		userMapper.update(user);
		userRelRoleMapper.deleteUserRelRolesByUserId(user.getId());
		List<UserRelRole> userRelRoleList = new ArrayList<UserRelRole>();
		for(Role role: roleList) {
			UserRelRole rel = new UserRelRole(user.getId(), role.getId());
			userRelRoleList.add(rel);
		}
		userRelRoleMapper.batchSave(userRelRoleList);
	}
	
}
