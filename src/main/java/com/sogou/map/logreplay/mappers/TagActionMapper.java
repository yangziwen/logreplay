package com.sogou.map.logreplay.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.sogou.map.logreplay.bean.TagAction;

public interface TagActionMapper {

	@Select("select * from tag_action")
	public List<TagAction> list();
	
}
