package com.sogou.map.logreplay.security;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;

import com.sogou.map.logreplay.bean.User;
import com.sogou.map.logreplay.service.UserService;

public class ShiroDbRealm extends AuthorizingRealm {
	
	private UserService userService;
	
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	/**
	 * 认证回调函数,登录时调用.
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
		UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
		User user = userService.getUserByUsername(token.getUsername());
		if (user == null) {
			return null;
		}
		SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user.getUsername(), user.getPassword(), getName());
		SimplePrincipalCollection principals = (SimplePrincipalCollection) info.getPrincipals();
		principals.add(user, getName());
		return info;
	}

	/**
	 * 授权查询回调函数, 进行鉴权但缓存中无用户授权信息时调用.
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		return null;
	}

}
