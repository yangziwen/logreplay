package io.github.yangziwen.logreplay.config;

import javax.servlet.DispatcherType;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.collect.ImmutableMap;

import net.bull.javamelody.MonitoringFilter;
import net.bull.javamelody.SessionListener;
import net.bull.javamelody.SpringDataSourceBeanPostProcessor;
import net.bull.javamelody.SpringDataSourceFactoryBean;

@Configuration
public class JavamelodyConfig {

	@Bean
	public FilterRegistrationBean monitorFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean(new MonitoringFilter());
        registration.setName("javamelody");
        registration.setInitParameters(ImmutableMap.<String, String>builder()
        		.put("url-exclude-pattern", "/(js|css|fonts|img)/.*")
        		.put("sampling-seconds", "10")
        		.put("sampling-included-packages", "io.github.yangziwen.logreplay")
        		.build());
        registration.setAsyncSupported(true);
        registration.setDispatcherTypes(DispatcherType.ASYNC, DispatcherType.REQUEST);
        registration.addUrlPatterns("/*");
        registration.setOrder(11);
		return registration;
	}

	@Bean
	public ServletListenerRegistrationBean<?> sessionListener() {
		return new ServletListenerRegistrationBean<SessionListener>(new SessionListener());
	}

	@Bean
	public SpringDataSourceBeanPostProcessor springDataSourceBeanPostProcessor() {
		return new SpringDataSourceBeanPostProcessor();
	}

	@Bean
	public SpringDataSourceFactoryBean wrappedDataSource() {
		SpringDataSourceFactoryBean bean = new SpringDataSourceFactoryBean();
		bean.setTargetName("dataSource");
		return bean;
	}

}
