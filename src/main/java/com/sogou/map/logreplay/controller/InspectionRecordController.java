package com.sogou.map.logreplay.controller;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sogou.map.logreplay.bean.InspectionRecord;
import com.sogou.map.logreplay.bean.PageInfo;
import com.sogou.map.logreplay.bean.Permission.Target;
import com.sogou.map.logreplay.bean.TagInfo;
import com.sogou.map.logreplay.bean.User;
import com.sogou.map.logreplay.controller.base.BaseController;
import com.sogou.map.logreplay.dao.base.Page;
import com.sogou.map.logreplay.dao.base.QueryParamMap;
import com.sogou.map.logreplay.dto.InspectionRecordDto;
import com.sogou.map.logreplay.exception.LogReplayException;
import com.sogou.map.logreplay.service.InspectionRecordService;
import com.sogou.map.logreplay.service.PageInfoService;
import com.sogou.map.logreplay.service.TagInfoService;
import com.sogou.map.logreplay.service.UserService;
import com.sogou.map.logreplay.util.AuthUtil;
import com.sogou.map.logreplay.util.ProductUtil;

@Controller
@RequestMapping("/inspectionRecord")
public class InspectionRecordController extends BaseController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PageInfoService pageInfoService;
	
	@Autowired
	private TagInfoService tagInfoService;
	
	@Autowired
	private InspectionRecordService inspectionRecordService;
	
	/**
	 * 获取校验结果的列表
	 */
	@ResponseBody
	@RequestMapping("/list")
	public ModelMap list(
			@RequestParam(defaultValue = Page.DEFAULT_START) int start,
			@RequestParam(defaultValue = Page.DEFAULT_LIMIT) int limit,
			String submitterName,
			Long submitterRoleId,
			String solverName,
			Long solverRoleId,
			Integer pageNo,
			Integer tagNo,
			Boolean valid,
			Boolean solved,
			String submitTimeSince,
			String submitTimeUntil
			) {
		List<Long> submitterIdList = null;
		if(StringUtils.isNotBlank(submitterName)) {
			submitterIdList = userService.getUserIdListResultByName(submitterName);
			if(CollectionUtils.isEmpty(submitterIdList)) {
				return successResult(Page.<InspectionRecordDto>emptyPage());
			}
		}
		List<Long> solverIdList = null;
		if(StringUtils.isNotBlank(solverName)) {
			solverIdList = userService.getUserIdListResultByName(solverName);
			if(CollectionUtils.isEmpty(solverIdList)) {
				return successResult(Page.<InspectionRecordDto>emptyPage());
			}
		}
		QueryParamMap params = new QueryParamMap()
			.addParam("productId", ProductUtil.getProductId())
			.addParam(submitterIdList != null, "submitterId__in", submitterIdList)
			.addParam(submitterRoleId != null, "submitterRoleId", submitterRoleId)
			.addParam(solverIdList != null, "solverId__in", solverIdList)
			.addParam(solverRoleId != null, "solverRoleId", solverRoleId)
			.addParam(pageNo != null && pageNo > 0, "pageNo", pageNo)
			.addParam(tagNo != null && tagNo > 0, "tagNo", tagNo)
			.addParam(valid != null, "valid", valid)
			.addParam(solved != null, "solved", solved)
			.addParam(StringUtils.isNotBlank(submitTimeSince), "createTime__ge", submitTimeSince)
			.addParam(StringUtils.isNotBlank(submitTimeUntil), "createTime__le", submitTimeUntil)
			.orderByDesc("createTime")
		;
		Page<InspectionRecordDto> page = InspectionRecordDto.from(inspectionRecordService.getInspectionRecordPaginateResultWithTransientFields(start, limit, params));
		return successResult(page);
	}
	
	@ResponseBody
	@RequestMapping("/detail/{id}")
	public ModelMap detail(@PathVariable("id") Long id) {
		InspectionRecord record = inspectionRecordService.getInspectionRecordById(id);
		return successResult(new InspectionRecordDto().from(record));
	}
	
	/**
	 * 提交校验结果，相当于创建
	 */
	@ResponseBody
	@RequestMapping(value = "/submit", method = RequestMethod.POST)
	public ModelMap submit(
			@RequestParam Integer pageNo,
			@RequestParam Integer tagNo,
			@RequestParam Boolean valid,
			@RequestParam(required = false) String comment
			) {
		if(!AuthUtil.isPermitted(Target.Inspection_Record.modify())) { 
			throw LogReplayException.unauthorizedException("Role of 'admin' or 'test' or 'dev' is required!");
		}
		if(pageNo == null || tagNo == null || valid == null) {
			throw LogReplayException.invalidParameterException("Parameters are invalid!");
		}
		PageInfo pageInfo = pageInfoService.getPageInfoByPageNoAndProductId(pageNo, ProductUtil.getProductId());
		if(pageInfo == null) {
			throw LogReplayException.invalidParameterException(String.format("PageInfo with pageNo[%d] does not exist!", pageNo));
		}
		TagInfo tagInfo = tagInfoService.getTagInfoByPageNoTagNoAndProductId(pageNo, tagNo, ProductUtil.getProductId());
		if(tagInfo == null) {
			throw LogReplayException.invalidParameterException(String.format("TagInfo with pageNo[%d] and tagNo[%d] does not exist!", pageNo, tagNo));
		}
		try {
			User curUser = AuthUtil.getCurrentUser();
			InspectionRecord record = new InspectionRecord(pageInfo.getId(), tagInfo.getId(), curUser.getId(), valid, comment);
			record.setTagInfo(tagInfo);
			record.setSubmitterRoleId(AuthUtil.getCurrentRoleId());
			inspectionRecordService.createInspectionRecord(record);
			return successResult("InspectionRecord is created successfully!");
		} catch (Exception e) {
			e.printStackTrace();
			throw LogReplayException.operationFailedException("Failed to create InspectionRecord!");
		}
	}
	
	/**
	 * 将校验结果标记为“已处理”
	 */
	@ResponseBody
	@RequestMapping(value = "/resolve/{id}", method = RequestMethod.POST)
	public Map<String, Object> resolve(@PathVariable("id") Long id) {
		if(!AuthUtil.isPermitted(Target.Inspection_Record.modify())) {
			throw LogReplayException.unauthorizedException("Role of 'admin' or 'test' or 'dev' is required!");
		}
		InspectionRecord record = null;
		if(id == null || (record = inspectionRecordService.getInspectionRecordById(id)) == null) {
			throw LogReplayException.invalidParameterException("Id[%d] of InspectionRecord is invalid!", id);
		}
		try {
			User curUser = AuthUtil.getCurrentUser();
			record.setSolverId(curUser.getId());
			record.setSolved(true);
			record.setSolverRoleId(AuthUtil.getCurrentRoleId());
			inspectionRecordService.updateInspectionRecord(record);
			return successResult("InspectionRecord is updated successfully!");
		} catch (Exception e) {
			e.printStackTrace();
			throw LogReplayException.operationFailedException("Failed to update InspectionRecord[%d]", record.getId());
		}
		
	}

}
