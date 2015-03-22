package com.sogou.map.logreplay.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sogou.map.logreplay.bean.TagAction;
import com.sogou.map.logreplay.dao.TagActionDao;

@Component
public class TagActionService {
	
	@Autowired
	private TagActionDao tagActionDao;

	public List<TagAction> getTagActionListResult(Map<String, Object> params) {
		return tagActionDao.list(params) ;
	}
}
