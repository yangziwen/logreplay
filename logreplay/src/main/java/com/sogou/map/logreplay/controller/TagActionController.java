package com.sogou.map.logreplay.controller;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sogou.map.logreplay.bean.TagAction;
import com.sogou.map.logreplay.dao.base.QueryParamMap;
import com.sogou.map.logreplay.service.TagActionService;
import com.sogou.map.mengine.common.service.BaseService;

@Component
@Path("/tagAction")
public class TagActionController extends BaseService {
	
	@Autowired
	private TagActionService tagActionService;

	@GET
	@Path("/list")
	public Response list() {
		List<TagAction> list = tagActionService.getTagActionListResult(QueryParamMap.EMPTY_MAP);
		return successResultToJson(list, true);
	}
	
//	public 
	
}
