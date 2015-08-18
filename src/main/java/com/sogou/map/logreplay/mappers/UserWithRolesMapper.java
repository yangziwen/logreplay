package com.sogou.map.logreplay.mappers;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;

import com.sogou.map.logreplay.bean.UserWithRoles;

public interface UserWithRolesMapper {

	public UserWithRoles getById(Long id);
	
	public List<UserWithRoles> list(Map<String, Object> params);
	
	public List<UserWithRoles> list(Map<String, Object> params, RowBounds rowBounds);
	
	public int count(Map<String, Object> params);
	
}
