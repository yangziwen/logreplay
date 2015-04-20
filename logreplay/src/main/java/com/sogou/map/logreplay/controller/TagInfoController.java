package com.sogou.map.logreplay.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.sogou.map.logreplay.bean.PageInfo;
import com.sogou.map.logreplay.bean.Role;
import com.sogou.map.logreplay.bean.TagAction;
import com.sogou.map.logreplay.bean.TagInfo;
import com.sogou.map.logreplay.bean.TagInfo.InspectStatus;
import com.sogou.map.logreplay.bean.TagParam;
import com.sogou.map.logreplay.bean.TagTarget;
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
import com.sogou.map.logreplay.util.ExcelExportUtil;
import com.sogou.map.logreplay.util.ExcelExportUtil.CellType;
import com.sogou.map.logreplay.util.ExcelExportUtil.Column;
import com.sogou.map.logreplay.util.JsonUtil;
import com.sogou.map.logreplay.util.ProductUtil;
import com.sogou.map.mengine.common.service.BaseService;

@Component
@Path("/tagInfo")
public class TagInfoController extends BaseService {
	
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
			@QueryParam("inspectMode") String inspectMode
			) {
		InspectStatus inspectStatus = InspectStatus.from(NumberUtils.toInt(inspectStatusStr, -1));
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
		String inspectStatusField = Role.DEV.equals(inspectMode)? "devInspectStatus": "inspectStatus";
		Page<TagInfo> page = tagInfoService.getTagInfoPageResult(start, limit, new QueryParamMap()
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
			.addParam(inspectStatus != InspectStatus.UNKNOWN, inspectStatusField, inspectStatus.getIntValue())
			.orderByAsc("page_info.page_no").orderByAsc("tagNo")
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
	public Response export(
			@QueryParam("isCommonTag") boolean isCommonTag
			) {
		List<TagInfo> list = tagInfoService.getTagInfoListResult(new QueryParamMap()
			.addParam("productId", ProductUtil.getProductId())
			.addParam(Boolean.FALSE.equals(isCommonTag), "page_info.id__is_not_null")
			.addParam(Boolean.TRUE.equals(isCommonTag), "page_info.id__is_null")
			.orderByAsc("page_info.page_no").orderByAsc("tagNo")
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
		HSSFWorkbook workbook = ExcelExportUtil.exportDataList(columnList, dtoList);
		String filename = ProductUtil.getCurrentProduct().getName() 
				+ (isCommonTag? "_公共操作详情.xls": "_操作详情.xls");
		return ExcelExportUtil.generateExcelResponse(workbook, filename);
	}
	
	private static List<Column> buildTagInfoColumnList(boolean isCommonTag) {
		List<Column> columnList = new ArrayList<Column>();
		if(!isCommonTag) {
			columnList.add(ExcelExportUtil.column("页面编号", "pageNo", 3000, CellType.number));
			columnList.add(ExcelExportUtil.column("页面名称", "pageName", 8000, CellType.text));
		}
		columnList.add(ExcelExportUtil.column("操作编号", "tagNo", 3000, CellType.number));
		columnList.add(ExcelExportUtil.column("操作项名称", "tagName", 10000, CellType.text));
		
		columnList.add(ExcelExportUtil.column("操作动作", "actionName", 3000, CellType.text));
		columnList.add(ExcelExportUtil.column("操作目标", "targetName", 3000, CellType.text));
		
		columnList.add(ExcelExportUtil.column("初始版本", "originVersionDisplay", 3000, CellType.text));
		columnList.add(ExcelExportUtil.column("开发校验结果", "devInspectStatus", 4000, CellType.text));
		columnList.add(ExcelExportUtil.column("测试校验结果", "inspectStatus", 4000, CellType.text));
		
		columnList.add(ExcelExportUtil.column("操作项备注", "comment", 6000, CellType.text));
		
		columnList.add(ExcelExportUtil.column("操作参数", "tagParamDisplay", 10000, CellType.text));
		columnList.add(ExcelExportUtil.column("操作参数备注", "tagParamComment", 10000, CellType.text));
		
		return columnList;
	}
	
}
