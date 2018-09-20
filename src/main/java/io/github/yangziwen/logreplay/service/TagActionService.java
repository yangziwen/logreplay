package io.github.yangziwen.logreplay.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.yangziwen.logreplay.bean.TagAction;
import io.github.yangziwen.logreplay.dao.TagActionDao;
import io.github.yangziwen.logreplay.dao.base.QueryParamMap;

@Component
public class TagActionService {
	
	@Autowired
	private TagActionDao tagActionDao;

	public List<TagAction> getTagActionListResult() {
		return tagActionDao.list(QueryParamMap.emptyMap());
	}
	
}
