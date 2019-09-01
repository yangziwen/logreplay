package io.github.yangziwen.logreplay.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

import io.github.yangziwen.logreplay.bean.PageInfo;
import io.github.yangziwen.logreplay.bean.TagAction;
import io.github.yangziwen.logreplay.bean.TagInfo;
import io.github.yangziwen.logreplay.bean.TagInfo.InspectStatus;
import io.github.yangziwen.logreplay.bean.TagParam;
import io.github.yangziwen.logreplay.bean.TagTarget;
import io.github.yangziwen.logreplay.controller.base.BaseController;
import io.github.yangziwen.logreplay.dao.base.Page;
import io.github.yangziwen.logreplay.dao.base.QueryParamMap;
import io.github.yangziwen.logreplay.dto.TagInfoDto;
import io.github.yangziwen.logreplay.exception.LogReplayException;
import io.github.yangziwen.logreplay.service.PageInfoService;
import io.github.yangziwen.logreplay.service.TagActionService;
import io.github.yangziwen.logreplay.service.TagInfoService;
import io.github.yangziwen.logreplay.service.TagParamService;
import io.github.yangziwen.logreplay.service.TagTargetService;
import io.github.yangziwen.logreplay.util.ExcelUtil;
import io.github.yangziwen.logreplay.util.ExcelUtil.CellType;
import io.github.yangziwen.logreplay.util.ExcelUtil.Column;
import io.github.yangziwen.logreplay.util.ProductUtil;
import io.github.yangziwen.logreplay.util.TagFields;

