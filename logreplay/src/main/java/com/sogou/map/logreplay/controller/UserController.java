package com.sogou.map.logreplay.controller;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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
		try {
			User user = userService.getUserByUsername(AuthUtil.getUsername());
			user.setScreenName(screenName);
			userService.updateUser(user);
			return successResultToJson(String.format("User[%s] is successfully updated", AuthUtil.getUsername()), true);
		} catch (Exception e) {
			e.printStackTrace();
			throw LogReplayException.operationFailedException(String.format("Failed to update User[%s]", AuthUtil.getUsername()));
		}
	}
}
