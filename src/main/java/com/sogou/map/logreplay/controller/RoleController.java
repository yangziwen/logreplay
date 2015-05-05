package com.sogou.map.logreplay.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sogou.map.logreplay.bean.Role;
import com.sogou.map.logreplay.controller.base.BaseController;
import com.sogou.map.logreplay.dao.base.QueryParamMap;
import com.sogou.map.logreplay.service.RoleService;

@Controller
@RequestMapping("/role")
public class RoleController extends BaseController {
	
	@Autowired
	private RoleService roleService;

	@ResponseBody
	@RequestMapping("/list")
	public ModelMap list() {
		List<Role> list = roleService.getRoleListResult(new QueryParamMap().orderByAsc("id"));
		return successResult(list);
	}
}
