package com.sogou.map.logreplay.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.sogou.map.logreplay.bean.PageInfo;
import com.sogou.map.logreplay.bean.Role;
import com.sogou.map.logreplay.bean.TagAction;
import com.sogou.map.logreplay.bean.TagInfo;
import com.sogou.map.logreplay.bean.TagTarget;
import com.sogou.map.logreplay.dao.PageInfoDao;
import com.sogou.map.logreplay.dao.TagActionDao;
import com.sogou.map.logreplay.dao.TagInfoDao;
import com.sogou.map.logreplay.dao.TagTargetDao;
import com.sogou.map.logreplay.dao.base.QueryParamMap;
import com.sogou.map.logreplay.exception.LogReplayException;
import com.sogou.map.logreplay.util.AuthUtil;
import com.sogou.map.mengine.common.service.BaseService;

@Component
@Path("/setup")
public class SetupController extends BaseService {
	
	@Autowired
	private PageInfoDao pageInfoDao;
	
	@Autowired
	private TagInfoDao tagInfoDao;
	
	@Autowired
	private TagTargetDao tagTargetDao;
	
	@Autowired
	private TagActionDao tagActionDao;
	
	@GET
	@Path("/tagAction/import")
	public Response importTagAction(
			@QueryParam("filePath") String filePath) {
		if(!AuthUtil.hasRole(Role.ADMIN)) {
			throw LogReplayException.unauthorizedException("Role[admin] is required!");
		}
		File file = null;
		if(StringUtils.isBlank(filePath) || !(file = new File(filePath)).exists()) {
			throw LogReplayException.invalidParameterException("FilePath is not valid!");
		}
		BeanParser<TagAction> parser = new BeanParser<TagAction>() {
			@Override
			public TagAction parse(String str) {
				if(StringUtils.isBlank(str)) {
					return null;
				}
				TagAction action = new TagAction();
				action.setName(str.trim());
				action.setEnabled(true);
				return action;
			}
		};
		List<TagAction> tagActionList = parseBeanList(file, parser);
		tagActionDao.batchSave(tagActionList, 20);
		return successResultToJson("success", true);
	}
	
	@GET
	@Path("/tagTarget/import")
	public Response importTagTarget(
			@QueryParam("filePath") String filePath) {
		if(!AuthUtil.hasRole(Role.ADMIN)) {
			throw LogReplayException.unauthorizedException("Role[admin] is required!");
		}
		File file = null;
		if(StringUtils.isBlank(filePath) || !(file = new File(filePath)).exists()) {
			throw LogReplayException.invalidParameterException("FilePath is not valid!");
		}
		BeanParser<TagTarget> parser = new BeanParser<TagTarget>() {
			@Override
			public TagTarget parse(String str) {
				if(StringUtils.isBlank(str)) {
					return null;
				}
				TagTarget target = new TagTarget();
				target.setName(str.trim());
				target.setEnabled(true);
				return target;
			}
		};
		List<TagTarget> tagTargetList = parseBeanList(file, parser);
		tagTargetDao.batchSave(tagTargetList, 20);
		return successResultToJson("success", true);
	}
	
	@GET
	@Path("/pageInfo/import")
	public Response importPageInfo(
			@QueryParam("filePath") String filePath) {
		if(!AuthUtil.hasRole(Role.ADMIN)) {
			throw LogReplayException.unauthorizedException("Role[admin] is required!");
		}
		File file = null;
		if(StringUtils.isBlank(filePath) || !(file = new File(filePath)).exists()) {
			throw LogReplayException.invalidParameterException("FilePath is not valid!");
		}
		final Timestamp ts = new Timestamp(System.currentTimeMillis());
		BeanParser<PageInfo> parser = new BeanParser<PageInfo> () {
			@Override
			public PageInfo parse(String str) {
				if(StringUtils.isBlank(str)) {
					return null;
				}
				String[] arr = str.split("\\s");
				if(arr == null || arr.length < 2) {
					return null;
				}
				PageInfo pageInfo = new PageInfo();
				pageInfo.setPageNo(NumberUtils.toInt(arr[0]));
				pageInfo.setName(arr[1]);
				pageInfo.setCreateTime(ts);
				pageInfo.setUpdateTime(ts);
				return pageInfo;
			}
		};
		List<PageInfo> pageInfoList = parseBeanList(file, parser);
		pageInfoDao.batchSave(pageInfoList, 50);
		return successResultToJson("success", true);
	}
	
