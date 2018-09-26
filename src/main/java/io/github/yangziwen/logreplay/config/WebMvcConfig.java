package io.github.yangziwen.logreplay.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

	@Bean
	public InternalResourceViewResolver viewResolver() {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setPrefix("/WEB-INF/view/");
		resolver.setSuffix(".jsp");
		resolver.setViewClass(JstlView.class);
		return resolver;
	}

	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {
		configurer.setUseSuffixPatternMatch(false);
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		int cachePeriod = 28800;
		registry.addResourceHandler("/js/**").addResourceLocations("/static/js/").setCachePeriod(cachePeriod);
		registry.addResourceHandler("/css/**").addResourceLocations("/static/css/").setCachePeriod(cachePeriod);
		registry.addResourceHandler("/img/**").addResourceLocations("/static/img/").setCachePeriod(cachePeriod);
		registry.addResourceHandler("/fonts/**").addResourceLocations("/static/fonts").setCachePeriod(cachePeriod);
	}

	@Bean
	public CommonsMultipartResolver multipartResolver() {
		return new CommonsMultipartResolver();
	}

}
