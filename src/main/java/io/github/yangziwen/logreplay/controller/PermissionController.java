package io.github.yangziwen.logreplay.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.github.yangziwen.logreplay.bean.Permission;
import io.github.yangziwen.logreplay.controller.base.BaseController;
import io.github.yangziwen.logreplay.service.PermissionService;

@Controller
@RequestMapping("/permission")
public class PermissionController extends BaseController {
	
	@Autowired
	private PermissionService permissionService;

	@ResponseBody
	@RequestMapping("/list")
	public ModelMap list(Long roleId,
			@RequestParam(defaultValue = "false") boolean excluded) {
		List<Permission> list = permissionService.getPermissionListByRoleId(roleId, excluded);
		return successResult(list);
	}
	
}
