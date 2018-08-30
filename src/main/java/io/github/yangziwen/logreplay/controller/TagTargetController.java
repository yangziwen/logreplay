package io.github.yangziwen.logreplay.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.github.yangziwen.logreplay.bean.TagTarget;
import io.github.yangziwen.logreplay.controller.base.BaseController;
import io.github.yangziwen.logreplay.service.TagTargetService;

@Controller
@RequestMapping("/tagTarget")
public class TagTargetController extends BaseController {
	
	@Autowired
	private TagTargetService tagTargetService;

	@ResponseBody
	@RequestMapping("/list")
	public Map<String, Object> list() {
		List<TagTarget> list = tagTargetService.getTagTargetListResult();
		return successResult(list);
	}
}
