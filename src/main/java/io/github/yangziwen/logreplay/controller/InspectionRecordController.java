package io.github.yangziwen.logreplay.controller;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.yangziwen.logreplay.bean.InspectionRecord;
import io.github.yangziwen.logreplay.bean.PageInfo;
import io.github.yangziwen.logreplay.bean.Permission.Target;
import io.github.yangziwen.logreplay.bean.TagInfo;
import io.github.yangziwen.logreplay.bean.User;
import io.github.yangziwen.logreplay.controller.base.BaseController;
import io.github.yangziwen.logreplay.dao.base.Page;
import io.github.yangziwen.logreplay.dao.base.QueryParamMap;
import io.github.yangziwen.logreplay.dto.InspectionRecordDto;
import io.github.yangziwen.logreplay.exception.LogReplayException;
import io.github.yangziwen.logreplay.service.InspectionRecordService;
import io.github.yangziwen.logreplay.service.PageInfoService;
import io.github.yangziwen.logreplay.service.TagInfoService;
import io.github.yangziwen.logreplay.service.UserService;
import io.github.yangziwen.logreplay.util.AuthUtil;
import io.github.yangziwen.logreplay.util.ProductUtil;

@RestController
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
	@GetMapping("/list")
	@RequiresPermissions("inspection_record:view")
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
		if (StringUtils.isNotBlank(submitterName)) {
			submitterIdList = userService.getUserIdListResultByName(submitterName);
			if (CollectionUtils.isEmpty(submitterIdList)) {
				return successResult(Page.<InspectionRecordDto>emptyPage());
			}
		}
		List<Long> solverIdList = null;
		if (StringUtils.isNotBlank(solverName)) {
			solverIdList = userService.getUserIdListResultByName(solverName);
			if (CollectionUtils.isEmpty(solverIdList)) {
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

	@GetMapping("/detail/{id}")
	@RequiresPermissions("inspection_record:view")
	public ModelMap detail(@PathVariable("id") Long id) {
		InspectionRecord record = inspectionRecordService.getInspectionRecordById(id);
		return successResult(new InspectionRecordDto().from(record));
	}

	/**
	 * 提交校验结果，相当于创建
	 */
	@PostMapping("/submit")
	@RequiresPermissions("inspection_record:modify")
	public ModelMap submit(
			@RequestParam Integer pageNo,
			@RequestParam Integer tagNo,
			@RequestParam Boolean valid,
			@RequestParam(required = false) String comment
			) {
		if (!AuthUtil.isPermitted(Target.Inspection_Record.modify())) {
			throw LogReplayException.unauthorizedException("Role of 'admin' or 'test' or 'dev' is required!");
		}
		if (pageNo == null || tagNo == null || valid == null) {
			throw LogReplayException.invalidParameterException("Parameters are invalid!");
		}
		PageInfo pageInfo = pageInfoService.getPageInfoByPageNoAndProductId(pageNo, ProductUtil.getProductId());
		if (pageInfo == null) {
			throw LogReplayException.invalidParameterException(String.format("PageInfo with pageNo[%d] does not exist!", pageNo));
		}
		TagInfo tagInfo = tagInfoService.getTagInfoByPageNoTagNoAndProductId(pageNo, tagNo, ProductUtil.getProductId());
		if (tagInfo == null) {
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
			logger.error("error happens when submit inspection record with pageNo[{}] and tagNo[{}]", pageNo, tagNo, e);
			throw LogReplayException.operationFailedException("Failed to create InspectionRecord!");
		}
	}

	/**
	 * 将校验结果标记为“已处理”
	 */
	@PostMapping("/resolve/{id}")
	@RequiresPermissions("inspection_record:modify")
	public Map<String, Object> resolve(@PathVariable("id") Long id) {
		if (!AuthUtil.isPermitted(Target.Inspection_Record.modify())) {
			throw LogReplayException.unauthorizedException("Role of 'admin' or 'test' or 'dev' is required!");
		}
		InspectionRecord record = null;
		if (id == null || (record = inspectionRecordService.getInspectionRecordById(id)) == null) {
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
			logger.error("failed to resolve inspection record[{}]", id, e);
			throw LogReplayException.operationFailedException("Failed to update InspectionRecord[%d]", record.getId());
		}

	}

}
