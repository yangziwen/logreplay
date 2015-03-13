package com.sogou.map.logreplay.controller;

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
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sogou.map.logreplay.bean.InspectionRecord;
import com.sogou.map.logreplay.bean.PageInfo;
import com.sogou.map.logreplay.bean.TagInfo;
import com.sogou.map.logreplay.bean.User;
import com.sogou.map.logreplay.dao.base.Page;
import com.sogou.map.logreplay.dao.base.QueryParamMap;
import com.sogou.map.logreplay.dto.InspectionRecordDto;
import com.sogou.map.logreplay.exception.LogReplayException;
import com.sogou.map.logreplay.service.InspectionRecordService;
import com.sogou.map.logreplay.service.PageInfoService;
import com.sogou.map.logreplay.service.TagInfoService;
import com.sogou.map.logreplay.service.UserService;
import com.sogou.map.logreplay.util.AuthUtil;
import com.sogou.map.logreplay.util.JsonUtil;
import com.sogou.map.mengine.common.service.BaseService;

@Component
@Path("/inspectionRecord")
public class InspectionRecordController extends BaseService {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PageInfoService pageInfoService;
	
	@Autowired
	private TagInfoService tagInfoService;
	
	@Autowired
	private InspectionRecordService inspectionRecordService;
	
	@GET
	@Path("/list")
	public Response list(
			@DefaultValue(Page.DEFAULT_START) @QueryParam("start") int start,
			@DefaultValue(Page.DEFAULT_LIMIT) @QueryParam("limit") int limit,
			@QueryParam("submitterName") String submitterName,
			@QueryParam("solverName") String solverName,
			@QueryParam("pageNo") Integer pageNo,
			@QueryParam("tagNo") Integer tagNo,
			@QueryParam("valid") String validStr,
			@QueryParam("solved") String solvedStr,
			@QueryParam("submitTimeSince") String submitTimeSince,
			@QueryParam("submitTimeUntil") String submitTimeUntil
			) {
		Boolean valid = BooleanUtils.toBooleanObject(validStr);
		Boolean solved = BooleanUtils.toBooleanObject(solvedStr);
		List<Long> submitterIdList = null;
		if(StringUtils.isNotBlank(submitterName)) {
			submitterIdList = userService.getUserIdListResultByName(submitterName);
			if(CollectionUtils.isEmpty(submitterIdList)) {
				return successResultToJson(Page.<InspectionRecordDto>emptyPage(), JsonUtil.configInstance(), true);
			}
		}
		List<Long> solverIdList = null;
		if(StringUtils.isNotBlank(solverName)) {
			solverIdList = userService.getUserIdListResultByName(solverName);
			if(CollectionUtils.isEmpty(solverIdList)) {
				return successResultToJson(Page.<InspectionRecordDto>emptyPage(), JsonUtil.configInstance(), true);
			}
		}
		QueryParamMap params = new QueryParamMap()
			.addParam(submitterIdList != null, "submitterId__in", submitterIdList)
			.addParam(solverIdList != null, "solverId__in", solverIdList)
			.addParam(pageNo != null && pageNo > 0, "pageNo", pageNo)
			.addParam(tagNo != null && tagNo > 0, "tagNo", tagNo)
			.addParam(valid != null, "valid", valid)
			.addParam(solved != null, "solved", solved)
			.addParam(StringUtils.isNotBlank(submitTimeSince), "createTime__ge", submitTimeSince)
			.addParam(StringUtils.isNotBlank(submitTimeUntil), "createTime__le", submitTimeUntil)
			.orderByDesc("createTime")
		;
		Page<InspectionRecordDto> page = InspectionRecordDto.from(inspectionRecordService.getInspectionRecordPaginateResultWithTransientFields(start, limit, params));
		return successResultToJson(page, JsonUtil.configInstance(), true);
	}
	
	@GET
	@Path("/detail/{id}")
	public Response detail(@PathParam("id") Long id) {
		InspectionRecord record = inspectionRecordService.getInspectionRecordById(id);
		return successResultToJson(new InspectionRecordDto().from(record), JsonUtil.configInstance(), true);
	}
	
	/**
	 * 提交校验结果，相当于创建
	 * @return
	 */
	@POST
	@Path("/submit")
	public Response submit(
			@FormParam("pageNo") Integer pageNo,
			@FormParam("tagNo") Integer tagNo,
			@FormParam("valid") Boolean valid,
			@FormParam("comment") String comment
			) {
		if(pageNo == null || tagNo == null || valid == null) {
			throw LogReplayException.invalidParameterException("Parameters invalid!");
		}
		PageInfo pageInfo = pageInfoService.getPageInfoByPageNo(pageNo);
		if(pageInfo == null) {
			throw LogReplayException.invalidParameterException(String.format("PageInfo with pageNo[%d] does not exist!", pageNo));
		}
		TagInfo tagInfo = tagInfoService.getTagInfoByPageNoAndTagNo(pageNo, tagNo);
		if(tagInfo == null) {
			throw LogReplayException.invalidParameterException(String.format("TagInfo with pageNo[%d] and tagNo[%d] does not exist!", pageNo, tagNo));
		}
		try {
			User curUser = AuthUtil.getCurrentUser();
			InspectionRecord record = new InspectionRecord(pageInfo.getId(), tagInfo.getId(), curUser.getId(), valid, comment);
			record.setTagInfo(tagInfo);
			inspectionRecordService.createInspectionRecord(record);
			return successResultToJson("InspectionRecord is created successfully!", true);
		} catch (Exception e) {
			e.printStackTrace();
			throw LogReplayException.operationFailedException("Failed to create InspectionRecord!");
		}
	}
	
	@POST
	@Path("/resolve/{id}")
	public Response resolve(
			@PathParam("id") Long id
			) {
		InspectionRecord record = null;
		if(id == null || (record = inspectionRecordService.getInspectionRecordById(id)) == null) {
			throw LogReplayException.invalidParameterException(String.format("Id[%d] of InspectionRecord is invalid!", id));
		}
		try {
			User curUser = AuthUtil.getCurrentUser();
			record.setSolverId(curUser.getId());
			record.setSolved(true);
			inspectionRecordService.updateInspectionRecord(record);
			return successResultToJson("InspectionRecord is updated successfully!", true);
		} catch (Exception e) {
			e.printStackTrace();
			throw LogReplayException.operationFailedException(String.format("Failed to update InspectionRecord[%d]", record.getId()));
		}
		
	}

}
