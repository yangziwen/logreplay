package com.sogou.map.logreplay.util;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringUtil implements ApplicationContextAware {

	private static ApplicationContext context;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		context = applicationContext;
	}
	
	public static ApplicationContext getApplicationContext() {
		return context;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String beanName) {
		return (T) context.getBean(beanName);
	}
	
	public static <T> T getBean(Class<T> clazz) {
		Map<?,T> beanMap = context.getBeansOfType(clazz);
		if(beanMap == null || beanMap.size() == 0) {
			return null;
		}
		return beanMap.values().iterator().next();
	}
	
}
