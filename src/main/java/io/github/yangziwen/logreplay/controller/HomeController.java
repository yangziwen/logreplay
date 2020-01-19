package io.github.yangziwen.logreplay.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

	/**
	 * 将根路径请求转发到/home.htm
	 */
	@RequestMapping("/")
	public String toHome() {
		return "redirect:index.html";
	}

	/**
	 * 将页面请求转发给jsp
	 */
	@RequestMapping("/**/*.htm")
	public String toJsp(HttpServletRequest request) {
		return request.getRequestURI().replace(request.getContextPath(), "")
				.replaceFirst("^/", "")
				.replaceAll("\\.htm$", "");
	}

}
