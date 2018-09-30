package io.github.yangziwen.logreplay.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import org.apache.catalina.core.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.ServletContextInitializer;
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

	private static final Logger logger = LoggerFactory.getLogger(WebMvcConfig.class);

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
		registry.addResourceHandler("/fonts/**").addResourceLocations("/static/fonts/").setCachePeriod(cachePeriod);
	}

	@Bean
	public CommonsMultipartResolver multipartResolver() {
		return new CommonsMultipartResolver();
	}


	@Bean
	public ServletContextInitializer preCompileJspsAtStartup() {
		JspVisitor visitor = new JspVisitor();
		return context -> {
			new JspTree("/WEB-INF/view/", context).accept(visitor);
		};
	}

	private static class JspTree {

		private ServletContext context;
		private String path;

		public JspTree(String path, ServletContext context) {
			this.path = path;
			this.context = context;
		}

		public void accept(JspVisitor visitor) {
			if (path.endsWith("/")) {
				context.getResourcePaths(path).forEach(path -> new JspTree(path, context).accept(visitor));
				return;
			}
			visitor.visit(path, context);
		}
	}

	private static class JspVisitor {

		public void visit(String jspPath, ServletContext context) {
			ServletRegistration.Dynamic registration = context.addServlet(jspPath, Constants.JSP_SERVLET_CLASS);
			registration.setInitParameter("jspFile", jspPath);
			registration.setLoadOnStartup(99);
			registration.addMapping(jspPath);
			registration.setInitParameter("genStringAsCharArray", "true");
			registration.setInitParameter("trimSpaces", "true");
			registration.setInitParameter("development", "false");
			logger.info("jsp[{}] is compiled", jspPath);
		}

	}

}
