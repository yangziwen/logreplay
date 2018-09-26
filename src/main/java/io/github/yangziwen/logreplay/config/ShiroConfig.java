package io.github.yangziwen.logreplay.config;

import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.collect.ImmutableMap;

import io.github.yangziwen.logreplay.security.ShiroDbRealm;
import io.github.yangziwen.logreplay.service.PermissionService;
import io.github.yangziwen.logreplay.service.UserService;

@Configuration
public class ShiroConfig {

	@Value("${shiro.remember.me.cipher.key}")
	private String cipherKey;

	private RememberMeManager rememberMeManager() {
		SimpleCookie cookie = new SimpleCookie("rememberMe");
		cookie.setHttpOnly(true);
		cookie.setMaxAge(604800);
		CookieRememberMeManager manager = new CookieRememberMeManager();
		manager.setCookie(cookie);
		manager.setCipherKey(Base64.decode(cipherKey));
		return manager;
	}

	private CredentialsMatcher credentialsMatcher() {
		HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
		matcher.setHashAlgorithmName("MD5");
		matcher.setHashIterations(3);
		return matcher;
	}

	private EhCacheManager shiroEhcacheManager() {
		EhCacheManager manager = new EhCacheManager();
		manager.setCacheManagerConfigFile("classpath:config/ehcache-shiro.xml");
		return manager;
	}

	@Bean
	public ShiroDbRealm shiroDbRealm(
			@Autowired UserService userService,
			@Autowired PermissionService permissionService) {
		ShiroDbRealm realm = new ShiroDbRealm();
		realm.setUserService(userService);
		realm.setPermissionService(permissionService);
		realm.setCredentialsMatcher(credentialsMatcher());
		return realm;
	}

	@Bean
	public WebSecurityManager securityManager(@Autowired ShiroDbRealm shiroDbRealm) {
		DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
		manager.setRealm(shiroDbRealm);
		manager.setRememberMeManager(rememberMeManager());
		manager.setCacheManager(shiroEhcacheManager());
		return manager;
	}

	@Bean
	// declare @Bean methods as static when defining post-processor beans
	public static LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
		return new LifecycleBeanPostProcessor();
	}

	@Bean
	public ShiroFilterFactoryBean shiroFilter(
			@Autowired WebSecurityManager securityManager) {
		ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
		factoryBean.setSecurityManager(securityManager);
		factoryBean.setLoginUrl("/login.htm");
		factoryBean.setSuccessUrl("/home.htm");
		factoryBean.setUnauthorizedUrl("/unauthorized.htm");
		factoryBean.setFilterChainDefinitionMap(ImmutableMap.<String, String>builder()
				.put("/js/**", "anon")
				.put("/css/**", "anon")
				.put("/img/**", "anon")
				.put("/fonts/**", "anon")
				.put("/register**", "anon")
				.put("/register/**", "anon")
//				.put("/stomp/**", "anon")
//				.put("/socket/**", "anon")
				.put("/operationRecord/receive", "anon")
				.put("/login.htm", "authc")
				.put("/logout.htm", "logout")
				.put("/admin/**", "authc, roles[admin]")
				.put("/monitoring**", "roles[admin]")
				.put("/monitor/**", "roles[admin]")
				.put("/**", "user")
				.build());
		return factoryBean;
	}

}
