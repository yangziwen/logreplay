package com.sogou.map.logreplay.mappers;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;

import com.sogou.map.logreplay.bean.Permission;

public interface PermissionMapper {

	public List<Permission> list(Map<String, Object> params);
	
	public List<Permission> list(Map<String, Object> params, RowBounds rowBounds);
	
}
