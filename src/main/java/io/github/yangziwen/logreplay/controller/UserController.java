package io.github.yangziwen.logreplay.controller;

import org.apache.commons.lang.StringUtils;
import org.audit4j.core.annotation.Audit;
import org.audit4j.core.annotation.AuditField;
import org.audit4j.core.annotation.DeIdentify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.github.yangziwen.logreplay.bean.User;
import io.github.yangziwen.logreplay.controller.base.BaseController;
import io.github.yangziwen.logreplay.exception.LogReplayException;
import io.github.yangziwen.logreplay.service.UserService;
import io.github.yangziwen.logreplay.util.AuthUtil;

@Controller
@RequestMapping("/user")
public class UserController extends BaseController {

	@Autowired
	private UserService userService;

	@ResponseBody
	@RequestMapping("/detail")
	public ModelMap detail() {
		User user = userService.getUserByUsername(AuthUtil.getUsername());
		return successResult(user);
	}

	@Audit(action = "user.update_profile")
	@ResponseBody
	@RequestMapping(value = "/profile/update", method = RequestMethod.POST)
	public ModelMap updateProfile(
			@AuditField(field = "screenName")
			@RequestParam String screenName) {
		if(StringUtils.isBlank(screenName)) {
			throw LogReplayException.invalidParameterException("ScreenName should not be null!");
		}
		String username = AuthUtil.getUsername();
		try {
			User user = userService.getUserByUsername(username);
			user.setScreenName(screenName);
			userService.updateUser(user);
			return successResult("User[%s] is successfully updated", username);
		} catch (Exception e) {
			logger.error("failed to update profile of user[{}]", username, e);
			throw LogReplayException.operationFailedException("Failed to update User[%s]", username);
		}
	}

	@Audit(action = "user.update_password")
	@ResponseBody
	@RequestMapping(value = "/password/update", method = RequestMethod.POST)
	public ModelMap updatePassword(
			@DeIdentify(fromRight = 2)
			@AuditField(field = "oldPassword")
			@RequestParam String oldPassword,
			@DeIdentify(fromRight = 2)
			@AuditField(field = "newPassword")
			@RequestParam String newPassword) {
		if(StringUtils.isBlank(oldPassword) || StringUtils.isBlank(newPassword)) {
			throw LogReplayException.invalidParameterException("Neither oldPassword nor newPassword should be null!");
		}
		if((newPassword = newPassword.trim()).length() < User.PASSWORD_MIN_LENGTH) {
			throw LogReplayException.invalidParameterException("New password is too short!");
		}
		String username = AuthUtil.getUsername();
		User user = userService.getUserByUsername(username);
		if(!AuthUtil.hashPassword(username, oldPassword).equals(user.getPassword())) {
			throw LogReplayException.invalidParameterException("Parameter oldPassword is wrong!");
		}
		try {
			user.setPassword(AuthUtil.hashPassword(username, newPassword));
			userService.updateUser(user);
			return successResult("The password of User[%s] is successfully updated", username);
		} catch (Exception e) {
			logger.error("failed to update password of user[{}]", username, e);
			throw LogReplayException.operationFailedException("Failed to update the password of User[%s]", username);
		}
	}

	@ResponseBody
	@RequestMapping("/checkPassword")
	public boolean checkPassword(String password) {
		if(StringUtils.isBlank(password)) {
			return false;
		}
		User user = userService.getUserByUsername(AuthUtil.getUsername());
		if(!AuthUtil.hashPassword(user.getUsername(), password).equals(user.getPassword())) {
			return false;
		}
		return true;
	}
}
