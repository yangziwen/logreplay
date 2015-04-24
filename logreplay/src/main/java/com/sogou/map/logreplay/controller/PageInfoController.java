package com.sogou.map.logreplay.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.sogou.map.logreplay.bean.PageInfo;
import com.sogou.map.logreplay.bean.Role;
import com.sogou.map.logreplay.dao.base.Page;
import com.sogou.map.logreplay.dao.base.QueryParamMap;
import com.sogou.map.logreplay.exception.LogReplayException;
import com.sogou.map.logreplay.service.PageInfoService;
import com.sogou.map.logreplay.util.AuthUtil;
import com.sogou.map.logreplay.util.ExcelUtil;
import com.sogou.map.logreplay.util.ExcelUtil.Column;
import com.sogou.map.logreplay.util.JsonUtil;
import com.sogou.map.logreplay.util.ProductUtil;
import com.sogou.map.mengine.common.bo.ApiException;
import com.sogou.map.mengine.common.service.BaseService;

@Component
@Path("/pageInfo")
public class PageInfoController extends BaseService {
	
	private static final List<Column> PAGE_INFO_COLUMN_LIST = buildPageInfoColumnList();
	
	@Autowired
	private PageInfoService pageInfoService;

	@GET
	@Path("/list")
	public Response list(
			@DefaultValue(Page.DEFAULT_START) @QueryParam("start") int start,
			@DefaultValue(Page.DEFAULT_LIMIT) @QueryParam("limit") int limit,
			@QueryParam("pageNo") Integer pageNo,
			@QueryParam("pageName") String name,
			@QueryParam("updateBeginTime") String updateBeginTime,
			@QueryParam("updateEndTime") String updateEndTime
			) {
		Page<PageInfo> page = pageInfoService.getPageInfoPageResult(start, limit, new QueryParamMap()
			.addParam(pageNo != null, "pageNo", pageNo)
			.addParam(StringUtils.isNotBlank(name), "name__contain", name)
			.addParam(StringUtils.isNotBlank(updateBeginTime), "updateTime__ge", updateBeginTime)
			.addParam(StringUtils.isNotBlank(updateEndTime), "updateTime__le", updateEndTime)
			.addParam("productId", ProductUtil.getProductId())
			.orderByAsc("pageNo")
		);
		return successResultToJson(page, JsonUtil.configInstance(), true);
	}
	
	@GET
	@Path("/export")
	public Response export(
			@QueryParam("pageNo") Integer pageNo,
			@QueryParam("pageName") String name,
			@QueryParam("updateBeginTime") String updateBeginTime,
			@QueryParam("updateEndTime") String updateEndTime
			) throws UnsupportedEncodingException {
		List<PageInfo> list = pageInfoService.getPageInfoListResult(new QueryParamMap()
			.addParam(pageNo != null, "pageNo", pageNo)
			.addParam(StringUtils.isNotBlank(name), "name__contain", name)
			.addParam(StringUtils.isNotBlank(updateBeginTime), "updateTime__ge", updateBeginTime)
			.addParam(StringUtils.isNotBlank(updateEndTime), "updateTime__le", updateEndTime)
			.addParam("productId", ProductUtil.getProductId())
			.orderByAsc("pageNo")
		);
		Workbook workbook = ExcelUtil.exportDataList(PAGE_INFO_COLUMN_LIST, list);
		String filename = ProductUtil.getCurrentProduct().getName() + "_页面详情.xls";
		return ExcelUtil.generateExcelResponse(workbook, filename);
	}
	
	@GET
	@Path("/detail/{id}")
	public Response detail(@PathParam("id") Long id) {
		PageInfo info = pageInfoService.getPageInfoById(id);
		return successResultToJson(info, JsonUtil.configInstance(), true);
	}
	
	@GET
	@Path("/detailByPageNo/{pageNo}")
	public Response detailByPageNo(@PathParam("pageNo") Integer pageNo) {
		PageInfo info = pageInfoService.getPageInfoByPageNoAndProductId(pageNo, ProductUtil.getProductId());
		return successResultToJson(info, JsonUtil.configInstance(), true);
	}
	
	@POST
	@Path("/update/{id}")
	public Response update(@PathParam("id") Long id,
			@FormParam("pageNo") Integer pageNo,
			@FormParam("name") String name) throws ApiException {
		if(!AuthUtil.hasRole(Role.ADMIN)) {
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
			return successResultToJson(String.format("PageInfo[%d] is updated successfully!", id), true);
		} catch (Exception e) {
			throw LogReplayException.operationFailedException(String.format("Failed to update PageInfo[%d]", id));
		}
	}
	
	@POST
	@Path("/create")
	public Response create(
			@FormParam("pageNo") Integer pageNo,
			@FormParam("name") String name
			) throws ApiException {
		if(!AuthUtil.hasRole(Role.ADMIN)) {
			throw LogReplayException.unauthorizedException("Role[admin] is required!");
		}
		if(pageNo == null || StringUtils.isBlank(name)) {
			throw LogReplayException.invalidParameterException("Parameters are invalid!");
		}
		try {
			PageInfo info = new PageInfo(pageNo, name);
			pageInfoService.createPageInfo(info);
			return successResultToJson(String.format("PageInfo[%s] is created successfully!", info.getId()), true);
		} catch (Exception e) {
			throw LogReplayException.operationFailedException("Failed to create PageInfo!");
		}
	}
	
	@GET
	@Path("/checkDuplication")
	public Response checkDuplication(
			@QueryParam("id") Long id,
			@QueryParam("pageNo") Integer pageNo) {
		if(pageNo == null || pageNo <= 0) {
			return Response.ok().entity("false").build();
		}
		if(id == null && pageInfoService.getPageInfoListResult(0, 1, new QueryParamMap()
				.addParam("pageNo", pageNo)
				.addParam("productId", ProductUtil.getProductId())).size() > 0) {
			return Response.ok().entity("false").build();
		}
		if(pageInfoService.getPageInfoListResult(0, 1, new QueryParamMap()
				.addParam("pageNo", pageNo)
				.addParam("productId", ProductUtil.getProductId())
				.addParam("id__ne", id)).size() > 0) {
			return Response.ok().entity("false").build();
		}
		return Response.ok().entity("true").build();
	}
	
	@GET
	@Path("/checkExist")
	public Response checkExist(@QueryParam("pageNo") Integer pageNo) {
		if(pageNo == null || pageNo <= 0) {
			return Response.ok().entity("false").build();
		}
		if(pageInfoService.getPageInfoByPageNoAndProductId(pageNo, ProductUtil.getProductId()) == null) {
			return Response.ok().entity("false").build();
		}
		return Response.ok().entity("true").build();
	}
	
	private static List<Column> buildPageInfoColumnList() {
		List<ExcelUtil.Column> columnList = Lists.newArrayList(
				ExcelUtil.column("页面编号", "pageNo", 3000, ExcelUtil.CellType.number),
				ExcelUtil.column("页面名称", "name", 8000, ExcelUtil.CellType.text)
		);
		return columnList;
	}
	
}
