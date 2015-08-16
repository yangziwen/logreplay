package com.sogou.map.logreplay.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sogou.map.logreplay.bean.TagAction;
import com.sogou.map.logreplay.mappers.TagActionMapper;

@Component
public class TagActionService {
	
	@Autowired
	private TagActionMapper tagActionMapper;

	public List<TagAction> getTagActionListResult() {
		return tagActionMapper.list();
	}
	
}
