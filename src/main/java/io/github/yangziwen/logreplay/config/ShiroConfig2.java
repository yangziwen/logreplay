package io.github.yangziwen.logreplay.config;

import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.yangziwen.logreplay.security.ShiroDbRealm;
import io.github.yangziwen.logreplay.service.PermissionService;
import io.github.yangziwen.logreplay.service.UserService;

@Configuration
public class ShiroConfig2 {

    private CredentialsMatcher credentialsMatcher() {
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
        matcher.setHashAlgorithmName("MD5");
        matcher.setHashIterations(3);
        return matcher;
    }

    @Bean
    public Realm shiroDbRealm(
            @Autowired UserService userService,
            @Autowired PermissionService permissionService) {
        ShiroDbRealm realm = new ShiroDbRealm();
        realm.setUserService(userService);
        realm.setPermissionService(permissionService);
        realm.setCredentialsMatcher(credentialsMatcher());
        return realm;
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
