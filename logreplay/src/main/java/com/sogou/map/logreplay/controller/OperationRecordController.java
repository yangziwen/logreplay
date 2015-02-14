package com.sogou.map.logreplay.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sogou.map.logreplay.bean.OperationRecord;
import com.sogou.map.logreplay.bean.TagInfo;
import com.sogou.map.logreplay.dao.base.QueryParamMap;
import com.sogou.map.logreplay.dto.OperationRecordDto;
import com.sogou.map.logreplay.service.OperationRecordService;
import com.sogou.map.logreplay.service.PageInfoService;
import com.sogou.map.logreplay.service.TagInfoService;
import com.sogou.map.logreplay.util.JsonUtil;
import com.sogou.map.mengine.common.service.BaseService;

@Component
@Path("/operationRecord")
public class OperationRecordController extends BaseService {

	@Autowired
	private OperationRecordService operationRecordService;
	
	@Autowired
	private PageInfoService pageInfoService;
	
	@Autowired
	private TagInfoService tagInfoService;
	
	@GET
	@Path("/query")
	public Response query(
			@QueryParam("since") Long since,
			@QueryParam("deviceId") String deviceId,
			@QueryParam("uvid") String uvid,
			@DefaultValue("30") @QueryParam("limit") int limit
			) {
		List<OperationRecord> list = operationRecordService.getOperationRecordListResult(0, limit, new QueryParamMap()
			.addParam(StringUtils.isNotBlank(deviceId), "deviceId", deviceId)
			.addParam(StringUtils.isNotBlank(uvid), "uvid", uvid)
			.addParam("timestamp__ge", new Timestamp(since))
			.orderByAsc("timestamp")
		);
		List<OperationRecordDto> dtoList = convertToDtoList(list);
		return successResultToJson(dtoList, JsonUtil.configInstance(), true);
	}
	
	private List<OperationRecordDto> convertToDtoList(List<OperationRecord> list) {
		List<TagInfo> tagInfoList = tagInfoService.getTagInfoListResult(new QueryParamMap()
			.addParam("id__in", collectTagInfoId(list))
		);
		Map<Integer, Map<Integer, TagInfo>> dict = new HashMap<Integer, Map<Integer,TagInfo>>();
		for(TagInfo tagInfo: tagInfoList) {
			Map<Integer, TagInfo> subDict = dict.get(tagInfo.getPageNo());
			if(subDict == null) {
				subDict = new HashMap<Integer, TagInfo>();
				dict.put(tagInfo.getPageNo(), subDict);
			}
			subDict.put(tagInfo.getTagNo(), tagInfo);
		}
		List<OperationRecordDto> dtoList = new ArrayList<OperationRecordDto>();
		for(OperationRecord record: list) {
			TagInfo tagInfo = null;
			if(dict.get(record.getPageNo()) != null) {
				tagInfo = dict.get(record.getPageNo()).get(record.getTagNo());
			}
			dtoList.add(OperationRecordDto.from(record, tagInfo));
		}
		return dtoList;
	}
	
	private Set<Long> collectTagInfoId(List<OperationRecord> list) {
		if(CollectionUtils.isEmpty(list)) {
			return Collections.emptySet();
		}
		Set<Long> tagInfoIdSet = new HashSet<Long>();
		for(OperationRecord record: list) {
			tagInfoIdSet.add(record.getTagInfoId());
		}
		return tagInfoIdSet;
	}
	
}
