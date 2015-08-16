package com.sogou.map.logreplay.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Delete;

import com.sogou.map.logreplay.bean.UserRelRole;

public interface UserRelRoleMapper {

	@Delete("delete from user_rel_role where user_id = #{userId}")
	public void deleteUserRelRolesByUserId(Long userId);
	
	public int batchSave(List<UserRelRole> list);
	
}
