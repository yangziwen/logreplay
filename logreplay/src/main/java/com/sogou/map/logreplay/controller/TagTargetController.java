package com.sogou.map.logreplay.controller;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sogou.map.logreplay.bean.TagTarget;
import com.sogou.map.logreplay.service.TagTargetService;
import com.sogou.map.mengine.common.service.BaseService;

@Component
@Path("/tagTarget")
public class TagTargetController extends BaseService {
	
	@Autowired
	private TagTargetService tagTargetService;

	@GET
	@Path("/list")
	public Response list() {
		List<TagTarget> list = tagTargetService.getTagTargetListResult();
		return successResultToJson(list, true);
	}
}
