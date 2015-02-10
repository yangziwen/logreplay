package com.sogou.map.logreplay.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sogou.map.logreplay.bean.ParamInfo;
import com.sogou.map.logreplay.bean.TagParam;
import com.sogou.map.logreplay.dao.ParamInfoDao;
import com.sogou.map.logreplay.dao.TagParamDao;
import com.sogou.map.logreplay.dao.base.QueryParamMap;

@Service
public class TagParamService {

	@Autowired
	private TagParamDao tagParamDao;
	
	@Autowired
	private ParamInfoDao paramInfoDao;
	
	/**
	 * paramInfo是否应该直接用json保存？
	 * 有些纠结
	 */
	public void renewTagParamAndParamInfo(TagParam tagParam, List<ParamInfo> paramInfoList) {
		tagParamDao.saveOrUpdate(tagParam);
		for(ParamInfo paramInfo: paramInfoList) {
			paramInfo.setTagParamId(tagParam.getId());
		}
		List<ParamInfo> toSaveParamInfoList = extractToSaveParamInfoList(paramInfoList);
		List<ParamInfo> toUpdateParamInfoList = extractToUpdateParamInfoList(tagParam, paramInfoList);
		List<ParamInfo> toDeleteParamInfoList = extractToDeleteParamInfoList(tagParam, paramInfoList);
		paramInfoDao.batchSave(toSaveParamInfoList.toArray(new ParamInfo[]{}), 100);
		paramInfoDao.batchUpdate(toUpdateParamInfoList.toArray(new ParamInfo[]{}), 100);
		paramInfoDao.batchDeleteByIds(collectParamInfoId(toDeleteParamInfoList));
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
	
	public List<ParamInfo> getParamInfoListResultByTagParamId(Long tagParamId) {
		return paramInfoDao.list(new QueryParamMap().addParam("tagParamId", tagParamId).orderByAsc("name").orderByAsc("value")); 
	}
}
