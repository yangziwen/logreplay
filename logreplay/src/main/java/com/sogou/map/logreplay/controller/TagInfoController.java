package com.sogou.map.logreplay.controller;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.sogou.map.logreplay.bean.PageInfo;
import com.sogou.map.logreplay.bean.Role;
import com.sogou.map.logreplay.bean.TagAction;
import com.sogou.map.logreplay.bean.TagInfo;
import com.sogou.map.logreplay.bean.TagInfo.InspectStatus;
import com.sogou.map.logreplay.bean.TagParam;
import com.sogou.map.logreplay.bean.TagTarget;
import com.sogou.map.logreplay.controller.base.BaseController;
import com.sogou.map.logreplay.dao.base.Page;
import com.sogou.map.logreplay.dao.base.QueryParamMap;
import com.sogou.map.logreplay.dto.TagInfoDto;
import com.sogou.map.logreplay.exception.LogReplayException;
import com.sogou.map.logreplay.service.PageInfoService;
import com.sogou.map.logreplay.service.TagActionService;
import com.sogou.map.logreplay.service.TagInfoService;
import com.sogou.map.logreplay.service.TagParamService;
import com.sogou.map.logreplay.service.TagTargetService;
import com.sogou.map.logreplay.util.AuthUtil;
import com.sogou.map.logreplay.util.ExcelUtil;
import com.sogou.map.logreplay.util.ExcelUtil.CellType;
import com.sogou.map.logreplay.util.ExcelUtil.Column;
import com.sogou.map.logreplay.util.JsonUtil;
import com.sogou.map.logreplay.util.ProductUtil;
import com.sogou.map.logreplay.util.TagFields;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;

@Component
@Path("/tagInfo")
public class TagInfoController extends BaseController {
	
	private static List<Column> TAG_INFO_COLUMN_LIST = buildTagInfoColumnList(false);
	
	private static List<Column> COMMON_TAG_INFO_COLUMN_LIST = buildTagInfoColumnList(true);
	
	@Autowired
	private PageInfoService pageInfoService;

	@Autowired
	private TagInfoService tagInfoService;
	
	@Autowired
	private TagActionService tagActionService;
	
	@Autowired
	private TagTargetService tagTargetService;
	
