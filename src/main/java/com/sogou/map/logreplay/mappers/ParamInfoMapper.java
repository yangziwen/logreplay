package com.sogou.map.logreplay.mappers;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;

import com.sogou.map.logreplay.bean.ParamInfo;

public interface ParamInfoMapper {

	public int batchSave(List<ParamInfo> list);
	
	public int batchDeleteByIds(List<Long> ids);
	
	public int batchUpdate(List<ParamInfo> list);
	
	public List<ParamInfo> list(Map<String, Object> params);
	
	public List<ParamInfo> list(Map<String, Object> params, RowBounds rowBounds);
	
}
