package com.sogou.map.logreplay.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sogou.map.logreplay.bean.TagAction;
import com.sogou.map.logreplay.controller.base.BaseController;
import com.sogou.map.logreplay.service.TagActionService;

@Controller
@RequestMapping("/tagAction")
public class TagActionController extends BaseController {
	
	@Autowired
	private TagActionService tagActionService;

	@ResponseBody
	@RequestMapping("/list")
	public Map<String, Object> list() {
		List<TagAction> list = tagActionService.getTagActionListResult();
		return successResult(list);
	}
	
}
