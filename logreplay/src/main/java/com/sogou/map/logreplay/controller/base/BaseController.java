package com.sogou.map.logreplay.controller.base;

import org.springframework.ui.ModelMap;

/**
 * 在摆脱对mengine-core包的依赖后，
 * 为了与前端代码保持兼容，
 * 而统一用successResult方法输出json结果
 */
public abstract class BaseController {
	
	protected ModelMap successResult(Object response) {
		return new ModelMap("code", 0).addAttribute("response", response);
	}

}
