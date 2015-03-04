package com.sogou.map.logreplay.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;

import com.sogou.map.logreplay.bean.User;
import com.sogou.map.logreplay.security.ShiroDbRealm;

public class AuthUtil {
	
	private AuthUtil() {}
	
	public static Subject getCurrentSubject() {
		return SecurityUtils.getSubject();
	}
	
	public static boolean isAuthenticated() {
		return getCurrentSubject().isAuthenticated();
	}
	
	public static boolean isRemembered() {
		return getCurrentSubject().isRemembered();
	}
	
	public static boolean isUser() {
		return isAuthenticated() || isRemembered();
	}
	
	public static boolean isGuest() {
		return !isUser();
	}
	
	public static String getUsername() {
		return (String) getCurrentSubject().getPrincipal();
	}
	
	public static User getCurrentUser() {
		return getCurrentSubject().getPrincipals().oneByType(User.class);
	}
	
	public static String getScreenName() {
		User user = getCurrentUser();
		if(user == null) {
			return "";
		}
		return StringUtils.isNotBlank(user.getScreenName())
				? user.getScreenName()
				: user.getUsername();
	}
	
	/**
	 * 需要查数据库
	 */
	public static List<String> getRoleList() {
		PrincipalCollection principals = getCurrentSubject().getPrincipals();
		AuthorizationInfo info = SpringUtil.getBean(ShiroDbRealm.class).doGetAuthorizationInfo(principals);
		if(info != null) {
			return new ArrayList<String>(info.getRoles());
		}
		return Collections.emptyList();
	}
	
	/**
	 * 需要查数据库
	 */
	public static String getRoles() {
		return StringUtils.join(getRoleList(), ",");
	}
	
	public static void login(String username, String password) {
		login(username, password, true);
	}
	
	public static void login(String username, String password, boolean rememberMe) {
		UsernamePasswordToken token = new UsernamePasswordToken(username, password);
		token.setRememberMe(rememberMe);
		getCurrentSubject().login(token);
	}
	
	public static void logout() {
		getCurrentSubject().logout();
	}

}
