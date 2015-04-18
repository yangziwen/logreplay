package com.sogou.map.logreplay.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

import com.sogou.map.logreplay.bean.Role;
import com.sogou.map.logreplay.bean.User;
import com.sogou.map.logreplay.dao.base.QueryParamMap;
import com.sogou.map.logreplay.security.ShiroDbRealm;
import com.sogou.map.logreplay.service.RoleService;

public class AuthUtil {
	
	private AuthUtil() {}
	
	private static Map<String, Role> roleObjMap = null;
	
	public static Long getCurrentRoleId() {
		Role role = getCurrentRoleObj();
		return role != null? role.getId(): null;
	}
	
	public static Role getCurrentRoleObj() {
		return getRoleObjByName(getRoleList().get(0));
	}
	
	public static Role getRoleObjByName(String role) {
		Role roleObj = ensureRoleObjMap().get(role);
		roleObj = roleObj != null? roleObj.clone(): new Role();
		return roleObj;
	}
	
	public static List<Role> getAllRoleObjList() {
		List<Role> list = new ArrayList<Role>();
		for(Role role: ensureRoleObjMap().values()) {
			list.add(role.clone());
		}
		return list;
	}
	
	private static Map<String, Role> ensureRoleObjMap() {
		if(roleObjMap == null) {
			List<Role> list = SpringUtil.getBean(RoleService.class).getRoleListResult(QueryParamMap.emptyMap());
			Map<String, Role> map = new LinkedHashMap<String, Role>();
			for(Role role: list) {
				map.put(role.getName(), role);
			}
			roleObjMap = map;
		}
		return roleObjMap;
	}
	
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
	
	public static List<String> getRoleList() {
		PrincipalCollection principals = getCurrentSubject().getPrincipals();
		AuthorizationInfo info = SpringUtil.getBean(ShiroDbRealm.class).getAuthorizationInfo(principals);
		if(info != null) {
			return new ArrayList<String>(info.getRoles());
		}
		return Collections.emptyList();
	}
	
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
