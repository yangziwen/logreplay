package com.sogou.map.logreplay.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

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
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;

@Component
@Path("/operationRecord")
public class OperationRecordController extends BaseService {
	
	private static final Logger logger = LoggerFactory.getLogger(OperationRecordController.class);

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
			@QueryParam("until") Long until,
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
			.addParam(since != null, "timestamp__gt",since)
			.addParam(until != null, "timestamp__lt", until)
			.orderByAsc("timestamp")
		);
		List<OperationRecordDto> dtoList = convertToDtoList(list);
		return successResultToJson(dtoList, JsonUtil.configInstance(), true);
	}
	
	private List<OperationRecordDto> convertToDtoList(List<OperationRecord> list) {
		if(CollectionUtils.isEmpty(list)) {
			return Collections.emptyList();
		}
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
					recordList = new ArrayList<OperationRecord>(500);
				}
			}
			if(recordList.size() > 0) {
				count += operationRecordService.batchSaveOrUpdateOperationRecord(recordList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw LogReplayException.operationFailedException("Operation failed while importing log data!");
		} finally {
			IOUtils.closeQuietly(reader);
		}
		return successResultToJson(String.format("Successfully import [%d] records!", count), true);
	}
	
	@POST
	@Path("/upload/nginx")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadNginxLog(
			FormDataMultiPart multiPartData
			) {
		FormDataBodyPart filePart = multiPartData.getField("file");
		InputStream in = filePart.getValueAs(InputStream.class);
		BufferedReader reader = null;
		String line = "";
		int count = 0;
		try {
			reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			List<OperationRecord> recordList = new ArrayList<OperationRecord>(500);
			OperationLogProcessor processor = new OperationLogProcessor();
			while(true) {
				try {
					line = reader.readLine();
				} catch (Exception e) {
					logger.warn(e.getMessage());
					line = null;
				}
				if(line == null) {
					break;
				}
				recordList.addAll(processor.process(line).toRecordList());
				if(recordList.size() > 1000) {
					count += operationRecordService.batchSaveOrUpdateOperationRecord(recordList);
					recordList = new ArrayList<OperationRecord>(500);
				}
			}
			if(recordList.size() > 0) {
				count += operationRecordService.batchSaveOrUpdateOperationRecord(recordList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw LogReplayException.operationFailedException("Operation Failed while importing log data!");
		} finally {
			IOUtils.closeQuietly(reader);
		}
		return successResultToJson(new ModelMap("count", count), true);
	}
	
}
