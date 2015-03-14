package com.sogou.map.logreplay.controller;

import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sogou.map.logreplay.bean.Role;
import com.sogou.map.logreplay.bean.User;
import com.sogou.map.logreplay.bean.UserWithRoles;
import com.sogou.map.logreplay.dao.base.Page;
import com.sogou.map.logreplay.dao.base.QueryParamMap;
import com.sogou.map.logreplay.exception.LogReplayException;
import com.sogou.map.logreplay.service.RoleService;
import com.sogou.map.logreplay.service.UserService;
import com.sogou.map.logreplay.util.AuthUtil;
import com.sogou.map.logreplay.util.JsonUtil;
import com.sogou.map.mengine.common.service.BaseService;

@Component
@Path("/admin/user")
public class UserAdminController extends BaseService {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private RoleService roleService;
	
	@GET
	@Path("/list")
	public Response list(
			@DefaultValue(Page.DEFAULT_START) @QueryParam("start") int start,
			@DefaultValue(Page.DEFAULT_LIMIT) @QueryParam("limit") int limit,
			@QueryParam("username") String username,
			@QueryParam("screenName") String screenName,
			@QueryParam("roleNames") String roleNames,
			@QueryParam("enabled") String enabled) {
		Page<UserWithRoles> page = userService.getUserWithRolesPaginateResult(start, limit, new QueryParamMap()
			.addParam(StringUtils.isNotBlank(username), "username__start_with", username)
			.addParam(StringUtils.isNotBlank(screenName), "screenName__contain", screenName)
			.addParam(StringUtils.isNotBlank(roleNames), "role.name__in", roleNames.split(","))
			.addParam(StringUtils.isNotBlank(enabled), "user.enabled", BooleanUtils.toBoolean(enabled))
			.orderByAsc("user.username")
		);
		return successResultToJson(page, JsonUtil.configInstance(), true);
	}
	
	@GET
	@Path("/detail/{id}")
	public Response detail(@PathParam("id") Long id) {
		UserWithRoles user = userService.getUserWithRolesById(id);
		return successResultToJson(user, JsonUtil.configInstance(), true);
	}
	
	@POST
	@Path("/create")
	public Response create (
			@FormParam("username") String username,
			@FormParam("screenName") String screenName,
			@FormParam("roleNames") String roleNames,
			@FormParam("enabled") Boolean enabled) {
		if(StringUtils.isBlank(username) || StringUtils.isBlank(roleNames)) {
			throw LogReplayException.invalidParameterException("Either Username or roleNames should not be null!");
		}
		List<Role> roleList = roleService.getRoleListResult(new QueryParamMap().addParam("name__in", roleNames.split(",")));
		if(CollectionUtils.isEmpty(roleList)) {
			throw LogReplayException.invalidParameterException("Invalid roleNames!");
		}
		if(userService.getUserByUsername(username) != null) {
			throw LogReplayException.invalidParameterException("Duplicated username!");
		}
		try {
			// ≥ı º√‹¬Î∂º «1234
			User user = new User(username, screenName, AuthUtil.hashPassword(username, "1234"), enabled); 
			userService.createUser(user, roleList);
			return successResultToJson(String.format("User[%d] is created successfully!", user.getId()), true);
		} catch (Exception e) {
			throw LogReplayException.operationFailedException("Failed to create user!");
		}
	}
	
	@POST
	@Path("/update/{id}")
	public Response update (
			@PathParam("id") Long id,
			@FormParam("screenName") String screenName,
			@FormParam("roleNames") String roleNames,
			@FormParam("enabled") Boolean enabled
			) {
		if(id == null) {
			throw LogReplayException.invalidParameterException("Id of user should not be null!");
		}
		List<Role> roleList = roleService.getRoleListResult(new QueryParamMap().addParam("name__in", roleNames.split(",")));
		if(CollectionUtils.isEmpty(roleList)) {
			throw LogReplayException.invalidParameterException("Invalid roleNames!");
		}
		User user = userService.getUserById(id);
		if(user == null) {
			throw LogReplayException.notExistException(String.format("User[%d] does not exist!", id));
		}
		if(StringUtils.isNotBlank(screenName)) {
			user.setScreenName(screenName);
		}
		if(enabled != null) {
			user.setEnabled(enabled);
		}
		try {
			userService.updateUser(user, roleList);
			return successResultToJson(String.format("User[%d] is updated successfully!", user.getId()), true);
		} catch (Exception e) {
			throw LogReplayException.operationFailedException(String.format("Failed to update user[%d]!", id));
		}
	}
	
	@POST
	@Path("/password/update/{id}")
	public Response updatePassword(
			@PathParam("id") Long id,
			@FormParam("password") String password
			) {
		User user = null;
		if(StringUtils.isBlank(password) || password.trim().length() < 4) {
			throw LogReplayException.invalidParameterException(String.format("Invalid password[%s]!", password));
		}
		if(id == null || (user = userService.getUserById(id)) == null) {
			throw LogReplayException.invalidParameterException(String.format("Invalid userId[%s]!", id));
		}
		try {
			user.setPassword(AuthUtil.hashPassword(user.getUsername(), password));
			userService.updateUser(user);
			return successResultToJson(String.format("Password of user[%d] is updated successfully!", user.getId()), true);
		} catch (Exception e) {
			throw LogReplayException.operationFailedException(String.format("Failed to update password of user[%d]!", id));
		}
	}
	
	@GET
	@Path("/checkDuplication")
	public Response checkDuplication(
			@QueryParam("id") Long id,
			@QueryParam("username") String username) {
		if(id == null && userService.getUserByUsername(username) != null) {
			return Response.ok().entity("false").build();
		}
		return Response.ok().entity("true").build();
	}

}
