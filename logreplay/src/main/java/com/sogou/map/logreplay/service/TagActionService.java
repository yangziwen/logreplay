package com.sogou.map.logreplay.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sogou.map.logreplay.bean.TagAction;
import com.sogou.map.logreplay.dao.TagActionDao;
import com.sogou.map.logreplay.dao.base.QueryParamMap;

@Component
public class TagActionService {
	
	@Autowired
	private TagActionDao tagActionDao;

	public List<TagAction> getTagActionListResult() {
		return tagActionDao.list(QueryParamMap.emptyMap());
	}
	
}