@RestController
@RequestMapping("/tagInfo")
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

	@GetMapping("/list")
	@RequiresPermissions("tag_info:view")
	public ModelMap list(
			@RequestParam(defaultValue = Page.DEFAULT_START) int start,
			@RequestParam(defaultValue = Page.DEFAULT_LIMIT) int limit,
			Integer pageNo,
			Integer tagNo,
			String pageName,
			String tagName,
			String updateBeginTime,
			String updateEndTime,
			Boolean isCommonTag,
			Integer originVersionSince,
			Integer originVersionUntil,
			@RequestParam(value = "inspectStatus", required = false)
			String inspectStatusStr,
			@RequestParam(value = "devInspectStatus", required = false)
			String devInspectStatusStr
			) {
		Page<TagInfo> page = tagInfoService.getTagInfoPageResult(start, limit, buildQueryParamMap(
				pageNo, tagNo, pageName, tagName, updateBeginTime, updateEndTime,
				isCommonTag, originVersionSince, originVersionUntil, inspectStatusStr, devInspectStatusStr)
		);
		fillHasParamsFlag(page.getList());
		return successResult(page);
	}

	/**
	 * 为tagInfo添加是否有参数(tagParam)的标识
	 */
	private void fillHasParamsFlag(List<TagInfo> tagInfoList) {
		if (CollectionUtils.isEmpty(tagInfoList)) {
			return;
		}
		Set<Long> tagInfoIdSet = new HashSet<Long>();
		for (TagInfo tagInfo: tagInfoList) {
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
		for (TagInfo tagInfo: tagInfoList) {
			tagInfo.setHasParams(tagInfoIdsWithParam.contains(tagInfo.getId()));
		}

	}

	@GetMapping("/detail/{id}")
	@RequiresPermissions("tag_info:view")
	public ModelMap detail(@PathVariable("id") Long id) {
		TagInfo tagInfo = tagInfoService.getTagInfoById(id);
		return successResult(tagInfo);
	}

	@GetMapping("/detailByPageNoAndTagNo/{pageNo}/{tagNo}")
	@RequiresPermissions("tag_info:view")
	public ModelMap detailByPageNoAndTagNo(
			@PathVariable("pageNo") Integer pageNo,
			@PathVariable("tagNo") Integer tagNo) {
		TagInfo tagInfo = tagInfoService.getTagInfoByPageNoTagNoAndProductId(pageNo, tagNo, ProductUtil.getProductId());
		return successResult(tagInfo);
	}

	@PostMapping("/update")
	@RequiresPermissions("tag_info:modify")
	public ModelMap update(
			@RequestParam Long id,
			@RequestParam Integer tagNo,
			@RequestParam String name,
			@RequestParam(required = false) Long pageInfoId,
			@RequestParam Long actionId,
			@RequestParam Long targetId,
			@RequestParam Integer originVersion,
			@RequestParam(required = false) String comment
			) {
		boolean needPageInfo = tagNo < TagInfo.COMMON_TAG_NO_MIN_VALUE;
		if (StringUtils.isBlank(name)
				|| tagNo == null
				|| (pageInfoId == null && needPageInfo)
				|| actionId == null
				|| targetId == null
				|| originVersion == null || originVersion <= 0) {
			throw LogReplayException.invalidParameterException("Parameters are invalid!");
		}
		PageInfo pageInfo = needPageInfo ? pageInfoService.getPageInfoById(pageInfoId) : null;
		if (pageInfo == null && needPageInfo) {
			throw LogReplayException.invalidParameterException(String.format("PageInfo[%d] does not exist!", pageInfoId));
		}
		TagInfo tagInfo = tagInfoService.getTagInfoById(id);
		if (tagInfo == null) {
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
			return successResult("TagInfo[%d] is updated successfully!", id);
		} catch (Exception e) {
			throw LogReplayException.operationFailedException("Failed to update TagInfo[%d]", id);
		}

	}

	@PostMapping("/create")
	@RequiresPermissions("tag_info:modify")
	public ModelMap create(
			@RequestParam Integer tagNo,
			@RequestParam String name,
			@RequestParam Long pageInfoId,
			@RequestParam Long actionId,
			@RequestParam Long targetId,
			@RequestParam Integer originVersion,
			@RequestParam(required = false) String comment
			) {
		boolean needPageInfo = tagNo < TagInfo.COMMON_TAG_NO_MIN_VALUE;
		if (StringUtils.isBlank(name)
				|| tagNo == null
				|| (pageInfoId == null && needPageInfo)
				|| actionId == null
				|| targetId == null
				|| originVersion == null || originVersion <= 0) {
			throw LogReplayException.invalidParameterException("Parameters are invalid!");
		}
		PageInfo pageInfo = needPageInfo ? pageInfoService.getPageInfoById(pageInfoId) : null;
		if (pageInfo == null && needPageInfo) {
			throw LogReplayException.invalidParameterException("PageInfo[%d] does not exist!", pageInfoId);
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
			return successResult("TagInfo[%d] is created successfully!", tagInfo.getId());
		} catch (Exception e) {
			throw LogReplayException.operationFailedException("Failed to create TagInfo!");
		}

	}

	@PostMapping("/delete")
	@RequiresPermissions("tag_info:modify")
	public ModelMap delete(@RequestParam("id") Long id) {
		if (id == null || id <= 0) {
			throw LogReplayException.invalidParameterException("Parameters are invalid!");
		}
		try {
			tagInfoService.deleteTagInfoById(id);
			return successResult("TagInfo[%d] is deleted successfully!", id);
		} catch (Exception e) {
			throw LogReplayException.operationFailedException("Failed to delete TagInfo[%d]!", id);
		}
	}

	@GetMapping("/checkDuplication")
	public boolean checkDuplication(Long id, Integer tagNo, Long pageInfoId) {
		if (tagNo == null || tagNo <= 0) {
			return false;
		}
		if (id == null && tagInfoService.getTagInfoListResult(0, 1, new QueryParamMap()
				.addParam("productId", ProductUtil.getProductId())
				.addParam(pageInfoId != null, "pageInfoId", pageInfoId)
				.addParam("tagNo", tagNo)).size() > 0) {
			return false;
		}
		if (tagInfoService.getTagInfoListResult(0, 1, new QueryParamMap()
				.addParam("productId", ProductUtil.getProductId())
				.addParam(pageInfoId != null, "pageInfoId", pageInfoId)
				.addParam("tagNo", tagNo)
				.addParam("id__ne", id)).size() > 0) {
			return false;
		}
		return true;
	}

	@GetMapping("/checkExist")
	public boolean checkExist(Integer pageNo, Integer tagNo) {
		if (tagNo == null || (tagNo < TagInfo.COMMON_TAG_NO_MIN_VALUE && pageNo == null)) {
			return false;
		}
		if (tagInfoService.getTagInfoByPageNoTagNoAndProductId(pageNo, tagNo, ProductUtil.getProductId()) == null) {
			return false;
		}
		return true;
	}

	@GetMapping("/export")
	@RequiresPermissions("tag_info:view")
	public void exportTagInfos(
			Integer pageNo,
			Integer tagNo,
			String pageName,
			String tagName,
			String updateBeginTime,
			String updateEndTime,
			Boolean isCommonTag,
			Integer originVersionSince,
			Integer originVersionUntil,
			@RequestParam(value = "inspectStatus", required = false) String inspectStatusStr,
			@RequestParam(value = "devInspectStatus", required = false) String devInspectStatusStr,
			HttpServletResponse response
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
		ExcelUtil.outputExcelToResponse(workbook, filename, response);
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
		if (StringUtils.isNotBlank(pageName)) {
			List<PageInfo> pageInfoList = pageInfoService.getPageInfoListResult(new QueryParamMap()
				.addParam("name__contain", pageName)
				.addParam("productId", ProductUtil.getProductId())
			);
			for (PageInfo pageInfo: pageInfoList) {
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
			.addParam(Boolean.FALSE.equals(isCommonTag), "page_info_id__is_not_null")
			.addParam(Boolean.TRUE.equals(isCommonTag), "page_info_id__is_null")
			.addParam(originVersionSince != null && originVersionSince > 0, "originVersion__ge", originVersionSince)
			.addParam(originVersionUntil != null && originVersionUntil > 0 , "originVersion__le", originVersionUntil)
			.addParam(inspectStatus != InspectStatus.UNKNOWN, "inspectStatus", inspectStatus.getIntValue())
			.addParam(devInspectStatus != InspectStatus.UNKNOWN, "devInspectStatus", devInspectStatus.getIntValue())
			.orderByAsc("page_info.page_no").orderByAsc("tagNo");
	}

	private static List<Column> buildTagInfoColumnList(boolean isCommonTag) {
		List<Column> columnList = new ArrayList<Column>();
		if (!isCommonTag) {
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

	@ResponseBody
	@RequestMapping(value = "/import", method = RequestMethod.POST)
	@RequiresPermissions("tag_info:modify")
	public ModelMap importTagInfos(MultipartFile file) throws IOException {
		List<Map<String, String>> mapList = ExcelUtil.importMapList(file.getInputStream());
		List<TagInfoDto> dtoList = TagFields.convertToTagInfoDtoList(mapList);
		int count = importTagInfoByDtoList(dtoList);
		return successResult(new ModelMap("count", count));
	}

	public int importTagInfoByDtoList(List<TagInfoDto> dtoList) {
		Map<Integer, PageInfo> pageInfoMap = Maps.newHashMap();
		pageInfoMap.put(0, new PageInfo());			// 遇到“公共操作项”时，pageNo会被解析为0
		pageInfoMap.put(null, new PageInfo());		// 插入个空key，以防万一

		Map<String, TagAction> actionMap = Maps.newHashMap();
		List<TagAction> actionList = tagActionService.getTagActionListResult();
		for (TagAction action: actionList) {
			actionMap.put(action.getName(), action);
		}

		Map<String, TagTarget> targetMap = Maps.newHashMap();
		List<TagTarget> targetList = tagTargetService.getTagTargetListResult();
		for (TagTarget target: targetList) {
			targetMap.put(target.getName(), target);
		}

		int cnt = 0;
		for (TagInfoDto tagInfoDto: dtoList) {
			if (createTagInfoByDto(tagInfoDto, pageInfoMap, actionMap, targetMap)) {
				cnt ++;
			}
		}
		return cnt;
	}

	private PageInfo getOrCreatePageInfoIfNotExist(Integer pageNo, String pageName) {
		if (pageNo <= 0) {
			return null;
		}
		PageInfo pageInfo = pageInfoService.getPageInfoByPageNoAndProductId(pageNo, ProductUtil.getProductId());
		if (pageInfo == null) {
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

		if (tagInfoService.getTagInfoByPageNoTagNoAndProductId(
				tagInfoDto.getPageNo(),
				tagInfoDto.getTagNo(),
				ProductUtil.getProductId()) != null) {
			return false;
		}

		if (!pageInfoMap.containsKey(tagInfoDto.getPageNo())) {
			PageInfo pageInfo = getOrCreatePageInfoIfNotExist(tagInfoDto.getPageNo(), tagInfoDto.getPageName());
			if (pageInfo != null) {
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
		if (tagParam != null) {
			tagParam.setTagInfoId(tagInfo.getId());
			tagParamService.renewTagParamAndParamInfo(tagParam, tagParam.getParamInfoList());
		}

		return true;
	}

}
