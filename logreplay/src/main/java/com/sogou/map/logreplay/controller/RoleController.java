package com.sogou.map.logreplay.controller;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sogou.map.logreplay.bean.Role;
import com.sogou.map.logreplay.controller.base.BaseController;
import com.sogou.map.logreplay.dao.base.QueryParamMap;
import com.sogou.map.logreplay.service.RoleService;
import com.sogou.map.logreplay.util.JsonUtil;

@Component
@Path("/role")
public class RoleController extends BaseController {
	
	@Autowired
	private RoleService roleService;

	@GET
	@Path("/list")
	public Response list() {
		List<Role> list = roleService.getRoleListResult(new QueryParamMap().orderByAsc("id"));
		return successResultToJson(list, JsonUtil.configInstance(), true);
	}
}
