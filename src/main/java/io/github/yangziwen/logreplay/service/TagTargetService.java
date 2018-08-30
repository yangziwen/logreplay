package io.github.yangziwen.logreplay.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.yangziwen.logreplay.bean.TagTarget;
import io.github.yangziwen.logreplay.dao.TagTargetDao;
import io.github.yangziwen.logreplay.dao.base.QueryParamMap;

@Service
public class TagTargetService {

	@Autowired
	private TagTargetDao tagTargetDao;
	
	public List<TagTarget> getTagTargetListResult() {
		return tagTargetDao.list(QueryParamMap.emptyMap());
	}
}
