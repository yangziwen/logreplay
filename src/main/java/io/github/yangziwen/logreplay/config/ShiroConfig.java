package io.github.yangziwen.logreplay.config;

import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.boot.autoconfigure.ShiroAnnotationProcessorAutoConfiguration;
import org.apache.shiro.spring.config.web.autoconfigure.ShiroWebAutoConfiguration;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import io.github.yangziwen.logreplay.security.ShiroDbRealm;
import io.github.yangziwen.logreplay.service.PermissionService;
import io.github.yangziwen.logreplay.service.UserService;
import net.sf.ehcache.CacheManager;

@Configuration
@AutoConfigureBefore({
    ShiroWebAutoConfiguration.class,
    ShiroAnnotationProcessorAutoConfiguration.class
})
public class ShiroConfig {

    @Value("${shiro.remember.me.cipher.key}")
    private String cipherKey;

    @Bean
    public RememberMeManager rememberMeManager() {
        SimpleCookie cookie = new SimpleCookie("rememberMe");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(604800);
        CookieRememberMeManager manager = new CookieRememberMeManager();
        manager.setCookie(cookie);
        manager.setCipherKey(Base64.decode(cipherKey));
        return manager;
    }

    @Bean
    public EhCacheManager cacheManager(
            @Autowired CacheManager cacheManager) {
        EhCacheManager manager = new EhCacheManager();
        manager.setCacheManager(cacheManager);
        return manager;
    }

    private CredentialsMatcher credentialsMatcher() {
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
        matcher.setHashAlgorithmName("MD5");
        matcher.setHashIterations(3);
        return matcher;
    }

    @Bean
    public Realm realm(
            @Autowired UserService userService,
            @Autowired PermissionService permissionService) {
        ShiroDbRealm realm = new ShiroDbRealm();
        realm.setUserService(userService);
        realm.setPermissionService(permissionService);
        realm.setCredentialsMatcher(credentialsMatcher());
        return realm;
    }

    // 使shiro的注解生效，同时防止使用jdk去代理cglib的代理结果
    // 详情可见https://alanli7991.github.io/2016/10/21/切面编程三AspectJ与Shiro不兼容和Spring二次代理错误分析/
    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator creator = new DefaultAdvisorAutoProxyCreator();
        creator.setProxyTargetClass(true);
        return creator;
    }

    @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();
        chainDefinition.addPathDefinition("/js/**", "anon");
        chainDefinition.addPathDefinition("/css/**", "anon");
        chainDefinition.addPathDefinition("/img/**", "anon");
        chainDefinition.addPathDefinition("/fonts/**", "anon");
        chainDefinition.addPathDefinition("/register**", "anon");
        chainDefinition.addPathDefinition("/register/**", "anon");
//        chainDefinition.addPathDefinition("/stomp/**", "anon");
//        chainDefinition.addPathDefinition("/socket/**", "anon");
        chainDefinition.addPathDefinition("/operationRecord/receive", "anon");
        chainDefinition.addPathDefinition("/login.htm", "authc");
        chainDefinition.addPathDefinition("/logout.htm", "logout");
        chainDefinition.addPathDefinition("/admin/**", "authc, roles[admin]");
        chainDefinition.addPathDefinition("/monitoring**", "roles[admin]");
        chainDefinition.addPathDefinition("/monitor/**", "roles[admin]");
        chainDefinition.addPathDefinition("/manage/**", "roles[admin]");
        chainDefinition.addPathDefinition("/**", "user");
        return chainDefinition;
    }

}
