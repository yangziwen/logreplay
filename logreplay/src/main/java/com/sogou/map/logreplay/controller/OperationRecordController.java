package com.sogou.map.logreplay.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
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
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sogou.map.logreplay.bean.OperationRecord;
import com.sogou.map.logreplay.bean.TagInfo;
import com.sogou.map.logreplay.dao.base.QueryParamMap;
import com.sogou.map.logreplay.dto.OperationRecordDto;
import com.sogou.map.logreplay.exception.LogReplayException;
import com.sogou.map.logreplay.logprocess.processor.OperationLogProcessor;
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
			@QueryParam("pageNo") Integer pageNo,
			@QueryParam("tagNo") Integer tagNo,
			@DefaultValue("30") @QueryParam("limit") int limit
			) {
		List<OperationRecord> list = operationRecordService.getOperationRecordListResult(0, limit, new QueryParamMap()
			.addParam(StringUtils.isNotBlank(deviceId), "deviceId", deviceId)
			.addParam(StringUtils.isNotBlank(uvid), "uvid", uvid)
			.addParam(pageNo != null, "pageNo", pageNo)
			.addParam(tagNo != null, "tagNo", tagNo)
			.addParam("timestamp__gt",since)
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
	
	@GET
	@Path("/import")
	public Response importData(
			@QueryParam("source") String sourcePath) {
		File source = null;
		if(StringUtils.isBlank(sourcePath) || !(source =  new File(sourcePath)).exists()) {
			throw LogReplayException.notExistException(String.format("File under the path [%s] does not exist!", sourcePath));
		}
		BufferedReader reader = null;
		String line = "";
		int count = 0;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(source), "UTF-8"));
			List<OperationRecord> recordList = new ArrayList<OperationRecord>(500);
			OperationLogProcessor processor = new OperationLogProcessor();
			while((line = reader.readLine()) != null) {
				recordList.addAll(processor.process(line).toRecordList());
				if(recordList.size() > 1000) {
					count += operationRecordService.batchSaveOrUpdateOperationRecord(recordList);
					recordList = new ArrayList<OperationRecord>();
				}
			}
			if(recordList.size() > 0) {
				count += operationRecordService.batchSaveOrUpdateOperationRecord(recordList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw LogReplayException.operationFailedException("Failed while importing log data!");
		} finally {
			IOUtils.closeQuietly(reader);
		}
		return successResultToJson(String.format("Successfully import [%d] records!", count), true);
	}
	
}
