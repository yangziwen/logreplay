package io.github.yangziwen.logreplay.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import io.github.yangziwen.logreplay.bean.Permission;
import io.github.yangziwen.logreplay.bean.Role;
import io.github.yangziwen.logreplay.bean.Permission.Target;
import io.github.yangziwen.logreplay.controller.base.BaseController;
import io.github.yangziwen.logreplay.dao.base.QueryParamMap;
import io.github.yangziwen.logreplay.exception.LogReplayException;
import io.github.yangziwen.logreplay.service.PermissionService;
import io.github.yangziwen.logreplay.service.RoleService;
import io.github.yangziwen.logreplay.util.AuthUtil;

@Controller
@RequestMapping("/role")
public class RoleController extends BaseController {
	
	@Autowired
	private RoleService roleService;
	
	@Autowired
	private PermissionService permissionService;

	@ResponseBody
	@RequestMapping("/list")
	public ModelMap list() {
		List<Role> list = roleService.getRoleListResult(new QueryParamMap().orderByAsc("id"));
		return successResult(list);
	}
	
	@ResponseBody
	@RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
	public ModelMap update(
			@PathVariable(value = "id") Long id,
			@RequestParam String displayName, 
			@RequestParam String comment) {
		if(!AuthUtil.isPermitted(Target.Role.modify())) {
			throw LogReplayException.unauthorizedException("Current user is not permitted to update role");
		}
		Role role = roleService.getRoleById(id);
		if(role == null) {
			throw LogReplayException.notExistException("Role[%d] does not exist!");
		}
		role.setDisplayName(displayName);
		role.setComment(comment);
		try {
			roleService.updateRole(role);
			return successResult("Role[%s] is updated successfully!", role.getName());
		} catch (Exception e) {
			throw LogReplayException.operationFailedException("Failed to update Role[%d]!", id);
		}
	}
	
	@ResponseBody
	@RequestMapping(value = "/updatePermissions/{id}", method = RequestMethod.POST)
	public ModelMap updateRelatedPermissions(
			@PathVariable("id") Long id,
			@RequestParam(defaultValue = "") String permissionIds
			) {
		if(id == null) {
			throw LogReplayException.invalidParameterException("Id of permission should not be null!");
		}
		Role role = roleService.getRoleById(id);
		if(role == null) {
			throw LogReplayException.notExistException("Role[%d] does not exist!", id);
		}
		
		List<Long> permissionIdList = Lists.transform(Arrays.asList(StringUtils.split(permissionIds, ",")), new Function<String, Long>() {
			@Override
			public Long apply(String input) {
				return NumberUtils.toLong(input);
			}
		});
		List<Permission> permissionList = CollectionUtils.isNotEmpty(permissionIdList)
				? permissionService.getPermissionListResult(new QueryParamMap().addParam("id__in", permissionIdList))
				: Collections.<Permission>emptyList();
		try {
			roleService.updateRelatedPermissions(role, permissionList);
			return successResult("Role[%d] is updated successfully!", role.getId());
		} catch (Exception e) {
			throw LogReplayException.operationFailedException("Failed to update role[%d]!", id);
		}
	}
}
