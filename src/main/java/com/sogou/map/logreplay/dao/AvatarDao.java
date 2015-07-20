package com.sogou.map.logreplay.dao;

import org.springframework.stereotype.Repository;

import com.sogou.map.logreplay.bean.Avatar;
import com.sogou.map.logreplay.dao.base.AbstractJdbcDaoImpl;
import com.sogou.map.logreplay.dao.base.QueryParamMap;

@Repository
public class AvatarDao extends AbstractJdbcDaoImpl<Avatar> {
	
	public int deleteByUserId(Long userId) {
		String sql = "delete from avatar where user_id = :userId";
		return jdbcTemplate.update(sql, new QueryParamMap().addParam("userId", userId));
	}

}
