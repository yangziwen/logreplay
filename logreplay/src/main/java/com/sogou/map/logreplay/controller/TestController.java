package com.sogou.map.logreplay.controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Component;

import com.sogou.map.mengine.common.service.BaseService;

@Component
@Path("/test")
public class TestController extends BaseService {

	@GET
	@Path("show")
	public Response show() {
		return successResultToJson("success", true);
	}
	
}
