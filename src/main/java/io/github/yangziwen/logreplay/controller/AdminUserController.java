package io.github.yangziwen.logreplay.controller;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.audit4j.core.annotation.Audit;
import org.audit4j.core.annotation.AuditField;
import org.audit4j.core.annotation.DeIdentify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.yangziwen.logreplay.bean.Role;
import io.github.yangziwen.logreplay.bean.User;
import io.github.yangziwen.logreplay.bean.UserWithRoles;
import io.github.yangziwen.logreplay.controller.base.BaseController;
import io.github.yangziwen.logreplay.dao.base.Page;
import io.github.yangziwen.logreplay.dao.base.QueryParamMap;
import io.github.yangziwen.logreplay.exception.LogReplayException;
import io.github.yangziwen.logreplay.service.RoleService;
import io.github.yangziwen.logreplay.service.UserService;
import io.github.yangziwen.logreplay.util.AuthUtil;

@RestController
@RequestMapping("/admin/user")
public class AdminUserController extends BaseController {

	@Autowired
	private UserService userService;

	@Autowired
	private RoleService roleService;

	@GetMapping("/list")
	@RequiresPermissions("user:view")
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

	@GetMapping("/detail/{id}")
	@RequiresPermissions("user:view")
	public ModelMap detail(@PathVariable("id") Long id) {
		UserWithRoles user = userService.getUserWithRolesById(id);
		return successResult(user);
	}

	@Audit(action = "admin.create_user")
	@PostMapping("/create")
	@RequiresPermissions("user:modify")
	public ModelMap create (
			@AuditField(field = "username")
			@RequestParam String username,
			@DeIdentify(fromRight = 2)
			@AuditField(field = "password")
			@RequestParam String password,
			@AuditField(field = "screenName")
			@RequestParam String screenName,
			@AuditField(field = "roleNames")
			@RequestParam String roleNames,
			@AuditField(field = "enabled")
			@RequestParam Boolean enabled) {
		if (StringUtils.isBlank(username) || StringUtils.isBlank(roleNames)) {
			throw LogReplayException.invalidParameterException("Either Username or roleNames should not be null!");
		}
		if (StringUtils.isBlank(password) || (password = password.trim()).length() < User.PASSWORD_MIN_LENGTH) {
			throw LogReplayException.invalidParameterException("Password is not valid!");
		}
		List<Role> roleList = roleService.getRoleListResult(new QueryParamMap().addParam("name__in", roleNames.split(",")));
		if (CollectionUtils.isEmpty(roleList)) {
			throw LogReplayException.invalidParameterException("Invalid roleNames!");
		}
		if (userService.getUserByUsername(username) != null) {
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

	@Audit(action = "admin.update_user")
	@PostMapping("/update/{id}")
	@RequiresPermissions("user:modify")
	public ModelMap update (
			@AuditField(field = "id")
			@PathVariable("id") Long id,
			@AuditField(field = "screenName")
			@RequestParam String screenName,
			@AuditField(field = "roleNames")
			@RequestParam String roleNames,
			@AuditField(field = "enabled")
			@RequestParam Boolean enabled) {
		if (id == null) {
			throw LogReplayException.invalidParameterException("Id of user should not be null!");
		}
		List<Role> roleList = roleService.getRoleListResult(new QueryParamMap().addParam("name__in", roleNames.split(",")));
		if (CollectionUtils.isEmpty(roleList)) {
			throw LogReplayException.invalidParameterException("Invalid roleNames!");
		}
		User user = userService.getUserById(id);
		if (user == null) {
			throw LogReplayException.notExistException("User[%d] does not exist!", id);
		}
		if (StringUtils.isNotBlank(screenName)) {
			user.setScreenName(screenName);
		}
		if (enabled != null) {
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
	@Audit(action = "admin.reset_password")
	@PostMapping("/password/update/{id}")
	@RequiresPermissions("user:modify")
	public ModelMap updatePassword(
			@AuditField(field = "id")
			@PathVariable("id") Long id,
			@DeIdentify(fromRight = 2)
			@AuditField(field = "password")
			@RequestParam String password) {
		User user = null;
		if (StringUtils.isBlank(password) || (password = password.trim()).length() < User.PASSWORD_MIN_LENGTH) {
			throw LogReplayException.invalidParameterException("Invalid password[%s]!", password);
		}
		if (id == null || (user = userService.getUserById(id)) == null) {
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

	@GetMapping("/checkDuplication")
	public boolean checkDuplication(Long id, String username) {
		if (id == null && userService.getUserByUsername(username) != null) {
			return false;
		}
		return true;
	}

}
