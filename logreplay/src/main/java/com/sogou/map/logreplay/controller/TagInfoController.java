package com.sogou.map.logreplay.controller;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sogou.map.logreplay.bean.PageInfo;
import com.sogou.map.logreplay.bean.TagInfo;
import com.sogou.map.logreplay.dao.base.Page;
import com.sogou.map.logreplay.dao.base.QueryParamMap;
import com.sogou.map.logreplay.exception.LogReplayException;
import com.sogou.map.logreplay.service.PageInfoService;
import com.sogou.map.logreplay.service.TagInfoService;
import com.sogou.map.logreplay.util.JsonUtil;
import com.sogou.map.mengine.common.service.BaseService;

@Component
@Path("/tagInfo")
public class TagInfoController extends BaseService {

	@Autowired
	private TagInfoService tagInfoService;
	
	@Autowired
	private PageInfoService pageInfoService;
	
	@GET
	@Path("/list")
	public Response list(
			@DefaultValue(Page.DEFAULT_START) @QueryParam("start") int start,
			@DefaultValue(Page.DEFAULT_LIMIT) @QueryParam("limit") int limit,
			@QueryParam("pageNo") Integer pageNo,
			@QueryParam("tagNo") Integer tagNo,
			@QueryParam("name") String name,
			@QueryParam("updateBeginTime") String updateBeginTime,
			@QueryParam("updateEndTime") String updateEndTime
			) {
		Page<TagInfo> page = tagInfoService.getTagInfoPageResult(start, limit, new QueryParamMap()
			.addParam(pageNo != null, "pageNo", pageNo)
			.addParam(tagNo != null, "tagNo", tagNo)
			.addParam(StringUtils.isNotBlank(name), "name__contain", name)
			.addParam(StringUtils.isNotBlank("updateBeginTime"), "updateTime__ge", updateBeginTime)
			.addParam(StringUtils.isNotBlank("updateEndTime"), "updateTime__le", updateEndTime)
			.orderByAsc("pageNo").orderByAsc("tagNo")
		);
		return successResultToJson(page, JsonUtil.configInstance(), true);
	}
	
	@GET
	@Path("/detail/{id}")
	public Response detail(@PathParam("id") Long id) {
		TagInfo tagInfo = tagInfoService.getTagInfoById(id);
		return successResultToJson(tagInfo, JsonUtil.configInstance(), true);
	}
	
	@GET	// ‘› ±”√get£¨∑Ω±„≤‚ ‘
	@Path("/update")
	public Response update(@PathParam("id") Long id,
			@QueryParam("tagNo") Integer tagNo,
			@QueryParam("name") String name,
			@QueryParam("pageInfoId") Long pageInfoId,
			@QueryParam("actionId") Long actionId,
			@QueryParam("targetId") Long targetId,
			@QueryParam("comment") String comment
			) {
		if(StringUtils.isBlank(name)
				|| tagNo == null
				|| pageInfoId == null
				|| actionId == null
				|| targetId == null) {
			throw LogReplayException.invalidParameterException("Parameters are invalid!");
		}
		PageInfo pageInfo = pageInfoService.getPageInfoById(pageInfoId);
		if(pageInfo == null) {
			throw LogReplayException.invalidParameterException(String.format("PageInfo[%d] does not exist!", pageInfoId));
		}
		TagInfo tagInfo = tagInfoService.getTagInfoById(id);
		if(tagInfo == null) {
			throw LogReplayException.invalidParameterException(String.format("TagInfo[%d] does not exist!", id));
		}
		try {
			tagInfo.setTagNo(tagNo);
			tagInfo.setName(name);
			tagInfo.setPageInfoId(pageInfoId);
			tagInfo.setActionId(actionId);
			tagInfo.setTargetId(targetId);
			tagInfo.setComment(comment);
			tagInfoService.updateTagInfo(tagInfo);
			return successResultToJson(String.format("TagInfo[%d] is updated successfully!", id), true);
		} catch (Exception e) {
			throw LogReplayException.operationFailedException(String.format("Failed to update TagInfo[%d]", id));
		}
		
	}
	
	@POST
	@Path("/create")
	public Response create(
			@FormParam("tagNo") Integer tagNo,
			@FormParam("name") String name,
			@FormParam("pageInfoId") Long pageInfoId,
			@FormParam("actionId") Long actionId,
			@FormParam("targetId") Long targetId,
			@FormParam("comment") String comment
			) {
		if(StringUtils.isBlank(name)
				|| tagNo == null
				|| pageInfoId == null
				|| actionId == null
				|| targetId == null) {
			throw LogReplayException.invalidParameterException("Parameters are invalid!");
		}
		PageInfo pageInfo = pageInfoService.getPageInfoById(pageInfoId);
		if(pageInfo == null) {
			throw LogReplayException.invalidParameterException(String.format("PageInfo[%d] does not exist!", pageInfoId));
		}
		try {
			TagInfo tagInfo = new TagInfo();
			tagInfo.setTagNo(tagNo);
			tagInfo.setName(name);
			tagInfo.setPageInfoId(pageInfoId);
			tagInfo.setActionId(actionId);
			tagInfo.setTargetId(targetId);
			tagInfo.setComment(comment);
			tagInfoService.createTagInfo(tagInfo);
			return successResultToJson(String.format("TagInfo[%d] is created successfully!", tagInfo.getId()), true);
		} catch (Exception e) {
			throw LogReplayException.operationFailedException("Failed to create TagInfo!");
		}
		
	}
	
}
