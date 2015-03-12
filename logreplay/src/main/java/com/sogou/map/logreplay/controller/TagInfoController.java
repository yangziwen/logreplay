package com.sogou.map.logreplay.controller;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sogou.map.logreplay.bean.PageInfo;
import com.sogou.map.logreplay.bean.TagInfo;
import com.sogou.map.logreplay.bean.TagInfo.InspectStatus;
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
			@QueryParam("pageName") String pageName,
			@QueryParam("tagName") String tagName,
			@QueryParam("updateBeginTime") String updateBeginTime,
			@QueryParam("updateEndTime") String updateEndTime,
			@QueryParam("isCommonTag") Boolean isCommonTag,
			@QueryParam("originVersionSince") Integer originVersionSince,
			@QueryParam("originVersionUntil") Integer originVersionUntil,
			@QueryParam("inspectStatus") String inspectStatusStr
			) {
		InspectStatus inspectStatus = InspectStatus.from(NumberUtils.toInt(inspectStatusStr, -1));
		List<Long> pageInfoIdList = new ArrayList<Long>();
		if(StringUtils.isNotBlank(pageName)) {
			List<PageInfo> pageInfoList = pageInfoService.getPageInfoListResult(new QueryParamMap().addParam("name__contain", pageName));
			for(PageInfo pageInfo: pageInfoList) {
				pageInfoIdList.add(pageInfo.getId());
			}
		}
		Page<TagInfo> page = tagInfoService.getTagInfoPageResult(start, limit, new QueryParamMap()
			.addParam(pageNo != null, "pageNo", pageNo)
			.addParam(tagNo != null, "tagNo", tagNo)
			.addParam(CollectionUtils.isNotEmpty(pageInfoIdList), "pageInfoId__in", pageInfoIdList)
			.addParam(StringUtils.isNotBlank(tagName), "name__contain", tagName)
			.addParam(StringUtils.isNotBlank(pageName), "page_info.name__contain", pageName)
			.addParam(StringUtils.isNotBlank(updateBeginTime), "updateTime__ge", updateBeginTime)
			.addParam(StringUtils.isNotBlank(updateEndTime), "updateTime__le", updateEndTime)
			.addParam(Boolean.FALSE.equals(isCommonTag), "page_info.id__is_not_null")
			.addParam(Boolean.TRUE.equals(isCommonTag), "page_info.id__is_null")
			.addParam(originVersionSince != null && originVersionSince > 0, "originVersion__ge", originVersionSince)
			.addParam(originVersionUntil != null && originVersionUntil > 0 , "originVersion__le", originVersionUntil)
			.addParam(inspectStatus != InspectStatus.UNKNOWN, "inspectStatus", inspectStatus.getIntValue())
			.orderByAsc("page_info.page_no").orderByAsc("tagNo")
		);
		return successResultToJson(page, JsonUtil.configInstance(), true);
	}
	
	@GET
	@Path("/detail/{id}")
	public Response detail(@PathParam("id") Long id) {
		TagInfo tagInfo = tagInfoService.getTagInfoById(id);
		return successResultToJson(tagInfo, JsonUtil.configInstance(), true);
	}
	
	@GET
	@Path("/detailByPageNoAndTagNo/{pageNo}/{tagNo}")
	public Response detailByPageNoAndTagNo(
			@PathParam("pageNo") Integer pageNo,
			@PathParam("tagNo") Integer tagNo) {
		TagInfo tagInfo = tagInfoService.getTagInfoByPageNoAndTagNo(pageNo, tagNo);
		return successResultToJson(tagInfo, JsonUtil.configInstance(), true);
	}
	
	@POST
	@Path("/update")
	public Response update(
			@FormParam("id") Long id,
			@FormParam("tagNo") Integer tagNo,
			@FormParam("name") String name,
			@FormParam("pageInfoId") Long pageInfoId,
			@FormParam("actionId") Long actionId,
			@FormParam("targetId") Long targetId,
			@FormParam("originVersion") Integer originVersion,
			@FormParam("comment") String comment
			) {
		boolean needPageInfo = tagNo < TagInfo.COMMON_TAG_NO_MIN_VALUE;
		if(StringUtils.isBlank(name)
				|| tagNo == null
				|| (pageInfoId == null && needPageInfo)
				|| actionId == null
				|| targetId == null
				|| originVersion == null || originVersion <= 0) {
			throw LogReplayException.invalidParameterException("Parameters are invalid!");
		}
		PageInfo pageInfo = needPageInfo ? pageInfoService.getPageInfoById(pageInfoId) : null;
		if(pageInfo == null && needPageInfo) {
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
			tagInfo.setPageNo(pageInfo != null? pageInfo.getPageNo(): null);
			tagInfo.setActionId(actionId);
			tagInfo.setTargetId(targetId);
			tagInfo.setOriginVersion(originVersion);
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
			@FormParam("originVersion") Integer originVersion,
			@FormParam("comment") String comment
			) {
		boolean needPageInfo = tagNo < TagInfo.COMMON_TAG_NO_MIN_VALUE;
		if(StringUtils.isBlank(name)
				|| tagNo == null
				|| (pageInfoId == null && needPageInfo)
				|| actionId == null
				|| targetId == null
				|| originVersion == null || originVersion <= 0) {
			throw LogReplayException.invalidParameterException("Parameters are invalid!");
		}
		PageInfo pageInfo = needPageInfo ? pageInfoService.getPageInfoById(pageInfoId) : null;
		if(pageInfo == null && needPageInfo) {
			throw LogReplayException.invalidParameterException(String.format("PageInfo[%d] does not exist!", pageInfoId));
		}
		try {
			TagInfo tagInfo = new TagInfo();
			tagInfo.setTagNo(tagNo);
			tagInfo.setName(name);
			tagInfo.setPageInfoId(pageInfoId);
			tagInfo.setPageNo(pageInfo != null? pageInfo.getPageNo(): null);
			tagInfo.setActionId(actionId);
			tagInfo.setTargetId(targetId);
			tagInfo.setOriginVersion(originVersion);
			tagInfo.setComment(comment);
			tagInfoService.createTagInfo(tagInfo);
			return successResultToJson(String.format("TagInfo[%d] is created successfully!", tagInfo.getId()), true);
		} catch (Exception e) {
			throw LogReplayException.operationFailedException("Failed to create TagInfo!");
		}
		
	}
	
	@GET
	@Path("/checkDuplication")
	public Response checkDuplication(
			@QueryParam("id") Long id,
			@QueryParam("tagNo") Integer tagNo,
			@QueryParam("pageInfoId") Long pageInfoId) {
		if(tagNo == null || tagNo <= 0 || pageInfoId == null || pageInfoId <= 0) {
			return Response.ok().entity("false").build();
		}
		if(id == null && tagInfoService.getTagInfoListResult(0, 1, new QueryParamMap()
				.addParam("pageInfoId", pageInfoId)
				.addParam("tagNo", tagNo)).size() > 0) {
			return Response.ok().entity("false").build();
		}
		if(tagInfoService.getTagInfoListResult(0, 1, new QueryParamMap()
				.addParam("pageInfoId", pageInfoId)
				.addParam("tagNo", tagNo)
				.addParam("id__ne", id)).size() > 0) {
			return Response.ok().entity("false").build();
		}
		return Response.ok().entity("true").build();
	}
	
	@GET
	@Path("/checkExist")
	public Response checkExist(
			@QueryParam("pageNo") Integer pageNo,
			@QueryParam("tagNo") Integer tagNo) {
		if(tagNo == null || (tagNo < TagInfo.COMMON_TAG_NO_MIN_VALUE && pageNo == null)) {
			return Response.ok().entity("false").build();
		}
		if(tagInfoService.getTagInfoByPageNoAndTagNo(pageNo, tagNo) == null) {
			return Response.ok().entity("false").build();
		}
		return Response.ok().entity("true").build();
	}
	
}
