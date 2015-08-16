package com.sogou.map.logreplay.mappers;

import java.util.List;
import java.util.Map;

import com.sogou.map.logreplay.bean.UserWithRoles;

public interface UserWithRolesMapper {

	public UserWithRoles getById(Long id);
	
	public List<UserWithRoles> list(Map<String, Object> params);
	
	public int count(Map<String, Object> params);
	
}
