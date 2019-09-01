package io.github.yangziwen.logreplay.controller;

import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.yangziwen.logreplay.bean.TagAction;
import io.github.yangziwen.logreplay.controller.base.BaseController;
import io.github.yangziwen.logreplay.service.TagActionService;

@RestController
@RequestMapping("/tagAction")
public class TagActionController extends BaseController {

	@Autowired
	private TagActionService tagActionService;

	@GetMapping("/list")
	@RequiresPermissions("/tag_info:view")
	public Map<String, Object> list() {
		List<TagAction> list = tagActionService.getTagActionListResult();
		return successResult(list);
	}

}
