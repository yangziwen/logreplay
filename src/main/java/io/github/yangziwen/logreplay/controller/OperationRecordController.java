package io.github.yangziwen.logreplay.controller;

import java.io.BufferedReader;
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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.AsyncEventBus;

import io.github.yangziwen.logreplay.bean.OperationRecord;
import io.github.yangziwen.logreplay.bean.PageInfo;
import io.github.yangziwen.logreplay.bean.ParamInfo;
import io.github.yangziwen.logreplay.bean.Permission.Target;
import io.github.yangziwen.logreplay.bean.TagInfo;
import io.github.yangziwen.logreplay.controller.base.BaseController;
import io.github.yangziwen.logreplay.dao.base.Page;
import io.github.yangziwen.logreplay.dao.base.QueryParamMap;
import io.github.yangziwen.logreplay.dto.OperationRecordDto;
import io.github.yangziwen.logreplay.dto.OperationRecordDto.TagParamParsedResult;
import io.github.yangziwen.logreplay.exception.LogReplayException;
import io.github.yangziwen.logreplay.logprocess.log.MobLog;
import io.github.yangziwen.logreplay.logprocess.processor.MobLogProcessor;
import io.github.yangziwen.logreplay.logprocess.processor.OperationLogProcessor;
import io.github.yangziwen.logreplay.service.OperationRecordService;
import io.github.yangziwen.logreplay.service.PageInfoService;
import io.github.yangziwen.logreplay.service.TagInfoService;
import io.github.yangziwen.logreplay.service.TagParamService;
import io.github.yangziwen.logreplay.util.AuthUtil;
import io.github.yangziwen.logreplay.util.IPUtil;
import io.github.yangziwen.logreplay.util.ProductUtil;
import io.github.yangziwen.logreplay.util.TagParamParser;
import io.github.yangziwen.logreplay.util.TagParamParser.ParamInfoHolder;

@Controller
@RequestMapping("/operationRecord")
public class OperationRecordController extends BaseController {

	@Autowired
	private OperationRecordService operationRecordService;

	@Autowired
	private PageInfoService pageInfoService;

	@Autowired
	private TagInfoService tagInfoService;

	@Autowired
	private TagParamService tagParamService;

	@Autowired
	private AsyncEventBus eventBus;

	/**
	 * 获取数据库中最新的一条操作记录
	 * 用于在实时校验(回放)开始时确定第一次轮询的idSince的值
	 */
	@ResponseBody
	@RequestMapping("/latest")
	public ModelMap getLatestRecord() {
		return successResult(operationRecordService.getLatestOperationRecord());
	}

	/**
	 * 获取操作记录
	 * 用于实时校验(回放)
	 */
	@ResponseBody
	@RequestMapping("/query")
	@RequiresPermissions("operation_record:view")
	public ModelMap query(
			Long idSince,
			Long since,
			Long until,
			String deviceId,
			String uvid,
			Integer pageNo,
			Integer tagNo,
			Integer originVersionSince,
			Integer originVersionUntil,
			@RequestParam(defaultValue = Page.DEFAULT_LIMIT) int limit
			) {
		List<OperationRecord> list = operationRecordService.getOperationRecordListResult(0, limit, new QueryParamMap()
			.addParam("productId", ProductUtil.getProductId())
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
		return successResult(dtoList);
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
			.addParam("productId", ProductUtil.getProductId())
			.addParam("pageNo__in", pageNoSet)
		), new Function<PageInfo, Integer>() {
			@Override
			public Integer apply(PageInfo pageInfo) {
				return pageInfo.getPageNo();
			}
		});
		Map<Integer, TagInfo> tagInfoMap = Maps.uniqueIndex(tagInfoService.getTagInfoListResult(new QueryParamMap()
			.addParam("productId", ProductUtil.getProductId())
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
	 * http://127.0.0.1:8075/operationRecord/receive?moblog=sid:,os:Android4%252e1%252e2,d:A00000408A5798,op:460%252d03,density:240,loginid:,net:wifi,vn:6%252e2%252e0,pd:1,v:60200000,u:1420989208035172,md:SCH%252dI829,bsns:807,openid:,mf:samsung,apn:&info={"key":"菜市场","tag":1,"p":4,"t":1421656262063}
	 */
	@ResponseBody
	@RequestMapping(value = "/receive", method = RequestMethod.GET)
	public ModelMap receiveDataViaGet(
			@RequestParam("moblog") String moblogStr,
			@RequestParam("info") String infoStr,
			HttpServletRequest request) {
		return doReceiveData(moblogStr, infoStr, request);
	}

	@ResponseBody
	@RequestMapping(value = "/receive", method = RequestMethod.POST)
	public ModelMap receiveDataViaPost (
			@RequestParam("moblog") String moblogStr,
			@RequestParam("info") String infoStr,
			HttpServletRequest request) {
		return doReceiveData(moblogStr, infoStr, request);
	}

	private ModelMap doReceiveData(String moblogStr, String infoStr, HttpServletRequest request) {
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
				.ip(IPUtil.getIpAddr(request))
				.productId(moblog.getProductId())
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
			postOperationRecord(record);
		} catch (Exception e) {
			logger.error("error happens when receive data", e);
			throw LogReplayException.operationFailedException("Failed to save %s", record);
		}
		return successResult(new ModelMap("success", true));
	}

	private void postOperationRecord(OperationRecord record) {
		ProductUtil.setProductId(record.getProductId());
		List<OperationRecord> list = operationRecordService.getOperationRecordListResult(0, 1, new QueryParamMap()
				.addParam("id", record.getId()));
		List<OperationRecordDto> dtoList = convertToDtoList(list);
		findAndFillCommonTagInfo(dtoList);
		fillTagParamParsedResult(dtoList);
		eventBus.post(dtoList.get(0));
	}

	@ResponseBody
	@RequestMapping(value = "/upload/nginx", method = RequestMethod.POST)
	@RequiresPermissions("operation_record:modify")
	public ModelMap uploadNginxLog(MultipartFile file) {
		if(!AuthUtil.isPermitted(Target.Operation_Record.modify())) {
			throw LogReplayException.unauthorizedException("Role[admin] is required!");
		}
		BufferedReader reader = null;
		String line = "";
		int count = 0;
		try {
			reader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
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
					count += operationRecordService.batchSaveOperationRecord(recordList);
					recordList = new ArrayList<OperationRecord>(500);
				}
			}
			if(recordList.size() > 0) {
				count += operationRecordService.batchSaveOperationRecord(recordList);
			}
		} catch (Exception e) {
			logger.error("error happens when upload nginx log", e);
			throw LogReplayException.operationFailedException("Operation Failed while uploading log data!");
		} finally {
			IOUtils.closeQuietly(reader);
		}
		return successResult(new ModelMap("count", count));
	}

}
