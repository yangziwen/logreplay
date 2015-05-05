package com.sogou.map.logreplay.filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.ui.ModelMap;

import com.alibaba.fastjson.JSON;
import com.sogou.map.logreplay.exception.LogReplayException;

public class LogReplayExceptionFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}
	
	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		try {
			chain.doFilter(request, response);
		} catch (LogReplayException e) {
			outputExceptionJson(response, e);
		} catch (Exception e) {
			e.printStackTrace();
			outputExceptionJson(response, e);
		}
	}
	
	private void outputExceptionJson(ServletResponse response, Exception exp) throws IOException {
		int errorId = LogReplayException.UNEXPECTED_ERROR_ID;
		String errorMsg = "";
		if(exp instanceof LogReplayException) {
			LogReplayException logReplayExp = (LogReplayException) exp;
			errorId = logReplayExp.getErrorId();
			errorMsg = logReplayExp.getErrorMsg();
		}
		String result = JSON.toJSONString(new ModelMap("code", errorId)
			.addAttribute("errorMsg", errorMsg)
		).toString();
		outputJsonResponse(response, result);
	}
	
	private void outputJsonResponse(ServletResponse response, String content) throws IOException {
		response.setContentType("application/json;charset=GBK");
		PrintWriter writer = response.getWriter();
		writer.write(content);
		writer.flush();
		writer.close();
	}


}
