package com.sogou.map.logreplay.mappers;

import java.util.List;
import java.util.Map;

import com.sogou.map.logreplay.bean.Permission;

public interface PermissionMapper {

	public List<Permission> list(Map<String, Object> params);
	
}
