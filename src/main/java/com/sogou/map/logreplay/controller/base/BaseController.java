package com.sogou.map.logreplay.controller.base;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sogou.map.logreplay.exception.LogReplayException;

public abstract class BaseController {
	
	/**
	 * 在摆脱对mengine-core包的依赖后，
	 * 为了与前端代码保持兼容，
	 * 而统一用successResult方法输出json结果
	 */
	protected ModelMap successResult(String response) {
		return new ModelMap("code", 0).addAttribute("response", response);
	}
	
	protected ModelMap successResult(String response, Object... args) {
		return new ModelMap("code", 0).addAttribute("response", String.format(response, args));
	}
	
	protected ModelMap successResult(Object response) {
		return new ModelMap("code", 0).addAttribute("response", response);
	}
	
	/**
	 * 统一处理LogReplayException
	 */
	@ResponseBody
	@ExceptionHandler(LogReplayException.class)
	protected ModelMap handleLogReplayException(LogReplayException e) {
		return new ModelMap("code", e.getErrorId()).addAttribute("errorMsg", e.getErrorMsg());
	}
	
}
