package com.sogou.map.logreplay.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.web.util.SavedRequest;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sogou.map.logreplay.util.AuthUtil;

/**
 * 登录相关的跳转接口
 * @author yangziwen
 */
@Controller
public class LoginController {

	@RequestMapping(value = "/login.htm", method = RequestMethod.GET)
	public String toLogin(HttpServletRequest request) {
		SavedRequest savedRequest = WebUtils.getSavedRequest(request);
		if(savedRequest != null) {
			String savedUri = savedRequest.getRequestURI();
			if(StringUtils.isBlank(savedUri) 
					|| !savedUri.endsWith(".htm") 
					|| savedUri.endsWith("404.htm")) {
				WebUtils.getAndClearSavedRequest(request);
			}
		}
		return "login";
	}
	
	/**
	 * 此接口用于处理登录错误的情形
	 * 用户名或者密码错误时，请求才会击穿shiro的过滤器链，抵达此方法
	 */
	@RequestMapping(value = "/login.htm", method = RequestMethod.POST)
	public String afterLoginSubmission(
			HttpServletRequest request,
			HttpServletResponse response
			) throws IOException {
		if(AuthUtil.isAuthenticated()) {
			org.apache.shiro.web.util.WebUtils.redirectToSavedRequest(request, response, "/home.htm");
			return null;
		} 
		if(AuthUtil.isRemembered()) {
			request.setAttribute("errorMessage", "密码错误，请重试!");
		} else {
			request.setAttribute("errorMessage", "用户名或密码错误，请重试!");
		}
		return "login";
	}
}
