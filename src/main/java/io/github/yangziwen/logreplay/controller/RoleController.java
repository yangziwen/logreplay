package io.github.yangziwen.logreplay.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import io.github.yangziwen.logreplay.bean.Permission;
import io.github.yangziwen.logreplay.bean.Role;
import io.github.yangziwen.logreplay.controller.base.BaseController;
import io.github.yangziwen.logreplay.dao.base.QueryParamMap;
import io.github.yangziwen.logreplay.exception.LogReplayException;
import io.github.yangziwen.logreplay.service.PermissionService;
import io.github.yangziwen.logreplay.service.RoleService;

@RestController
@RequestMapping("/role")
public class RoleController extends BaseController {

	@Autowired
	private RoleService roleService;

	@Autowired
	private PermissionService permissionService;

	@GetMapping("/list")
	public ModelMap list() {
		List<Role> list = roleService.getRoleListResult(new QueryParamMap().orderByAsc("id"));
		return successResult(list);
	}

	@PostMapping("/update/{id}")
	@RequiresPermissions("role:modify")
	public ModelMap update(
			@PathVariable(value = "id") Long id,
			@RequestParam String displayName,
			@RequestParam String comment) {
		Role role = roleService.getRoleById(id);
		if (role == null) {
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

	@PostMapping("/updatePermissions/{id}")
	@RequiresPermissions({"role:modify"})
	public ModelMap updateRelatedPermissions(
			@PathVariable("id") Long id,
			@RequestParam(defaultValue = "") String permissionIds
			) {
		if (id == null) {
			throw LogReplayException.invalidParameterException("Id of permission should not be null!");
		}
		Role role = roleService.getRoleById(id);
		if (role == null) {
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
