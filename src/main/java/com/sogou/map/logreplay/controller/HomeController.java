package com.sogou.map.logreplay.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

	/**
	 * 将根路径请求转发到/home.htm
	 */
	@RequestMapping("/")
	public String toHome() {
		return "redirect:home.htm";
	}
	
}
