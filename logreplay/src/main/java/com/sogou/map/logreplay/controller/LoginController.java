package com.sogou.map.logreplay.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.web.util.SavedRequest;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.stereotype.Component;

import com.sogou.map.logreplay.util.AuthUtil;
import com.sun.jersey.api.view.Viewable;

/**
 * 登录相关的跳转接口
 * @author yangziwen
 */
@Component
@Path("/login.htm")
public class LoginController {

	@GET
	public Viewable toLogin(@Context HttpServletRequest request) {
		SavedRequest savedRequest = WebUtils.getSavedRequest(request);
		if(savedRequest != null) {
			String savedUri = savedRequest.getRequestURI();
			if(StringUtils.isBlank(savedUri) 
					|| !savedUri.endsWith(".htm") 
					|| savedUri.endsWith("404.htm")) {
				WebUtils.getAndClearSavedRequest(request);
			}
		}
		return new Viewable("/login.jsp");
	}
	
	@POST
	public Viewable afterLoginSubmission(
			@Context HttpServletRequest request,
			@Context HttpServletResponse response
			) throws IOException {
		if(AuthUtil.isAuthenticated()) {
			org.apache.shiro.web.util.WebUtils.redirectToSavedRequest(request, response, "/home");
			return null;
		} 
		if(AuthUtil.isRemembered()) {
			request.setAttribute("errorMessage", "密码错误，请重试!");
		} else {
			request.setAttribute("errorMessage", "用户名或密码错误，请重试!");
		}
		return new Viewable("/login.jsp");
	}
}
