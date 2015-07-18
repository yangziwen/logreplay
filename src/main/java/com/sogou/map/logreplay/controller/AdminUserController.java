package com.sogou.map.logreplay.controller;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sogou.map.logreplay.bean.Role;
import com.sogou.map.logreplay.bean.User;
import com.sogou.map.logreplay.bean.UserWithRoles;
import com.sogou.map.logreplay.controller.base.BaseController;
import com.sogou.map.logreplay.dao.base.Page;
import com.sogou.map.logreplay.dao.base.QueryParamMap;
import com.sogou.map.logreplay.exception.LogReplayException;
import com.sogou.map.logreplay.service.RoleService;
import com.sogou.map.logreplay.service.UserService;
import com.sogou.map.logreplay.util.AuthUtil;

@Controller
@RequestMapping("/admin/user")
public class AdminUserController extends BaseController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private RoleService roleService;
	
	@ResponseBody
	@RequestMapping("/list")
	public ModelMap list(
			@RequestParam(defaultValue = Page.DEFAULT_START) int start,
			@RequestParam(defaultValue = Page.DEFAULT_LIMIT) int limit,
			String username,
			String screenName,
			String roleNames,
			Boolean enabled) {
		Page<UserWithRoles> page = userService.getUserWithRolesPaginateResult(start, limit, new QueryParamMap()
			.addParam(StringUtils.isNotBlank(username), "username__start_with", username)
			.addParam(StringUtils.isNotBlank(screenName), "screenName__contain", screenName)
			.addParam(StringUtils.isNotBlank(roleNames), "role.name__in", roleNames.split(","))
			.addParam(enabled != null, "user.enabled", enabled)
			.orderByAsc("user.username")
		);
		return successResult(page);
	}
	
	@ResponseBody
	@RequestMapping("/detail/{id}")
	public ModelMap detail(@PathVariable("id") Long id) {
		UserWithRoles user = userService.getUserWithRolesById(id);
		return successResult(user);
	}
	
	@ResponseBody
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ModelMap create (
			@RequestParam String username,
			@RequestParam String password,
			@RequestParam String screenName,
			@RequestParam String roleNames,
			@RequestParam Boolean enabled) {
		if(StringUtils.isBlank(username) || StringUtils.isBlank(roleNames)) {
			throw LogReplayException.invalidParameterException("Either Username or roleNames should not be null!");
		}
		if(StringUtils.isBlank(password) || (password = password.trim()).length() < User.PASSWORD_MIN_LENGTH) {
			throw LogReplayException.invalidParameterException("Password is not valid!");
		}
		List<Role> roleList = roleService.getRoleListResult(new QueryParamMap().addParam("name__in", roleNames.split(",")));
		if(CollectionUtils.isEmpty(roleList)) {
			throw LogReplayException.invalidParameterException("Invalid roleNames!");
		}
		if(userService.getUserByUsername(username) != null) {
			throw LogReplayException.invalidParameterException("Duplicated username!");
		}
		try {
			User user = new User(username, screenName, AuthUtil.hashPassword(username, password), enabled); 
			userService.createUser(user, roleList);
			return successResult("User[%d] is created successfully!", user.getId());
		} catch (Exception e) {
			throw LogReplayException.operationFailedException("Failed to create user!");
		}
	}
	
	@ResponseBody
	@RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
	public ModelMap update (
			@PathVariable("id") Long id,
			@RequestParam String screenName,
			@RequestParam String roleNames,
			@RequestParam Boolean enabled) {
		if(id == null) {
			throw LogReplayException.invalidParameterException("Id of user should not be null!");
		}
		List<Role> roleList = roleService.getRoleListResult(new QueryParamMap().addParam("name__in", roleNames.split(",")));
		if(CollectionUtils.isEmpty(roleList)) {
			throw LogReplayException.invalidParameterException("Invalid roleNames!");
		}
		User user = userService.getUserById(id);
		if(user == null) {
			throw LogReplayException.notExistException("User[%d] does not exist!", id);
		}
		if(StringUtils.isNotBlank(screenName)) {
			user.setScreenName(screenName);
		}
		if(enabled != null) {
			user.setEnabled(enabled);
		}
		try {
			userService.updateUser(user, roleList);
			return successResult("User[%d] is updated successfully!", user.getId());
		} catch (Exception e) {
			throw LogReplayException.operationFailedException("Failed to update user[%d]!", id);
		}
	}
	
	/**
	 * 管理员重置密码
	 */
	@ResponseBody
	@RequestMapping(value = "/password/update/{id}", method = RequestMethod.POST)
	public ModelMap updatePassword(
			@PathVariable("id") Long id,
			@RequestParam String password) {
		User user = null;
		if(StringUtils.isBlank(password) || (password = password.trim()).length() < User.PASSWORD_MIN_LENGTH) {
			throw LogReplayException.invalidParameterException("Invalid password[%s]!", password);
		}
		if(id == null || (user = userService.getUserById(id)) == null) {
			throw LogReplayException.invalidParameterException(String.format("Invalid userId[%s]!", id));
		}
		try {
			user.setPassword(AuthUtil.hashPassword(user.getUsername(), password));
			userService.updateUser(user);
			return successResult("Password of user[%d] is updated successfully!", user.getId());
		} catch (Exception e) {
			throw LogReplayException.operationFailedException("Failed to update password of user[%d]!", id);
		}
	}
	
	@ResponseBody
	@RequestMapping("/checkDuplication")
	public boolean checkDuplication(Long id, String username) {
		if(id == null && userService.getUserByUsername(username) != null) {
			return false;
		}
		return true;
	}

}
