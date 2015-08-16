package com.sogou.map.logreplay.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sogou.map.logreplay.bean.ParamInfo;
import com.sogou.map.logreplay.bean.TagParam;
import com.sogou.map.logreplay.dao.TagParamDao;
import com.sogou.map.logreplay.dao.TagParamWithInfosDao;
import com.sogou.map.logreplay.dao.base.QueryParamMap;
import com.sogou.map.logreplay.mappers.ParamInfoMapper;
import com.sogou.map.logreplay.util.TagParamParser;

@Service
public class TagParamService {

	@Autowired
	private TagParamDao tagParamDao;
	
	@Autowired
	private ParamInfoMapper paramInfoMapper;
	
	@Autowired
	private TagParamWithInfosDao tagParamWithInfosDao;
	
	public List<TagParam> getTagParamListResultWithInfos(Map<String, Object> params) {
		return tagParamWithInfosDao.list(params);
	}
	
	public TagParamParser getTagParamParserByTagInfoIdList(List<Long> tagInfoIdList) {
		TagParamParser parser = new TagParamParser();
		if(CollectionUtils.isEmpty(tagInfoIdList)) {
			return parser;
		}
		List<TagParam> tagParamList = getTagParamListResultWithInfos(new QueryParamMap()
			.addParam("tagInfoId__in", tagInfoIdList)
		);
		for(TagParam tagParam: tagParamList) {
			for(ParamInfo paramInfo: tagParam.getParamInfoList()) {
				parser.addParamInfo(tagParam.getTagInfoId(), paramInfo);
			}
		}
		return parser;
	}
	
	/**
	 * tagParam和paramInfo的增删改
	 */
	@Transactional
	public void renewTagParamAndParamInfo(TagParam tagParam, List<ParamInfo> paramInfoList) {
		if(CollectionUtils.isEmpty(paramInfoList) && StringUtils.isBlank(tagParam.getComment())) {
			paramInfoMapper.batchDeleteByIds(new ArrayList<Long>(collectParamInfoId(tagParam.getParamInfoList())));
			if(tagParam.getId() != null) {
				tagParamDao.delete(tagParam);
			}
			return;
		}
		tagParamDao.saveOrUpdate(tagParam);
		for(ParamInfo paramInfo: paramInfoList) {
			paramInfo.setTagParamId(tagParam.getId());
		}
		List<ParamInfo> toSaveParamInfoList = extractToSaveParamInfoList(paramInfoList);
		List<ParamInfo> toUpdateParamInfoList = extractToUpdateParamInfoList(tagParam, paramInfoList);
		List<ParamInfo> toDeleteParamInfoList = extractToDeleteParamInfoList(tagParam, paramInfoList);
		if(CollectionUtils.isNotEmpty(toSaveParamInfoList)) {
			paramInfoMapper.batchSave(toSaveParamInfoList);
		}
		if(CollectionUtils.isNotEmpty(toUpdateParamInfoList)) {
			paramInfoMapper.batchUpdate(toUpdateParamInfoList);
		}
		if(CollectionUtils.isNotEmpty(toDeleteParamInfoList)) {
			paramInfoMapper.batchDeleteByIds(new ArrayList<Long>(collectParamInfoId(toDeleteParamInfoList)));
		}
	}
	
	private List<ParamInfo> extractToSaveParamInfoList(List<ParamInfo> paramInfoList) {
		if(CollectionUtils.isEmpty(paramInfoList)) {
			return Collections.emptyList();
		}
		List<ParamInfo> toSaveList = new ArrayList<ParamInfo>();
		for(ParamInfo paramInfo: paramInfoList) {
			if(paramInfo == null || paramInfo.getId() != null) {
				continue;
			}
			toSaveList.add(paramInfo);
		}
		return toSaveList;
	}
	
	private List<ParamInfo> extractToUpdateParamInfoList(TagParam tagParam, List<ParamInfo> paramInfoList) {
		if(CollectionUtils.isEmpty(tagParam.getParamInfoList())) {
			return Collections.emptyList();
		}
		Set<Long> existedIdSet = collectParamInfoId(tagParam.getParamInfoList());
		List<ParamInfo> toUpdateList = new ArrayList<ParamInfo>();
		for(ParamInfo paramInfo: paramInfoList) {
			if(paramInfo == null || !existedIdSet.contains(paramInfo.getId())) {
				continue;
			}
			toUpdateList.add(paramInfo);
		}
		return toUpdateList;
	}
	
	private List<ParamInfo> extractToDeleteParamInfoList(TagParam tagParam, List<ParamInfo> paramInfoList) {
		if(CollectionUtils.isEmpty(tagParam.getParamInfoList())) {
			return Collections.emptyList();
		}
		if(CollectionUtils.isEmpty(paramInfoList)) {
			return tagParam.getParamInfoList();
		}
		Set<Long> survivedIdSet = collectParamInfoId(paramInfoList);
		List<ParamInfo> toDeleteList = new ArrayList<ParamInfo>();
		for(ParamInfo paramInfo: tagParam.getParamInfoList()) {
			if(paramInfo == null || survivedIdSet.contains(paramInfo.getId())) {
				continue;
			}
			toDeleteList.add(paramInfo);
		}
		return toDeleteList;
	}
	
	private Set<Long> collectParamInfoId(List<ParamInfo> paramInfoList) {
		if(CollectionUtils.isEmpty(paramInfoList)) {
			return Collections.emptySet();
		}
		Set<Long> existedIdSet = new HashSet<Long>();
		for(ParamInfo paramInfo: paramInfoList) {
			if(paramInfo == null || paramInfo.getId() == null) {
				continue;
			}
			existedIdSet.add(paramInfo.getId());
		}
		return existedIdSet;
	}
	
	public void saveOrUpdateTagParam(TagParam tagParam) {
		tagParamDao.saveOrUpdate(tagParam);
	}
	
	public TagParam getTagParamByTagInfoId(Long tagInfoId) {
		TagParam tagParam = tagParamDao.first(new QueryParamMap().addParam("tagInfoId", tagInfoId));
		if(tagParam != null) {
			tagParam.setParamInfoList(getParamInfoListResultByTagParamId(tagParam.getId()));
		}
		return tagParam;
	}
	
	public List<TagParam> getTagParamListResult(Map<String, Object> params) {
		return tagParamDao.list(params);
	}
	
	public List<ParamInfo> getParamInfoListResultByTagParamId(Long tagParamId) {
		return paramInfoMapper.list(new QueryParamMap().addParam("tagParamId", tagParamId).orderByAsc("name").orderByAsc("value")); 
	}
}
