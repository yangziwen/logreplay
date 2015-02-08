package com.sogou.map.logreplay.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sogou.map.logreplay.bean.TagTarget;
import com.sogou.map.logreplay.dao.TagTargetDao;

@Service
public class TagTargetService {

	@Autowired
	private TagTargetDao tagTargetDao;
	
	public List<TagTarget> getTagTargetListResult(Map<String, Object> param) {
		return tagTargetDao.list(param);
	}
}
