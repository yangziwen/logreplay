package com.sogou.map.logreplay.mappers;

import java.util.List;
import java.util.Map;

import com.sogou.map.logreplay.bean.TagParam;

public interface TagParamWithInfosMapper {
	
	List<TagParam> list(Map<String, Object> params);
	
}
