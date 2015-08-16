package com.sogou.map.logreplay.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sogou.map.logreplay.bean.TagTarget;
import com.sogou.map.logreplay.mappers.TagTargetMapper;

@Service
public class TagTargetService {

	@Autowired
	private TagTargetMapper tagTargetMapper;
	
	public List<TagTarget> getTagTargetListResult() {
		return tagTargetMapper.list();
	}
}
