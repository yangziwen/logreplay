package com.sogou.map.logreplay.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sogou.map.logreplay.util.ProductUtil;

public class ProductFilter extends OncePerRequestFilter {
	
	private static final Long DEFAULT_PRODUCT_ID = 1L;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
			throws ServletException, IOException {
		String uri = request.getRequestURI();
		if(uri.contains(".")) {
			filterChain.doFilter(request, response);
			return;
		}
		Cookie[] cookies = request.getCookies();
		if(ArrayUtils.isEmpty(cookies)) {
			ProductUtil.setProductId(DEFAULT_PRODUCT_ID);
			filterChain.doFilter(request, response);
			return;
		}
		for(Cookie cookie: cookies) {
			if(ProductUtil.COOKIE_KEY.equals(cookie.getName())) {
				ProductUtil.setProductId(NumberUtils.toLong(cookie.getValue(), DEFAULT_PRODUCT_ID));
				filterChain.doFilter(request, response);
				return;
			}
		}
		ProductUtil.setProductId(DEFAULT_PRODUCT_ID);
		filterChain.doFilter(request, response);
	}

}
