package com.sogou.map.logreplay.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.crypto.hash.DefaultHashService;
import org.apache.shiro.crypto.hash.HashRequest;
import org.apache.shiro.crypto.hash.HashService;
import org.apache.shiro.crypto.hash.SimpleHashRequest;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.SimpleByteSource;

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
	
	public static boolean hasRole(String role) {
		return getCurrentSubject().hasRole(role);
	}
	
	public static boolean hasAnyRoles(String... roles) {
		Subject subject = getCurrentSubject();
		for(String role: roles) {
			if(subject.hasRole(role)) {
				return true;
			}
		}
		return false;
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
	
	public static String hashPassword(String username, String password) {
		if(StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
			throw new IllegalArgumentException("Neither username nor password should be null!");
		}
		HashService hashService = new DefaultHashService();
		HashRequest hashRequest = new SimpleHashRequest("MD5", new SimpleByteSource(password), new SimpleByteSource(username), 3);
		return hashService.computeHash(hashRequest).toHex();
	}

}
