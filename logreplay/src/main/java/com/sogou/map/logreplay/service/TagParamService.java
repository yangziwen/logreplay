package com.sogou.map.logreplay.service;

import java.util.List;

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
