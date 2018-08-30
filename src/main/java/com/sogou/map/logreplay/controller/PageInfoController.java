package com.sogou.map.logreplay.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.sogou.map.logreplay.bean.PageInfo;
import com.sogou.map.logreplay.bean.Permission.Target;
import com.sogou.map.logreplay.controller.base.BaseController;
import com.sogou.map.logreplay.dao.base.Page;
import com.sogou.map.logreplay.dao.base.QueryParamMap;
import com.sogou.map.logreplay.exception.LogReplayException;
import com.sogou.map.logreplay.service.PageInfoService;
import com.sogou.map.logreplay.util.AuthUtil;
import com.sogou.map.logreplay.util.ExcelUtil;
import com.sogou.map.logreplay.util.ExcelUtil.Column;
import com.sogou.map.logreplay.util.ProductUtil;

@Controller
@RequestMapping("/pageInfo")
public class PageInfoController extends BaseController {

	private static final List<Column> PAGE_INFO_COLUMN_LIST = buildPageInfoColumnList();

	@Autowired
	private PageInfoService pageInfoService;

	@ResponseBody
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Map<String, Object> list(
			@RequestParam(defaultValue = Page.DEFAULT_START) int start,
			@RequestParam(defaultValue = Page.DEFAULT_LIMIT) int limit,
			Integer pageNo,
			String pageName,
			String updateBeginTime,
			String updateEndTime
			) {
		Page<PageInfo> page = pageInfoService.getPageInfoPageResult(start, limit, new QueryParamMap()
			.addParam(pageNo != null, "pageNo", pageNo)
			.addParam(StringUtils.isNotBlank(pageName), "name__contain", pageName)
			.addParam(StringUtils.isNotBlank(updateBeginTime), "updateTime__ge", updateBeginTime)
			.addParam(StringUtils.isNotBlank(updateEndTime), "updateTime__le", updateEndTime)
			.addParam("productId", ProductUtil.getProductId())
			.orderByAsc("pageNo")
		);
		return successResult(page);
	}

	@ResponseBody
	@RequestMapping(value = "/export", method = RequestMethod.GET)
	public void export(
			Integer pageNo,
			String pageName,
			String updateBeginTime,
			String updateEndTime,
			HttpServletResponse response
			) throws UnsupportedEncodingException {
		List<PageInfo> list = pageInfoService.getPageInfoListResult(new QueryParamMap()
			.addParam(pageNo != null, "pageNo", pageNo)
			.addParam(StringUtils.isNotBlank(pageName), "name__contain", pageName)
			.addParam(StringUtils.isNotBlank(updateBeginTime), "updateTime__ge", updateBeginTime)
			.addParam(StringUtils.isNotBlank(updateEndTime), "updateTime__le", updateEndTime)
			.addParam("productId", ProductUtil.getProductId())
			.orderByAsc("pageNo")
		);
		Workbook workbook = ExcelUtil.exportDataList(PAGE_INFO_COLUMN_LIST, list);
		String filename = ProductUtil.getCurrentProduct().getName() + "_页面详情.xls";
		ExcelUtil.outputExcelToResponse(workbook, filename, response);
	}

	@ResponseBody
	@RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
	public Map<String, Object> detail(@PathVariable("id") Long id) {
		PageInfo info = pageInfoService.getPageInfoById(id);
		return successResult(info);
	}

	@ResponseBody
	@RequestMapping(value = "/detailByPageNo/{pageNo}", method = RequestMethod.GET)
	public Map<String, Object> detailByPageNo(@PathVariable("pageNo") Integer pageNo) {
		PageInfo info = pageInfoService.getPageInfoByPageNoAndProductId(pageNo, ProductUtil.getProductId());
		return successResult(info);
	}

	@ResponseBody
	@RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
	public Map<String, Object> update(@PathVariable("id") Long id,
			@RequestParam Integer pageNo,
			@RequestParam String name) {
		if(!AuthUtil.isPermitted(Target.Page_Info.modify())) {
			throw LogReplayException.unauthorizedException("Role[admin] is required!");
		}
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
			return successResult("PageInfo[%d] is updated successfully!", id);
		} catch (Exception e) {
			throw LogReplayException.operationFailedException("Failed to update PageInfo[%d]", id);
		}
	}

	@ResponseBody
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ModelMap create(
			@RequestParam Integer pageNo,
			@RequestParam String name
			) {
		if(!AuthUtil.isPermitted(Target.Page_Info.modify())) {
			throw LogReplayException.unauthorizedException("Role[admin] is required!");
		}
		if(pageNo == null || StringUtils.isBlank(name)) {
			throw LogReplayException.invalidParameterException("Parameters are invalid!");
		}
		try {
			PageInfo info = new PageInfo(pageNo, name);
			pageInfoService.createPageInfo(info);
			return successResult("PageInfo[%s] is created successfully!", info.getId());
		} catch (Exception e) {
			throw LogReplayException.operationFailedException("Failed to create PageInfo!");
		}
	}

	@ResponseBody
	@RequestMapping("/checkDuplication")
	public boolean checkDuplication(Long id, Integer pageNo) {
		if(pageNo == null || pageNo <= 0) {
			return false;
		}
		if(id == null && pageInfoService.getPageInfoListResult(0, 1, new QueryParamMap()
				.addParam("pageNo", pageNo)
				.addParam("productId", ProductUtil.getProductId())).size() > 0) {
			return false;
		}
		if(pageInfoService.getPageInfoListResult(0, 1, new QueryParamMap()
				.addParam("pageNo", pageNo)
				.addParam("productId", ProductUtil.getProductId())
				.addParam("id__ne", id)).size() > 0) {
			return false;
		}
		return true;
	}

	@ResponseBody
	@RequestMapping("/checkExist")
	public boolean checkExist(Integer pageNo) {
		if(pageNo == null || pageNo <= 0) {
			return false;
		}
		if(pageInfoService.getPageInfoByPageNoAndProductId(pageNo, ProductUtil.getProductId()) == null) {
			return false;
		}
		return true;
	}

	private static List<Column> buildPageInfoColumnList() {
		List<ExcelUtil.Column> columnList = Lists.newArrayList(
				ExcelUtil.column("页面编号", "pageNo", 3000, ExcelUtil.CellType.number),
				ExcelUtil.column("页面名称", "name", 8000, ExcelUtil.CellType.text)
		);
		return columnList;
	}

}
