package com.sogou.map.logreplay.controller;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sogou.map.logreplay.bean.PageInfo;
import com.sogou.map.logreplay.dao.base.Page;
import com.sogou.map.logreplay.dao.base.QueryParamMap;
import com.sogou.map.logreplay.exception.LogReplayException;
import com.sogou.map.logreplay.service.PageInfoService;
import com.sogou.map.logreplay.util.JsonUtil;
import com.sogou.map.mengine.common.bo.ApiException;
import com.sogou.map.mengine.common.service.BaseService;

@Component
@Path("/pageInfo")
public class PageInfoController extends BaseService {
	
	@Autowired
	private PageInfoService pageInfoService;

	@GET
	@Path("/list")
	public Response list(
			@DefaultValue(Page.DEFAULT_START) @QueryParam("start") int start,
			@DefaultValue(Page.DEFAULT_LIMIT) @QueryParam("limit") int limit,
			@QueryParam("pageNo") Integer pageNo,
			@QueryParam("name") String name,
			@QueryParam("updateBeginTime") String updateBeginTime,
			@QueryParam("updateEndTime") String updateEndTime
			) {
		Page<PageInfo> page = pageInfoService.getPageInfoPageResult(start, limit, new QueryParamMap()
			.addParam(pageNo != null, "pageNo", pageNo)
			.addParam(StringUtils.isNotBlank(name), "name__contains", name)
			.addParam(StringUtils.isNotBlank(updateBeginTime), "updateTime__ge", updateBeginTime)
			.addParam(StringUtils.isNotBlank(updateEndTime), "updateTime__le", updateEndTime)
			.orderByAsc("pageNo")
		);
		return successResultToJson(page, JsonUtil.configInstance(), true);
	}
	
	@GET
	@Path("/detail/{id}")
	public Response detail(@PathParam("id") Long id) {
		PageInfo info = pageInfoService.getPageInfoById(id);
		return successResultToJson(info, JsonUtil.configInstance(), true);
	}
	
	@GET
	@Path("/update/{id}")
	public Response update(@PathParam("id") Long id,
			@QueryParam("pageNo") Integer pageNo,
			@QueryParam("name") String name) throws ApiException {
		if(pageNo == null || StringUtils.isBlank(name)) {
			throw LogReplayException.invalidParameterException("Parameters are invalid!");
		}
		PageInfo info = pageInfoService.getPageInfoById(id);
		if(info == null) {
			throw LogReplayException.invalidParameterException(String.format("PageInfo[%d] does not exist!", id));
		}
		try {
			info.setName(name);
			info.setPageNo(pageNo);
			pageInfoService.updatePageInfo(info);
			return successResultToJson(String.format("PageInfo[%d] is updated successfully!", id), true);
		} catch (Exception e) {
			throw LogReplayException.operationFailedException(String.format("Failed to update PageInfo[%d]", id));
		}
	}
	
	@GET
	@Path("/create")
	public Response create(
			@QueryParam("pageNo") Integer pageNo,
			@QueryParam("name") String name
			) throws ApiException {
		if(pageNo == null || StringUtils.isBlank(name)) {
			throw LogReplayException.invalidParameterException("Parameters are invalid!");
		}
		try {
			PageInfo info = new PageInfo(pageNo, name);
			pageInfoService.createPageInfo(info);
			return successResultToJson(String.format("PageInfo[%s] is created successfully!", info.getId()), true);
		} catch (Exception e) {
			throw LogReplayException.operationFailedException("Failed to create PageInfo!");
		}
	}
	
}