	@GET
	@Path("/commonTagInfo/import")
	public Response importCommonTagInfo(
			@QueryParam("filePath") String filePath) {
		if(!AuthUtil.hasRole(Role.ADMIN)) {
			throw LogReplayException.unauthorizedException("Role[admin] is required!");
		}
		File file = null;
		if(StringUtils.isBlank(filePath) || !(file = new File(filePath)).exists()) {
			throw LogReplayException.invalidParameterException("FilePath is not valid!");
		}
		final Map<String, TagAction> actionMap = Maps.uniqueIndex(tagActionDao.list(QueryParamMap.EMPTY_MAP),
				new Function<TagAction, String>() {
			@Override
			public String apply(TagAction tagAction) {
				return tagAction.getName();
			}
		});
		final Map<String, TagTarget> targetMap = Maps.uniqueIndex(tagTargetDao.list(QueryParamMap.EMPTY_MAP), 
				new Function<TagTarget, String>() {
			@Override
			public String apply(TagTarget tagTarget) {
				return tagTarget.getName();
			}
		});
		final Timestamp ts = new Timestamp(System.currentTimeMillis());
		BeanParser<TagInfo> parser = new BeanParser<TagInfo>() {
			@Override
			public TagInfo parse(String str) {
				if(StringUtils.isBlank(str)) {
					return null;
				}
				String[] arr = str.split("\\s");
				if(arr == null || arr.length < 4) {
					return null;
				}
				TagInfo tagInfo = new TagInfo();
				tagInfo.setTagNo(NumberUtils.toInt(arr[0]));
				tagInfo.setName(arr[1]);
				TagAction action = actionMap.get(arr[2].trim());
				if(action != null) {
					tagInfo.setActionId(action.getId());
				}
				TagTarget target = targetMap.get(arr[3].trim());
				if(target != null) {
					tagInfo.setTargetId(target.getId());
				}
				tagInfo.setCreateTime(ts);
				tagInfo.setUpdateTime(ts);
				return tagInfo;
			}
		};
		List<TagInfo> tagInfoList = parseBeanList(file, parser);
		tagInfoDao.batchSave(tagInfoList, 100);
		return successResultToJson("success", true);
	}
	
	@GET
	@Path("/tagInfo/import")
	public Response importTagInfo(
			@QueryParam("filePath") String filePath) {
		if(!AuthUtil.hasRole(Role.ADMIN)) {
			throw LogReplayException.unauthorizedException("Role[admin] is required!");
		}
		File file = null;
		if(StringUtils.isBlank(filePath) || !(file = new File(filePath)).exists()) {
			throw LogReplayException.invalidParameterException("FilePath is not valid!");
		}
		final Map<String, TagAction> actionMap = Maps.uniqueIndex(tagActionDao.list(QueryParamMap.EMPTY_MAP),
				new Function<TagAction, String>() {
			@Override
			public String apply(TagAction tagAction) {
				return tagAction.getName();
			}
		});
		final Map<String, TagTarget> targetMap = Maps.uniqueIndex(tagTargetDao.list(QueryParamMap.EMPTY_MAP), 
				new Function<TagTarget, String>() {
			@Override
			public String apply(TagTarget tagTarget) {
				return tagTarget.getName();
			}
		});
		final Map<Integer, PageInfo> pageInfoMap = Maps.uniqueIndex(pageInfoDao.list(QueryParamMap.EMPTY_MAP), 
				new Function<PageInfo, Integer>() {
			@Override
			public Integer apply(PageInfo pageInfo) {
				return pageInfo.getPageNo();
			}
		});
		final Timestamp ts = new Timestamp(System.currentTimeMillis());
		BeanParser<TagInfo> parser = new BeanParser<TagInfo>() {
			@Override
			public TagInfo parse(String str) {
				if(StringUtils.isBlank(str)) {
					return null;
				}
				String[] arr = str.split("\\s");
				if(arr == null || arr.length < 6) {
					return null;
				}
				TagInfo tagInfo = new TagInfo();
				Integer pageNo = NumberUtils.toInt(arr[0]);
				if(pageNo > 0) {
					tagInfo.setPageNo(pageNo);
				}
				tagInfo.setTagNo(NumberUtils.toInt(arr[2]));
				tagInfo.setName(arr[3]);
				PageInfo pageInfo = pageInfoMap.get(tagInfo.getPageNo());
				if(pageInfo != null) {
					tagInfo.setPageInfoId(pageInfo.getId());
				}
				TagAction action = actionMap.get(arr[4].trim());
				if(action != null) {
					tagInfo.setActionId(action.getId());
				}
				TagTarget target = targetMap.get(arr[5].trim());
				if(target != null) {
					tagInfo.setTargetId(target.getId());
				}
				tagInfo.setCreateTime(ts);
				tagInfo.setUpdateTime(ts);
				return tagInfo;
			}
		};
		List<TagInfo> tagInfoList = parseBeanList(file, parser);
		tagInfoDao.batchSave(tagInfoList, 100);
		return successResultToJson("success", true);
	}
	
	static interface BeanParser<B> {
		B parse(String str);
	}
	
	private <B> List<B> parseBeanList(File file, BeanParser<B> parser) {
		BufferedReader reader = null;
		List<B> list = new ArrayList<B>();
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = "";
			while((line = reader.readLine()) != null) {
				B bean = parser.parse(line);
				if(bean == null) {
					continue;
				}
				list.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(reader);
		}
		return list;
	}
	
}
