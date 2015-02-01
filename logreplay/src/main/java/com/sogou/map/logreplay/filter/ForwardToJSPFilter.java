package com.sogou.map.logreplay.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class ForwardToJSPFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String uri = request.getRequestURI();
		if(StringUtils.isBlank(uri)) {
			request.getRequestDispatcher("/WEB-INF/view/home.jsp").forward(request, response);
			return;
		}
		if(!uri.endsWith(".htm")) {
			filterChain.doFilter(request, response);
			return;
		}
		// login.htm和logout.htm委托给shiro管理，不适用这种默认的forward方式
		if(uri.endsWith("login.htm") || uri.endsWith("logout.htm")) {
			filterChain.doFilter(request, response);
			return;
		}
		String contextPath = request.getContextPath();
		if(StringUtils.isNotBlank(contextPath) && !"/".equals(contextPath)) {
			uri = uri.replaceFirst(contextPath, "");
		}
		String forwardPath = "/WEB-INF/view" + uri.replaceFirst("\\.htm$", ".jsp");
		request.getRequestDispatcher(forwardPath).forward(request, response);
	}

}