package com.sogou.map.logreplay.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sogou.map.logreplay.bean.OperationRecord;
import com.sogou.map.logreplay.bean.PageInfo;
import com.sogou.map.logreplay.bean.ParamInfo;
import com.sogou.map.logreplay.bean.Role;
import com.sogou.map.logreplay.bean.TagInfo;
import com.sogou.map.logreplay.dao.base.QueryParamMap;
import com.sogou.map.logreplay.dto.OperationRecordDto;
import com.sogou.map.logreplay.dto.OperationRecordDto.TagParamParsedResult;
import com.sogou.map.logreplay.exception.LogReplayException;
import com.sogou.map.logreplay.logprocess.log.MobLog;
import com.sogou.map.logreplay.logprocess.processor.MobLogProcessor;
import com.sogou.map.logreplay.logprocess.processor.OperationLogProcessor;
import com.sogou.map.logreplay.service.OperationRecordService;
import com.sogou.map.logreplay.service.PageInfoService;
import com.sogou.map.logreplay.service.TagInfoService;
import com.sogou.map.logreplay.service.TagParamService;
import com.sogou.map.logreplay.util.AuthUtil;
import com.sogou.map.logreplay.util.JsonUtil;
import com.sogou.map.logreplay.util.TagParamParser;
import com.sogou.map.logreplay.util.TagParamParser.ParamInfoHolder;
import com.sogou.map.mengine.common.service.BaseService;
import com.sogou.map.mengine.http.filter.AccessLoggerFilter;
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
	
	@Autowired
	private TagParamService tagParamService;
	
	@GET
	@Path("/latest")
	public Response getLatestRecord() {
		return successResultToJson(operationRecordService.getLatestOperationRecord(), true);
	}
	
	@GET
	@Path("/query")
	public Response query(
			@QueryParam("idSince") Long idSince,
			@QueryParam("since") Long since,
			@QueryParam("until") Long until,
			@QueryParam("deviceId") String deviceId,
			@QueryParam("uvid") String uvid,
			@QueryParam("pageNo") Integer pageNo,
			@QueryParam("tagNo") Integer tagNo,
			@QueryParam("originVersionSince") Integer originVersionSince,
			@QueryParam("originVersionUntil") Integer originVersionUntil,
			@DefaultValue("30") @QueryParam("limit") int limit
			) {
		List<OperationRecord> list = operationRecordService.getOperationRecordListResult(0, limit, new QueryParamMap()
			.addParam(StringUtils.isNotBlank(deviceId), "deviceId", deviceId)
			.addParam(StringUtils.isNotBlank(uvid), "uvid", uvid)
			.addParam(pageNo != null, "pageNo", pageNo)
			.addParam(tagNo != null, "tagNo", tagNo)
			.addParam(idSince != null, "id__gt", idSince)
			.addParam(idSince == null && since != null, "timestamp__gt", since)
			.addParam(until != null, "timestamp__lt", until)
			.addParam(originVersionSince != null && originVersionSince > 0, "tag_info.origin_version__ge", originVersionSince)
			.addParam(originVersionUntil != null && originVersionUntil > 0 , "tag_info.origin_version__le", originVersionUntil)
			.orderByAsc("timestamp")
		);
		List<OperationRecordDto> dtoList = convertToDtoList(list);
		findAndFillCommonTagInfo(dtoList);
		fillTagParamParsedResult(dtoList);
		return successResultToJson(dtoList, JsonUtil.configInstance(), true);
	}
	
	/**
	 * 填充tagNo在10000以上的tagInfo信息
	 * 这种tag不关联pageInfo
	 * operationRecord中的pageNo是多少，就填充对应的pageInfo
	 */
	private void findAndFillCommonTagInfo(List<OperationRecordDto> dtoList) {
		List<OperationRecordDto> dtoListWithCommonTag = new ArrayList<OperationRecordDto>();
		Set<Integer> tagNoSet = new HashSet<Integer>();
		Set<Integer> pageNoSet = new HashSet<Integer>();
		for(OperationRecordDto dto: dtoList) {
			if(dto.getTagNo() != null && dto.getTagNo() > TagInfo.COMMON_TAG_NO_MIN_VALUE) {
				dtoListWithCommonTag.add(dto);
				tagNoSet.add(dto.getTagNo());
				pageNoSet.add(dto.getPageNo());
			}
		}
		if(CollectionUtils.isEmpty(dtoListWithCommonTag)) {
			return;
		}
		Map<Integer, PageInfo> pageInfoMap = Maps.uniqueIndex(pageInfoService.getPageInfoListResult(new QueryParamMap()
			.addParam("pageNo__in", pageNoSet)
		), new Function<PageInfo, Integer>() {
			@Override
			public Integer apply(PageInfo pageInfo) {
				return pageInfo.getPageNo();
			}
		});
		Map<Integer, TagInfo> tagInfoMap = Maps.uniqueIndex(tagInfoService.getTagInfoListResult(new QueryParamMap()
			.addParam("tagNo__in", tagNoSet)
		), new Function<TagInfo, Integer>() {
			@Override
			public Integer apply(TagInfo tagInfo) {
				return tagInfo.getTagNo();
			}
		});
		for(OperationRecordDto dto: dtoListWithCommonTag) {
			PageInfo pageInfo = pageInfoMap.get(dto.getPageNo());
			if(pageInfo != null) {
				dto.setPageName(pageInfo.getName());
			}
			TagInfo tagInfo = tagInfoMap.get(dto.getTagNo());
			if(tagInfo != null) {
				dto.setTagInfoId(tagInfo.getId());
				dto.setTagName(tagInfo.getName());
				dto.setActionId(tagInfo.getActionId());
				dto.setTargetId(tagInfo.getTargetId());
				dto.setInspectStatus(tagInfo.getInspectStatus());
			}
		}
		
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
	
	public void fillTagParamParsedResult(List<OperationRecordDto> dtoList) {
		if(CollectionUtils.isEmpty(dtoList)) {
			return;
		}
		List<Long> tagInfoIdList = Lists.transform(dtoList, new Function<OperationRecordDto, Long>() {
			@Override
			public Long apply(OperationRecordDto dto) {
				return dto.getTagInfoId();
			}
		});
		TagParamParser parser = tagParamService.getTagParamParserByTagInfoIdList(tagInfoIdList);
		for(OperationRecordDto dto: dtoList) {
			String params = dto.getParams();
			if(StringUtils.isBlank(params)) {
				params = "{}";
			}
			JSONObject json = JSON.parseObject(params);
			List<String> requiredParamNameList = parser.getRequiredParamNameList(dto.getTagInfoId());
			for(String requiredParamName: requiredParamNameList) {
				if(!json.containsKey(requiredParamName)) {
					TagParamParsedResult lackOfParamResult = new TagParamParsedResult()
						.paramName(requiredParamName)
						.description("缺少参数!")
						.required(true)
						.valid(false);
					dto.addParamParsedResult(lackOfParamResult);
				}
			}
			for(Entry<String, Object> entry: json.entrySet()) {
				String key = entry.getKey();
				if(TagParamParser.isParamKeyExcluded(key)) {
					continue;
				}
				Object value = entry.getValue();
				if(value == null) {
					value = "";
				}
				ParamInfo paramInfo = parser.parse(dto.getTagInfoId(), key, value.toString());
				String description = paramInfo != null? paramInfo.getDescription(): "多余的参数!";
				TagParamParsedResult parsedResult = new TagParamParsedResult()
					.paramName(key).paramValue(value.toString())
					.description(description)
					.required(paramInfo != null)
					.valid(ParamInfoHolder.isValid(paramInfo));
				dto.addParamParsedResult(parsedResult);
			}
		}
	}
	
	/**
	 * 接收操作数据的接口
	 * 请求串格式如下
	 * http://127.0.0.1:8075/logreplay//operationRecord/receive?moblog=sid:,os:Android4%252e1%252e2,d:A00000408A5798,op:460%252d03,density:240,loginid:,net:wifi,vn:6%252e2%252e0,pd:1,v:60200000,u:1420989208035172,md:SCH%252dI829,bsns:807,openid:,mf:samsung,apn:&info={"key":"菜市场","tag":1,"p":4,"t":1421656262063}
	 */
	@GET
	@Path("/receive")
	public Response receiveDataViaGet(
			@QueryParam("moblog") String moblogStr,
			@QueryParam("info") String infoStr,
			@Context HttpServletRequest request) {
		return doReceiveData(moblogStr, infoStr, request);
	}
	
	@POST
	@Path("/receive")
	public Response receiveDataViaPost (
			@QueryParam("moblog") String moblogStr,
			@FormParam("info") String infoStr,
			@Context HttpServletRequest request) {
		return doReceiveData(moblogStr, infoStr, request);
	}
	
	private Response doReceiveData(String moblogStr, String infoStr, HttpServletRequest request) {
		MobLog moblog = new MobLogProcessor().process(moblogStr);
		if(StringUtils.isEmpty(moblog.getDeviceId()) || StringUtils.isEmpty(moblog.getVersion())) {
			throw LogReplayException.invalidParameterException("Invalid parameter of moblog!");
		}
		JSONObject info = JSON.parseObject(infoStr);
		if(MapUtils.isEmpty(info)) {
			throw LogReplayException.invalidParameterException("Invalid parameter of info!");
		}
		OperationRecord record = null;
		try {
			record = new OperationRecord.Builder()
				.ip(AccessLoggerFilter.getIpAddr(request))
				.deviceId(moblog.getDeviceId())
				.uvid(moblog.getUvid())
				.os(moblog.getOs())
				.version(moblog.getVersion())
				.timestamp(System.currentTimeMillis())
				.pageNo(info.getInteger("p"))
				.tagNo(info.getInteger("tag"))
				.params(info)
				.build();
			operationRecordService.saveOrUpdateOperationRecord(record);
		} catch (Exception e) {
			e.printStackTrace();
			throw LogReplayException.operationFailedException(String.format("Failed to save %s", record));
		}
		return successResultToJson("success", true);
	}
	
	@POST
	@Path("/upload/nginx")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadNginxLog(
			FormDataMultiPart multiPartData
			) {
		if(!AuthUtil.hasRole(Role.ADMIN)) {
			throw LogReplayException.unauthorizedException("Role[admin] is required!");
		}
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
			throw LogReplayException.operationFailedException("Operation Failed while uploading log data!");
		} finally {
			IOUtils.closeQuietly(reader);
		}
		return successResultToJson(new ModelMap("count", count), true);
	}
	
}
