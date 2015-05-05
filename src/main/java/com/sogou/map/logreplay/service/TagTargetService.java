package com.sogou.map.logreplay.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sogou.map.logreplay.bean.TagTarget;
import com.sogou.map.logreplay.dao.TagTargetDao;
import com.sogou.map.logreplay.dao.base.QueryParamMap;

@Service
public class TagTargetService {

	@Autowired
	private TagTargetDao tagTargetDao;
	
	public List<TagTarget> getTagTargetListResult() {
		return tagTargetDao.list(QueryParamMap.emptyMap());
	}
}
