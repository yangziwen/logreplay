package com.sogou.map.logreplay.controller;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sogou.map.logreplay.bean.TagAction;
import com.sogou.map.logreplay.controller.base.BaseController;
import com.sogou.map.logreplay.service.TagActionService;

@Component
@Path("/tagAction")
public class TagActionController extends BaseController {
	
	@Autowired
	private TagActionService tagActionService;

	@GET
	@Path("/list")
	public Response list() {
		List<TagAction> list = tagActionService.getTagActionListResult();
		return successResultToJson(list, true);
	}
	
}
