package io.github.yangziwen.logreplay.security;

import java.util.List;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.util.SimpleByteSource;

import io.github.yangziwen.logreplay.bean.Permission;
import io.github.yangziwen.logreplay.bean.Role;
import io.github.yangziwen.logreplay.bean.User;
import io.github.yangziwen.logreplay.bean.UserWithRoles;
import io.github.yangziwen.logreplay.service.PermissionService;
import io.github.yangziwen.logreplay.service.UserService;

public class ShiroDbRealm extends AuthorizingRealm {
	
	private UserService userService;
	
	private PermissionService permissionService;
	
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}
	
	/* spring可通过父类的setCredentialsMatcher注入credentialsMatcher对象 */
	
	/**
	 * 认证回调函数,登录时调用.
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
		UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
		User user = userService.getUserByUsername(token.getUsername());
		if (user == null || !Boolean.TRUE.equals(user.getEnabled())) {
			return null;
		}
		SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user.getUsername(), user.getPassword(), getName());
		info.setCredentialsSalt(new SimpleByteSource(user.getUsername()));
		SimplePrincipalCollection principals = (SimplePrincipalCollection) info.getPrincipals();
		principals.add(user, getName());
		return info;
	}

	/**
	 * 授权查询回调函数, 进行鉴权但缓存中无用户授权信息时调用.
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		User user = principals.oneByType(User.class);
		if(user != null) {
			UserWithRoles userWithRoles = userService.getUserWithRolesById(user.getId());
			for(Role role: userWithRoles.getRoles()) {
				info.addRole(role.getName());
				if(Role.ADMIN.equals(role.getName())) {
					info.addStringPermission("*:*");
				}
			}
			List<Permission> permissionList = permissionService.getPermissionListByRoleList(userWithRoles.getRoles());
			for(Permission permission: permissionList) {
				info.addStringPermission(permission.toString());
			}
		}
		return info;
	}
	
	/**
	 * 将此方法的可见性提升为public
	 * 此方法在大多数情况下可以通过shiro的缓存来获取角色信息
	 * 从而减少对数据库的请求
	 */
	@Override
	public AuthorizationInfo getAuthorizationInfo(PrincipalCollection principals) {
		return super.getAuthorizationInfo(principals);
	}

}
