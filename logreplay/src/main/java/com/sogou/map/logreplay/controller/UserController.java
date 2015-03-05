package com.sogou.map.logreplay.controller;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sogou.map.logreplay.bean.User;
import com.sogou.map.logreplay.exception.LogReplayException;
import com.sogou.map.logreplay.service.UserService;
import com.sogou.map.logreplay.util.AuthUtil;
import com.sogou.map.logreplay.util.JsonUtil;
import com.sogou.map.mengine.common.service.BaseService;

@Component
@Path("/user")
public class UserController extends BaseService {

	@Autowired
	private UserService userService;
	
	@GET
	@Path("/detail")
	public Response detail() {
		User user = userService.getUserByUsername(AuthUtil.getUsername());
		return successResultToJson(user, JsonUtil.configInstance(), true);
	}
	
	@POST
	@Path("/profile/update")
	public Response updateProfile(
			@FormParam("screenName") String screenName) { 
		if(StringUtils.isBlank(screenName)) {
			throw LogReplayException.invalidParameterException("ScreenName should not be null!");
		}
		String username = AuthUtil.getUsername();
		try {
			User user = userService.getUserByUsername(username);
			user.setScreenName(screenName);
			userService.updateUser(user);
			return successResultToJson(String.format("User[%s] is successfully updated", username), true);
		} catch (Exception e) {
			e.printStackTrace();
			throw LogReplayException.operationFailedException(String.format("Failed to update User[%s]", username));
		}
	}
	
	@POST
	@Path("/password/update")
	public Response updatePassword(
			@FormParam("oldPassword") String oldPassword,
			@FormParam("newPassword") String newPassword) {
		if(StringUtils.isBlank(oldPassword) || StringUtils.isBlank(newPassword)) {
			throw LogReplayException.invalidParameterException("Neither oldPassword nor newPassword should be null!");
		}
		String username = AuthUtil.getUsername();
		User user = userService.getUserByUsername(username);
		if(!oldPassword.equals(user.getPassword())) {
			throw LogReplayException.invalidParameterException("Parameter oldPassword is wrong!");
		}
		try {
			user.setPassword(newPassword);
			userService.updateUser(user);
			return successResultToJson(String.format("The password of User[%s] is successfully updated", username), true);
		} catch (Exception e) {
			e.printStackTrace();
			throw LogReplayException.operationFailedException(String.format("Failed to update the password of User[%s]", username));
		}
	}
	
	@GET
	@Path("/checkPassword")
	public Response checkPassword(@QueryParam("password") String password) {
		if(StringUtils.isBlank(password)) {
			return Response.ok().entity("false").build();
		}
		User user = userService.getUserByUsername(AuthUtil.getUsername());
		if(!password.equals(user.getPassword())) {	// TODO
			return Response.ok().entity("false").build();
		}
		return Response.ok().entity("true").build();
	}
}