	@Autowired
	private TagParamService tagParamService;
	
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
			@QueryParam("inspectStatus") String inspectStatusStr,
			@QueryParam("devInspectStatus") String devInspectStatusStr
			) {
		Page<TagInfo> page = tagInfoService.getTagInfoPageResult(start, limit, buildQueryParamMap(
				pageNo, tagNo, pageName, tagName, updateBeginTime, updateEndTime, 
				isCommonTag, originVersionSince, originVersionUntil, inspectStatusStr, devInspectStatusStr)
		);
		fillHasParamsFlag(page.getList());
		return successResultToJson(page, JsonUtil.configInstance(), true);
	}
	
	/**
	 * 为tagInfo添加是否有参数(tagParam)的标识
	 */
	private void fillHasParamsFlag(List<TagInfo> tagInfoList) {
		if(CollectionUtils.isEmpty(tagInfoList)) {
			return;
		}
		Set<Long> tagInfoIdSet = new HashSet<Long>();
		for(TagInfo tagInfo: tagInfoList) {
			tagInfoIdSet.add(tagInfo.getId());
		}
		List<TagParam> tagParamList = tagParamService.getTagParamListResult(new QueryParamMap()
			.addParam("tagInfoId__in", tagInfoIdSet)
		);
		Set<Long> tagInfoIdsWithParam = Maps.uniqueIndex(tagParamList, new Function<TagParam, Long>() {
			@Override
			public Long apply(TagParam tagParam) {
				return tagParam.getTagInfoId();
			}
		}).keySet();
		for(TagInfo tagInfo: tagInfoList) {
			tagInfo.setHasParams(tagInfoIdsWithParam.contains(tagInfo.getId()));
		}
		
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
		TagInfo tagInfo = tagInfoService.getTagInfoByPageNoTagNoAndProductId(pageNo, tagNo, ProductUtil.getProductId());
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
		if(!AuthUtil.hasRole(Role.ADMIN)) {
			throw LogReplayException.unauthorizedException("Role[admin] is required!");
		}
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
		if(!AuthUtil.hasRole(Role.ADMIN)) {
			throw LogReplayException.unauthorizedException("Role[admin] is required!");
		}
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
			tagInfo.setInspectStatus(InspectStatus.UNCHECKED.getIntValue());
			tagInfo.setDevInspectStatus(InspectStatus.UNCHECKED.getIntValue());
			tagInfoService.createTagInfo(tagInfo);
			return successResultToJson(String.format("TagInfo[%d] is created successfully!", tagInfo.getId()), true);
		} catch (Exception e) {
			throw LogReplayException.operationFailedException("Failed to create TagInfo!");
		}
		
	}
	
	@POST
	@Path("/delete")
	public Response delete(@FormParam("id") Long id) {
		if(!AuthUtil.hasRole(Role.ADMIN)) {
			throw LogReplayException.unauthorizedException("Role[admin] is required!");
		}
		if(id == null || id <= 0) {
			throw LogReplayException.invalidParameterException("Parameters are invalid!");
		}
		try {
			tagInfoService.deleteTagInfoById(id);
			return successResultToJson(String.format("TagInfo[%d] is deleted successfully!", id), true);
		} catch (Exception e) {
			throw LogReplayException.operationFailedException(String.format("Failed to delete TagInfo[%d]!", id));
		}
	}
	
	@GET
	@Path("/checkDuplication")
	public Response checkDuplication(
			@QueryParam("id") Long id,
			@QueryParam("tagNo") Integer tagNo,
			@QueryParam("pageInfoId") Long pageInfoId) {
		if(tagNo == null || tagNo <= 0) {
			return Response.ok().entity("false").build();
		}
		if(id == null && tagInfoService.getTagInfoListResult(0, 1, new QueryParamMap()
				.addParam("productId", ProductUtil.getProductId())
				.addParam(pageInfoId != null, "pageInfoId", pageInfoId)
				.addParam("tagNo", tagNo)).size() > 0) {
			return Response.ok().entity("false").build();
		}
		if(tagInfoService.getTagInfoListResult(0, 1, new QueryParamMap()
				.addParam("productId", ProductUtil.getProductId())
				.addParam(pageInfoId != null, "pageInfoId", pageInfoId)
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
		if(tagInfoService.getTagInfoByPageNoTagNoAndProductId(pageNo, tagNo, ProductUtil.getProductId()) == null) {
			return Response.ok().entity("false").build();
		}
		return Response.ok().entity("true").build();
	}
	
	@GET
	@Path("/export")
	public Response exportTagInfos(
			@QueryParam("pageNo") Integer pageNo,
			@QueryParam("tagNo") Integer tagNo,
			@QueryParam("pageName") String pageName,
			@QueryParam("tagName") String tagName,
			@QueryParam("updateBeginTime") String updateBeginTime,
			@QueryParam("updateEndTime") String updateEndTime,
			@QueryParam("isCommonTag") Boolean isCommonTag,
			@QueryParam("originVersionSince") Integer originVersionSince,
			@QueryParam("originVersionUntil") Integer originVersionUntil,
			@QueryParam("inspectStatus") String inspectStatusStr,
			@QueryParam("devInspectStatus") String devInspectStatusStr
			) {
		List<TagInfo> list = tagInfoService.getTagInfoListResult(buildQueryParamMap(
				pageNo, tagNo, pageName, tagName, updateBeginTime, updateEndTime, 
				isCommonTag, originVersionSince, originVersionUntil, inspectStatusStr, devInspectStatusStr)
		);
		Map<Long, TagAction> actionMap = Maps.uniqueIndex(tagActionService.getTagActionListResult(), new Function<TagAction, Long>() {
			@Override
			public Long apply(TagAction action) {
				return action.getId();
			}
		});
		
		Map<Long, TagTarget> targetMap = Maps.uniqueIndex(tagTargetService.getTagTargetListResult(), new Function<TagTarget, Long>() {
			@Override
			public Long apply(TagTarget target) {
				return target.getId();
			}
		});
		Map<Long, TagParam> tagParamMap = Maps.uniqueIndex(tagParamService.getTagParamListResultWithInfos(QueryParamMap.emptyMap()), new Function<TagParam, Long>() {
			@Override
			public Long apply(TagParam tagParam) {
				return tagParam.getTagInfoId();
			}
		});
		List<TagInfoDto> dtoList = TagInfoDto.from(list, actionMap, targetMap, tagParamMap);
		List<Column> columnList = isCommonTag
				? COMMON_TAG_INFO_COLUMN_LIST
				: TAG_INFO_COLUMN_LIST;
		Workbook workbook = ExcelUtil.exportDataList(columnList, dtoList);
		String filename = ProductUtil.getCurrentProduct().getName() 
				+ (isCommonTag? "_公共操作详情.xls": "_操作详情.xls");
		return ExcelUtil.generateExcelResponse(workbook, filename);
	}
	
	/**
	 * 查询接口和excel导出接口都会用到这个组织参数map的方法
	 */
	private Map<String, Object> buildQueryParamMap(
			Integer pageNo,
			Integer tagNo,
			String pageName,
			String tagName,
			String updateBeginTime,
			String updateEndTime,
			Boolean isCommonTag,
			Integer originVersionSince,
			Integer originVersionUntil,
			String inspectStatusStr,		// 测试结果
			String devInspectStatusStr		// 自测结果
			) {
		InspectStatus inspectStatus = InspectStatus.from(NumberUtils.toInt(inspectStatusStr, -1));
		InspectStatus devInspectStatus = InspectStatus.from(NumberUtils.toInt(devInspectStatusStr, -1));
		List<Long> pageInfoIdList = new ArrayList<Long>();
		if(StringUtils.isNotBlank(pageName)) {
			List<PageInfo> pageInfoList = pageInfoService.getPageInfoListResult(new QueryParamMap()
				.addParam("name__contain", pageName)
				.addParam("productId", ProductUtil.getProductId())
			);
			for(PageInfo pageInfo: pageInfoList) {
				pageInfoIdList.add(pageInfo.getId());
			}
		}
		return new QueryParamMap()
			.addParam("productId", ProductUtil.getProductId())
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
			.addParam(devInspectStatus != InspectStatus.UNKNOWN, "devInspectStatus", devInspectStatus.getIntValue())
			.orderByAsc("page_info.page_no").orderByAsc("tagNo");
	}
	
	private static List<Column> buildTagInfoColumnList(boolean isCommonTag) {
		List<Column> columnList = new ArrayList<Column>();
		if(!isCommonTag) {
			columnList.add(ExcelUtil.column(TagFields.pageNo, "pageNo", 3000, CellType.number));
			columnList.add(ExcelUtil.column(TagFields.pageName, "pageName", 8000, CellType.text));
		}
		columnList.add(ExcelUtil.column(TagFields.tagNo, "tagNo", 3000, CellType.number));
		columnList.add(ExcelUtil.column(TagFields.tagName, "tagName", 10000, CellType.text));
		
		columnList.add(ExcelUtil.column(TagFields.actionName, "actionName", 3000, CellType.text));
		columnList.add(ExcelUtil.column(TagFields.targetName, "targetName", 3000, CellType.text));
		
		columnList.add(ExcelUtil.column(TagFields.originVersionDisplay, "originVersionDisplay", 3000, CellType.text));
		columnList.add(ExcelUtil.column(TagFields.devInspectStatus, "devInspectStatus", 4000, CellType.text));
		columnList.add(ExcelUtil.column(TagFields.inspectStatus, "inspectStatus", 4000, CellType.text));
		
		columnList.add(ExcelUtil.column(TagFields.comment, "comment", 6000, CellType.text));
		
		columnList.add(ExcelUtil.column(TagFields.tagParamDisplay, "tagParamDisplay", 10000, CellType.text));
		columnList.add(ExcelUtil.column(TagFields.tagParamComment, "tagParamComment", 10000, CellType.text));
		
		return columnList;
	}
	
	@POST
	@Path("/import")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response importTagInfos(FormDataMultiPart multiPartData) {
		if(!AuthUtil.hasRole(Role.ADMIN)) {
			throw LogReplayException.unauthorizedException("Role[admin] is required!");
		}
		FormDataBodyPart filePart = multiPartData.getField("file");
		List<Map<String, String>> mapList = ExcelUtil.importMapList(filePart.getValueAs(InputStream.class));
		List<TagInfoDto> dtoList = TagFields.convertToTagInfoDtoList(mapList);
		int count = importTagInfoByDtoList(dtoList);
		return successResultToJson(new ModelMap("count", count), true);
	}
	
	public int importTagInfoByDtoList(List<TagInfoDto> dtoList) {
		Map<Integer, PageInfo> pageInfoMap = Maps.newHashMap();
		pageInfoMap.put(0, new PageInfo());			// 遇到“公共操作项”时，pageNo会被解析为0
		pageInfoMap.put(null, new PageInfo());		// 插入个空key，以防万一
		
		Map<String, TagAction> actionMap = Maps.newHashMap();
		List<TagAction> actionList = tagActionService.getTagActionListResult();
		for(TagAction action: actionList) {
			actionMap.put(action.getName(), action);
		}
		
		Map<String, TagTarget> targetMap = Maps.newHashMap();
		List<TagTarget> targetList = tagTargetService.getTagTargetListResult();
		for(TagTarget target: targetList) {
			targetMap.put(target.getName(), target);
		}
		
		int cnt = 0;
		for(TagInfoDto tagInfoDto: dtoList) {
			if(createTagInfoByDto(tagInfoDto, pageInfoMap, actionMap, targetMap)) {
				cnt ++;
			}
		}
		return cnt;
	}
	
	private PageInfo getOrCreatePageInfoIfNotExist(Integer pageNo, String pageName) {
		if(pageNo <= 0) {
			return null;
		}
		PageInfo pageInfo = pageInfoService.getPageInfoByPageNoAndProductId(pageNo, ProductUtil.getProductId());
		if(pageInfo == null) {
			pageInfo = new PageInfo(pageNo, pageName);
			pageInfoService.createPageInfo(pageInfo);
		}
		return pageInfo;
	}
	
	private boolean createTagInfoByDto(
			TagInfoDto tagInfoDto, 
			Map<Integer, PageInfo> pageInfoMap,		// Map<PageNo, PageInfo>
			Map<String, TagAction> actionMap,		// Map<TagAction.name, TagAction>
			Map<String, TagTarget> targetMap) {		// Map<TagTarget.name, TagTarget>
		
		if(tagInfoService.getTagInfoByPageNoTagNoAndProductId(
				tagInfoDto.getPageNo(), 
				tagInfoDto.getTagNo(), 
				ProductUtil.getProductId()) != null) {
			return false;
		}
		
		if(!pageInfoMap.containsKey(tagInfoDto.getPageNo())) {
			PageInfo pageInfo = getOrCreatePageInfoIfNotExist(tagInfoDto.getPageNo(), tagInfoDto.getPageName());
			if(pageInfo != null) {
				pageInfoMap.put(pageInfo.getPageNo(), pageInfo);
			}
		}
		PageInfo pageInfo = pageInfoMap.get(tagInfoDto.getPageNo());
		
		TagInfo tagInfo = new TagInfo();
		tagInfo.setProductId(ProductUtil.getProductId());
		tagInfo.setPageInfoId(pageInfo.getId());
		tagInfo.setPageNo(tagInfoDto.getPageNo());
		tagInfo.setTagNo(tagInfoDto.getTagNo());
		tagInfo.setName(tagInfoDto.getTagName());
		tagInfo.setActionId(actionMap.containsKey(tagInfoDto.getActionName())
				? actionMap.get(tagInfoDto.getActionName()).getId()
				: null);
		tagInfo.setTargetId(targetMap.containsKey(tagInfoDto.getTargetName())
				? targetMap.get(tagInfoDto.getTargetName()).getId()
				: null);
		tagInfo.setComment(tagInfoDto.getComment());
		tagInfo.setDevInspectStatus(InspectStatus.UNCHECKED.getIntValue());
		tagInfo.setInspectStatus(InspectStatus.UNCHECKED.getIntValue());
		tagInfo.setOriginVersion(tagInfoDto.getOriginVersion());
		tagInfoService.createTagInfo(tagInfo);
		
		TagParam tagParam = tagInfoDto.getTagParam();
		if(tagParam != null) {
			tagParam.setTagInfoId(tagInfo.getId());
			tagParamService.renewTagParamAndParamInfo(tagParam, tagParam.getParamInfoList());
		}
		
		return true;
	}
	
}
