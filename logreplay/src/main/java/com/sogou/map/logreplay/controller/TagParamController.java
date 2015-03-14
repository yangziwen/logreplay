package com.sogou.map.logreplay.controller;

import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.sogou.map.logreplay.bean.ParamInfo;
import com.sogou.map.logreplay.bean.TagParam;
import com.sogou.map.logreplay.exception.LogReplayException;
import com.sogou.map.logreplay.service.TagInfoService;
import com.sogou.map.logreplay.service.TagParamService;
import com.sogou.map.logreplay.util.AuthUtil;
import com.sogou.map.mengine.common.service.BaseService;

@Component
@Path("/tagParam")
public class TagParamController extends BaseService {
	
	@Autowired
	private TagParamService tagParamService;
	
	@Autowired
	private TagInfoService tagInfoService;

	@GET
	@Path("/detail")
	public Response detail(
			@QueryParam("tagInfoId") Long tagInfoId) {
		return successResultToJson(tagParamService.getTagParamByTagInfoId(tagInfoId), true);
	}
	
	@POST
	@Path("/update")
	public Response update(
			@FormParam("tagInfoId") Long tagInfoId,
			@FormParam("comment") String comment,
			@FormParam("paramInfoList") String paramInfoListJson) {
		if(!AuthUtil.hasRole("admin")) {
			throw LogReplayException.unauthorizedException("Role[admin] is required!");
		}
		if(tagInfoId == null || tagInfoService.getTagInfoById(tagInfoId) == null) {
			throw LogReplayException.invalidParameterException(String.format("TagInfo[%d] does not exist!", tagInfoId));
		}
		List<ParamInfo> paramInfoList = JSON.parseArray(paramInfoListJson, ParamInfo.class);
		TagParam tagParam = tagParamService.getTagParamByTagInfoId(tagInfoId);
		if(tagParam == null) {
			tagParam = new TagParam(tagInfoId, comment);
		} else {
			tagParam.setComment(comment);
		}
		try {
			tagParamService.renewTagParamAndParamInfo(tagParam, paramInfoList);
			return successResultToJson(String.format("TagParam[%d] is renewed successfully!", tagParam.getId()), true);
		} catch (Exception e) {
			throw LogReplayException.operationFailedException("Failed to renew TagParam!");
		}
	}
	
	
}
