package io.github.yangziwen.logreplay.controller;

import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.yangziwen.logreplay.bean.TagTarget;
import io.github.yangziwen.logreplay.controller.base.BaseController;
import io.github.yangziwen.logreplay.service.TagTargetService;

@RestController
@RequestMapping("/tagTarget")
public class TagTargetController extends BaseController {

	@Autowired
	private TagTargetService tagTargetService;

	@GetMapping("/list")
	@RequiresPermissions("tag_info:view")
	public Map<String, Object> list() {
		List<TagTarget> list = tagTargetService.getTagTargetListResult();
		return successResult(list);
	}
}
