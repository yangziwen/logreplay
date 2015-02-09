package com.sogou.map.logreplay.controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sogou.map.logreplay.service.TagParamService;
import com.sogou.map.mengine.common.service.BaseService;

@Component
@Path("/tagParam")
public class TagParamController extends BaseService {
	
	@Autowired
	private TagParamService tagParamService;

	@GET
	@Path("/detail")
	public Response detail(
			@QueryParam("tagInfoId") Long tagInfoId) {
		return successResultToJson(tagParamService.getTagParamByTagInfoId(tagInfoId), true);
	}
}
