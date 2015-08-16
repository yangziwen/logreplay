package com.sogou.map.logreplay.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.sogou.map.logreplay.bean.TagTarget;

public interface TagTargetMapper {

	@Select("select * from tag_target")
	public List<TagTarget> list();
	
}
